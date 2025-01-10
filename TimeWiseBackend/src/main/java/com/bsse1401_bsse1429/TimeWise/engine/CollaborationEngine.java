package com.bsse1401_bsse1429.TimeWise.engine;

import com.bsse1401_bsse1429.TimeWise.model.*;
import com.bsse1401_bsse1429.TimeWise.repository.NotificationRepository;
import com.bsse1401_bsse1429.TimeWise.repository.TaskRepository;
import com.bsse1401_bsse1429.TimeWise.repository.TeamRepository;
import com.bsse1401_bsse1429.TimeWise.repository.UserRepository;
import com.bsse1401_bsse1429.TimeWise.utils.UserCredentials;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class CollaborationEngine {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepositoryInstance;

    @Autowired
    private JavaMailSender mailSenderInstance;

    private static UserRepository userRepository;
    private static JavaMailSender mailSender;

    @PostConstruct
    private void initStaticDependencies() {
        userRepository = userRepositoryInstance;
        mailSender = mailSenderInstance;
    }

    // Main method to send notifications
    public String sendNotification(String entityName, String notificationSubject, String recipient, String messageContent) {
        String sender = UserCredentials.getCurrentUsername();
        Notification notification = null;

        switch (notificationSubject) {
            case "TEAM_INVITATION":
            case "TASK_INVITATION":
                notification = createNotification(sender, notificationSubject, entityName, recipient,
                        sender + " invited you to join the " + (notificationSubject.equals("TEAM_INVITATION") ? "team " : "task ") + entityName);
                handleInvitation(entityName, sender, recipient, notificationSubject);
                break;

            case "ASK_TO_JOIN_TEAM":
            case "ASK_TO_JOIN_TASK":
                notification = createNotification(sender, notificationSubject, entityName, recipient,
                        sender + " requested to join the " + (notificationSubject.equals("ASK_TO_JOIN_TEAM") ? "team " : "task ") + entityName + ". Message: " + messageContent);
                break;

            case "TEAM_MESSAGE":
                notification = createTeamNotification(sender, entityName, "Message from " + sender + " to team " + entityName + ": " + messageContent);
                break;

            case "USER_MESSAGE":
                notification = createNotification(sender, notificationSubject, null, recipient,
                        "Message from " + sender + " to you: " + messageContent);
                break;

            case "SYSTEM_MESSAGE":
                notification = createNotification(sender, notificationSubject, null, sender, "System alert: " + messageContent);
                break;

            default:
                throw new IllegalArgumentException("Invalid notification subject");
        }

        notificationRepository.save(notification);
        return sendEmail(notification);
    }

    public String handleResponse(String entityName, String notificationSubject, String entityOwner, String response) {
        String respondedBy = UserCredentials.getCurrentUsername();
        Notification notification = createNotification(respondedBy, notificationSubject, entityName, entityOwner,
                "Response: " + response);

        switch (notificationSubject) {
            case "REPLY_TO_TEAM_INVITATION":
                handleInvitationResponse(entityName, respondedBy, entityOwner, response, true);
                break;

            case "REPLY_TO_TASK_INVITATION":
                handleInvitationResponse(entityName, respondedBy, entityOwner, response, false);
                break;


            case "REPLY_TO_JOIN_TEAM_REQUEST":
            case "REPLY_TO_JOIN_TASK_REQUEST":
                handleJoinRequestResponse(entityName, respondedBy, entityOwner, response,
                        notificationSubject.equals("REPLY_TO_JOIN_TEAM_REQUEST"));
                break;

            default:
                throw new IllegalArgumentException("Invalid notification subject");
        }

        notificationRepository.save(notification);
        return sendEmail(notification);
    }

    private Notification createNotification(String sender, String subject, String entityName, String recipient, String messageContent) {
        Notification notification = new Notification();
        notification.setSender(sender);
        notification.setNotificationSubject(subject);
        notification.setNotificationMessage(messageContent);
        notification.setEntityNameRelatedToNotification(entityName);
        notification.setRecipients(Collections.singleton(recipient));
        notification.setNotificationStatus("Unseen");
        notification.setTimeStamp(new Date());
        return notification;
    }

    private Notification createTeamNotification(String sender, String teamName, String messageContent) {
        Team team = teamRepository.findByTeamName(teamName);
        if (team == null) {
            throw new IllegalArgumentException("Team not found.");
        }
        Notification notification = createNotification(sender, "TEAM_MESSAGE", teamName, null, messageContent);
        notification.setRecipients(new HashSet<>(team.getTeamMembers()));
        return notification;
    }

    private void handleInvitation(String entityName, String sender, String recipient, String subject) {
        if ("TEAM_INVITATION".equals(subject)) {
            Team team = teamRepository.findByTeamNameAndTeamOwner(entityName, sender);
            if (team == null) {
                throw new IllegalArgumentException("Team not found.");
            }
            team.getInvitedMembers().add(recipient);
            teamRepository.save(team);
        } else if ("TASK_INVITATION".equals(subject)) {
            Task task = taskRepository.findByTaskNameAndTaskOwner(entityName, sender);
            if (task == null) {
                throw new IllegalArgumentException("Task not found.");
            }
            task.getInvitedMembers().add(recipient);
            taskRepository.save(task);
        }
    }
    private void handleInvitationResponse(String entityName, String respondedBy, String entityOwner, String response, boolean isTeam) {
        if (isTeam) {
            Team team = teamRepository.findByTeamNameAndTeamOwner(entityName, entityOwner);
            processInvitationResponse(team, respondedBy, response, true);
        } else {
            Task task = taskRepository.findByTaskNameAndTaskOwner(entityName, entityOwner);
            processInvitationResponse(task, respondedBy, response, false);
        }
    }

    private <T> void processInvitationResponse(T entity, String respondedBy, String response, boolean isTeam) {
        if (isTeam && entity instanceof Team team) {
            modifyInvitationResponse(team.getInvitedMembers(), team.getTeamMembers(), respondedBy, response);
            teamRepository.save(team);
        } else if (!isTeam && entity instanceof Task task) {
            modifyInvitationResponse(task.getInvitedMembers(), task.getTaskParticipants(), respondedBy, response);
            taskRepository.save(task);
        }
    }

    private void modifyInvitationResponse(Set<String> invitedMembers, Set<String> members, String respondedBy, String response) {
        invitedMembers.remove(respondedBy);
        if ("accept".equalsIgnoreCase(response)) {
            members.add(respondedBy);
        }
    }

    private void handleJoinRequestResponse(String entityName, String respondedBy, String entityOwner, String response, boolean isTeam) {
        if (isTeam) {
            Team team = teamRepository.findByTeamNameAndTeamOwner(entityName, entityOwner);
            processJoinResponse(team, respondedBy, response, true);
        } else {
            Task task = taskRepository.findByTaskNameAndTaskOwner(entityName, entityOwner);
            processJoinResponse(task, respondedBy, response, false);
        }
    }

    private <T> void processJoinResponse(T entity, String respondedBy, String response, boolean isTeam) {
        if (isTeam && entity instanceof Team team) {
            modifyJoinResponse(team.getMembersRequestedForJoining(), team.getTeamMembers(), respondedBy, response);
            teamRepository.save(team);
        } else if (!isTeam && entity instanceof Task task) {
            modifyJoinResponse(task.getMembersRequestedForJoining(), task.getTaskParticipants(), respondedBy, response);
            taskRepository.save(task);
        }
    }

    private void modifyJoinResponse(Set<String> requested, Set<String> members, String respondedBy, String response) {
        requested.remove(respondedBy);
        if ("accept".equalsIgnoreCase(response)) {
            members.add(respondedBy);
        }
    }

    private String sendEmail(Notification notification) {
        StringBuilder emailStatus = new StringBuilder();
        List<User> recipients = userRepository.findByUserNames(notification.getRecipients());
        User sender = userRepository.findByUserName(notification.getSender());

        if (recipients == null || sender == null || sender.getEmail() == null) {
            throw new IllegalArgumentException("Invalid sender or recipient details.");
        }

        for (User recipient : recipients) {
            if (recipient == null || recipient.getEmail() == null) {
                emailStatus.append("Failed to send email to a recipient with missing email.\n");
                continue;
            }
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(sender.getEmail());
            message.setTo(recipient.getEmail());
            message.setSubject(notification.getNotificationSubject());
            message.setText(notification.getNotificationMessage());
            try {
                mailSender.send(message);
                emailStatus.append("Email sent to ").append(recipient.getUserName()).append("\n");
            } catch (Exception e) {
                emailStatus.append("Failed to send email to ").append(recipient.getUserName()).append(": ").append(e.getMessage()).append("\n");
            }
        }
        return emailStatus.toString();
    }
}
