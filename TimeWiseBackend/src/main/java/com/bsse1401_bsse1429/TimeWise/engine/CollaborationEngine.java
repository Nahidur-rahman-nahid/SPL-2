package com.bsse1401_bsse1429.TimeWise.engine;

import com.bsse1401_bsse1429.TimeWise.model.*;
import com.bsse1401_bsse1429.TimeWise.repository.UserRepository;
//import com.bsse1401_bsse1429.TimeWise.service.NotificationService;
import com.bsse1401_bsse1429.TimeWise.service.TeamService;
import com.bsse1401_bsse1429.TimeWise.service.TaskService;
import com.bsse1401_bsse1429.TimeWise.service.UserService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class CollaborationEngine {

//    @Autowired
//    private NotificationService notificationService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private TaskService taskService;


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;


    /**
     * Sends a team invitation to a user.
     * The team object represents the team sending the invitation.
     * The userName is the name of the user to invite.
     * The invitedBy is the team member sending the invitation.
     */
    public String sendTeamInvitation(Team team, String invitedTo, String invitedBy) {
//        if (!team.getTeamOwner().equals(invitedBy)) {
//            throw new IllegalArgumentException("Only team owner can invite others.");
//        }

        Notification notification = new Notification();
        notification.setSender(invitedBy);
        notification.setRecipient(invitedTo);
        notification.setNotificationSubject("Team Invitation");
        notification.setNotificationMessage("Myself "+invitedBy+" inviting you to join the team: " + team.getTeamName());
        notification.setNotificationStatus("Unseen");
        notification.setTimestamp(new Date());

       return  sendEmail(notification,invitedBy,invitedTo);

       // notificationService.sendNotification(notification);
    }



    // send mail to a user
    public String  sendEmail(Notification notification, String from, String to) {
        // Fetch recipient's email address
        User recipient = userRepository.findByUserName(to);
        if (recipient == null) {
            throw new IllegalArgumentException("Recipient user not found.");
        }
        String recipientEmail = recipient.getEmail();

        // Fetch sender's email address
        User sender = userRepository.findByUserName(from);
        if (sender == null) {
            throw new IllegalArgumentException("Sender user not found.");
        }
        String senderEmail = sender.getEmail();

        // Prepare email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderEmail); // Sender's email address
        message.setTo(recipientEmail); // Recipient's email address
        message.setSubject(notification.getNotificationSubject()); // Subject of the email
        message.setText(notification.getNotificationMessage()); // Body of the email

        // Send the email
        mailSender.send(message);
        return "Email sent successfully from " + senderEmail + " to " + recipientEmail;
    }


    /**
     * Handles acceptance of a team invitation.
     * The teamId identifies the team, and userName is the user accepting the invitation.
     */
//    public void acceptTeamInvitation(String teamId, String userName) {
//        Team team = teamService.getTeamById(teamId);
//        if (team.getTeamMembers().contains(userName)) {
//            throw new IllegalArgumentException("User is already a member of the team.");
//        }
//
//        team.getTeamMembers().add(userName);
//        teamService.updateTeam(team);
//
//        notifyTeamModification(team, "System");
//    }
//
//    /**
//     * Notifies all team members about modifications to the team.
//     * The team object represents the modified team, and modifiedBy is the user making the modification.
//     */
//    public void notifyTeamModification(Team team, String modifiedBy) {
//        String message = "The team '" + team.getTeamName() + "' was updated by " + modifiedBy + ".";
//        for (String member : team.getTeamMembers()) {
//            Notification notification = new Notification();
//            notification.setRecipient(member);
//            notification.setSender("System");
//            notification.setMessage(message);
//            notification.setType("Team Modification");
//            notification.setTimestamp(new Date());
//
//            notificationService.sendNotification(notification);
//        }
//    }
//
//    /**
//     * Assigns a task to a user and notifies them.
//     * The taskId identifies the task, assignedTo is the user assigned the task, and assignedBy is the user assigning the task.
//     */
//    public void assignTaskToUser(String taskId, String assignedTo, String assignedBy) {
//        Task task = taskService.getTaskById(taskId);
//
//        if (!taskService.isUserAuthorizedToAssignTask(assignedBy, task)) {
//            throw new IllegalArgumentException("You are not authorized to assign this task.");
//        }
//
//        task.setAssignedTo(assignedTo);
//        taskService.updateTask(task);
//
//        Notification notification = new Notification();
//        notification.setRecipient(assignedTo);
//        notification.setSender(assignedBy);
//        notification.setMessage("You have been assigned a new task: " + task.getTaskName());
//        notification.setType("Task Assignment");
//        notification.setTimestamp(new Date());
//
//        notificationService.sendNotification(notification);
//    }
//
//    /**
//     * Notifies team members about a new task added to the team.
//     * The task object represents the task, the team object represents the team, and addedBy is the user adding the task.
//     */
//    public void notifyTeamAboutTask(Task task, Team team, String addedBy) {
//        String message = "A new task '" + task.getTaskName() + "' has been added to the team '" + team.getTeamName() + "' by " + addedBy + ".";
//        for (String member : team.getTeamMembers()) {
//            Notification notification = new Notification();
//            notification.setRecipient(member);
//            notification.setSender(addedBy);
//            notification.setMessage(message);
//            notification.setType("Task Addition");
//            notification.setTimestamp(new Date());
//
//            notificationService.sendNotification(notification);
//        }
//    }
//
//    /**
//     * Tracks collaboration logs for auditing.
//     * The action specifies the activity performed (e.g., "Task Assigned"), performedBy is the user performing the action,
//     * and details provides additional context for the action.
//     */
//    public void trackCollaborationLog(String action, String performedBy, String details) {
//        // Example: Store in a database or log file.
//        System.out.println("Collaboration Log: Action - " + action + ", Performed By - " + performedBy + ", Details - " + details);
//        // This could be expanded to store logs in a persistent system.
//    }
//
//    /**
//     * Pushes real-time updates for collaboration.
//     * The message represents the update content, and recipient is the target user for the update.
//     */
//    public void pushRealTimeUpdates(String message, String recipient) {
//        // Logic for real-time updates (e.g., WebSocket, Push Notification).
//        System.out.println("Real-time update sent to " + recipient + ": " + message);
//    }

}

