package com.TimeWise.engine;

import com.TimeWise.model.*;
import com.TimeWise.repository.*;
import com.TimeWise.utils.UserCredentials;
import jakarta.annotation.PostConstruct;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Component
@EnableAsync
public class CollaborationEngine {

    @Autowired
    private NotificationRepository notificationRepositoryInstance;

    @Autowired
    private MessageRepository messageRepositoryInstance;

    @Autowired
    private  TeamRepository teamRepositoryInstance;

    @Autowired
    private  TaskRepository taskRepositoryInstance;

    @Autowired
    private UserRepository userRepositoryInstance;

    @Autowired
    private JavaMailSender mailSenderInstance;

    private static UserRepository userRepository;
    private static JavaMailSender mailSender;
    private static TeamRepository teamRepository;
    private static TaskRepository taskRepository;
    private static NotificationRepository notificationRepository;
    private static MessageRepository messageRepository;

    private static String timeWiseEmail;

    @Value("${timewise.email}")
    private String timeWiseEmailInstance;

    @PostConstruct
    private void initStaticDependencies() {
        userRepository = userRepositoryInstance;
        mailSender = mailSenderInstance;
        teamRepository=teamRepositoryInstance;
        taskRepository=taskRepositoryInstance;
        notificationRepository=notificationRepositoryInstance;
        messageRepository=messageRepositoryInstance;
        timeWiseEmail = timeWiseEmailInstance;

    }



    // Main method to send notifications
    public static String sendNotification(String entityName, String notificationSubject, String recipient, String messageContent) {
        String sender = UserCredentials.getCurrentUsername();
        Notification notification = null;

        switch (notificationSubject) {
            case "TEAM_INVITATION":
            case "TASK_INVITATION":
                handleInvitation(entityName, sender, recipient, notificationSubject);
                notification = createNotification(sender, notificationSubject, entityName, recipient,
                        sender + " invited you to join the " + (notificationSubject.equals("TEAM_INVITATION") ? "team " : "task ") + entityName);
                break;

            case "NEW_TEAM_TASK_ADDED":
            case "A_TEAM_TASK_REMOVED":
                notification = createTeamNotification(sender, entityName, notificationSubject, "Message from "+ sender + " to team " + entityName + ": " + messageContent);
                break;


            case "REPLY_TO_TEAM_INVITATION":
            case "REPLY_TO_TASK_INVITATION":
                boolean isTeam= !notificationSubject.equals("REPLY_TO_TASK_INVITATION");
                handleInvitationResponse(entityName, sender, recipient, messageContent, isTeam);
                notification = createNotification(sender, notificationSubject, entityName, recipient,
                        "The invitation to join "+entityName+"has been " + messageContent+"ed by the recipient.");

                break;
            default:
                throw new IllegalArgumentException("Invalid notification subject");
        }

        sendEmail(notification);

        notificationRepository.save(notification);
        return "Successfully sent notification";
    }
    public static String sendMessage( String sender,String recipient,String messageSubject,String messageDescription) {
        Notification notification=new Notification();
        switch (messageSubject) {
            case "USER_MESSAGE":
                notification = createNotification(sender, messageSubject, null, recipient,
                        "Message from " + sender + " to you: " + messageDescription);
                break;

            case "TIME_WISE_MESSAGE":
                notification = createNotification(sender, messageSubject, null, sender, "Message From TimeWise: " + messageDescription);
                break;

            case "TEAM_MESSAGE":
            notification = createTeamNotification(sender, recipient, messageSubject, "Message from "+ sender + " to team " + recipient + ": " + messageDescription);
            break;
            default:
                throw new IllegalArgumentException("Invalid notification subject");
        }
        Message message=new Message();
        message.setSender(sender);
        message.setRecipients(notification.getRecipients());
        message.setMessageSubject(messageSubject);
        message.setMessageDescription(messageDescription);
        message.setMessageStatus("Unseen");
        message.setTimeStamp(new Date());
        messageRepository.save(message);
        sendEmail(notification);
        return "Successfully sent message";

    }


    private static Notification createNotification(String sender, String subject, String entityName, String recipient, String messageContent) {
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

    private static Notification createTeamNotification(String sender, String teamName,String subject, String messageContent) {
        Team team = teamRepository.findByTeamName(teamName);
        if (team == null) {
            throw new IllegalArgumentException("Team not found.");
        }
        if(!team.getTeamMembers().contains(sender)){
            throw new IllegalArgumentException("Only team members can send a message to particular team");
        }
        Notification notification = createNotification(sender, subject, teamName, null, messageContent);
        notification.setRecipients(new HashSet<>(team.getTeamMembers()));
        return notification;
    }

    private static void handleInvitation(String entityName, String sender, String recipient, String subject) {
        if ("TEAM_INVITATION".equals(subject)) {
            Team team = teamRepository.findByTeamNameAndTeamOwner(entityName, sender);
            if (team == null) {
                throw new IllegalArgumentException("Team not found.");
            }
            if(team.getInvitedMembers().contains(recipient)){
                throw new IllegalArgumentException("You already invited this member to that team");
            }
            if(team.getTeamMembers().contains(recipient)){
                throw new IllegalArgumentException("This member is already a team member");
            }


            team.getInvitedMembers().add(recipient);
            teamRepository.save(team);
        } else if ("TASK_INVITATION".equals(subject)) {
            Task task = taskRepository.findByTaskNameAndTaskOwner(entityName, sender);
            if (task==null) {
                throw new IllegalArgumentException("Task not found.");
            }


                if(task.getInvitedMembers().contains(recipient)){
                    throw new IllegalArgumentException("You already invited this member with that task name");
                }

            task.getInvitedMembers().add(recipient);
            taskRepository.save(task);
        }
    }
    private static void handleInvitationResponse(String entityName, String respondedBy, String entityOwner, String response, boolean isTeam) {
        if (isTeam) {
            Team team = teamRepository.findByTeamName(entityName);
            if (team == null) {
                throw new IllegalArgumentException("Team " + entityName + " not found");
            }
            if (!team.getTeamOwner().equals(entityOwner)) {
                throw new IllegalArgumentException("Team name and team owner mismatch");
            }
            if (!team.getInvitedMembers().contains(respondedBy)) {
                throw new IllegalArgumentException("You are not invited to this team, " + entityName);
            }
            if (team.getTeamMembers().contains(respondedBy)) {
                throw new IllegalArgumentException("You are already a member of this team, " + entityName);
            }

            // If accepted, add user to all team tasks as a participant
            if ("accept".equalsIgnoreCase(response)) {
                List<Task> teamTasks = taskRepository.findByTaskOwnerAndTaskNameIn(team.getTeamOwner(),team.getTeamTasks());
                if(teamTasks.isEmpty()){
                    return ;// no task in the team so the user won't be part of any new task
                }
                // Iterate through all tasks of the team and add the new member as a participant
                for (Task task : teamTasks) {
                    task.getTaskParticipants().add(respondedBy);
                }
                taskRepository.saveAll(teamTasks);

                // Log the new member addition to the team's modification history
                String message = "New member " + respondedBy + " added to the team and now can participate in team tasks.";
                team.getTeamModificationHistories().add(message);
                team.getInvitedMembers().remove(respondedBy);
                team.getTeamMembers().add(respondedBy);
                teamRepository.save(team);
            }
            else if("decline".equalsIgnoreCase(response)){
                team.getInvitedMembers().remove(respondedBy);
                teamRepository.save(team);
            }
            else{
                throw new IllegalArgumentException("Response either by accept or decline");
            }
        } else {
            Task task = taskRepository.findByTaskNameAndTaskOwner(entityName, entityOwner);
            if (task == null) {
                throw new IllegalArgumentException("Task not found");
            }
            if (!task.getInvitedMembers().contains(respondedBy)) {
                throw new IllegalArgumentException("You are not invited to this task");
            }
            if (task.getTaskParticipants().contains(respondedBy)) {
                throw new IllegalArgumentException("You are already a participant in this task");
            }
            if ("accept".equalsIgnoreCase(response)) {
                task.getInvitedMembers().remove(respondedBy);
                task.getTaskParticipants().add(respondedBy);
                taskRepository.save(task);
            }
            else if("decline".equalsIgnoreCase(response)){
                task.getInvitedMembers().remove(respondedBy);
                taskRepository.save(task);
            }
            else{
                throw new IllegalArgumentException("Response either by accept or decline");
            }
        }
    }

    @Async
    public static String sendEmail(Notification notification) {
        StringBuilder emailStatus = new StringBuilder();

        // Find all recipients and sender
        List<User> recipients = userRepository.findByUserNameIn(notification.getRecipients());

        User  sender = userRepository.findByUserName(notification.getSender());

        // Validate sender details
        if (sender == null || sender.getUserEmail() == null) {
            emailStatus.append("Invalid sender details.");
            return emailStatus.toString();
        }
        if(recipients.isEmpty())
        {
            emailStatus.append("No recipients found for the notification. No emails sent.");
            return emailStatus.toString();
        }
        // Identify missing recipients
        Set<String> missingRecipients = new HashSet<>(notification.getRecipients());
        for (User recipient : recipients) {
            missingRecipients.remove(recipient.getUserName());
        }

        // Handle missing recipients
        if (missingRecipients.size() == notification.getRecipients().size()) {
            emailStatus.append("No valid recipients found for the notification. No emails sent.");
            return emailStatus.toString();
        } else if (!missingRecipients.isEmpty()) {
            emailStatus.append("The following recipients were not found in the database: ")
                    .append(String.join(", ", missingRecipients))
                    .append("\n");
        }

        // Process and send emails to valid recipients
        for (User recipient : recipients) {
            if (recipient == null || recipient.getUserEmail() == null) {
                emailStatus.append("Failed to send email to ").append(recipient != null ? recipient.getUserName() : "unknown user")
                        .append(": Missing email address.\n");
                continue;
            }
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom(sender.getUserEmail());
                message.setTo(recipient.getUserEmail());
                message.setSubject(notification.getNotificationSubject());
                message.setText(notification.getNotificationMessage());
                mailSender.send(message);
                emailStatus.append("Email successfully sent to ").append(recipient.getUserName()).append("\n");
            } catch (Exception e) {
                emailStatus.append("Failed to send email to ").append(recipient.getUserName())
                        .append(": ").append(e.getMessage()).append("\n");
            }
        }

        return emailStatus.toString();
    }
    @Async
    public static String sendProgressAndPerformanceReport(String messageBody,String userName,Boolean isProgressReport) {
        User recipient =userRepository.findByUserName(userName);
        if(recipient==null){
           return null;
        }
        if(recipient.getUserEmail()==null){
            return null;
        }
        StringBuilder messageSubject=new StringBuilder();
        if(isProgressReport){
            messageSubject.append("Daily Progress Report Summary for : ").append(userName).append("\n\n");
        }
        else{
            messageSubject.append("Weekly Performance Report Summary for : ").append(userName).append("\n\n");
        }
        StringBuilder emailStatus = new StringBuilder();

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(timeWiseEmail);
            message.setTo(recipient.getUserEmail());
            message.setSubject(messageSubject.toString());
            message.setText(messageBody);
            mailSender.send(message);
            emailStatus.append("Email successfully sent to ").append(recipient.getUserName()).append("\n");
        } catch (Exception e) {
            emailStatus.append("Failed to send email to ").append(recipient.getUserName())
                    .append(": ").append(e.getMessage()).append("\n");
        }
        return emailStatus.toString();

    }



    @Async
    public static String sendUserRegistrationVerificationCode(String receiverName, String receiverEmail,String verificationCode) {

        StringBuilder emailStatus = new StringBuilder();
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom(timeWiseEmail);
                message.setTo(receiverEmail);
                message.setSubject("TimeWise Registration Verification Code");
                message.setText("Your TimeWise account registration verification code is: " + verificationCode + ". It expires in 7 minutes.");
                mailSender.send(message);
                emailStatus.append("Email successfully sent to user name: ").append(receiverName).append(" user email:").append(receiverEmail).append("\n");
            } catch (Exception e) {
                emailStatus.append("Failed to send email to ").append(receiverName).append(" user email: ").append(receiverEmail)
                        .append(": ").append(e.getMessage()).append("\n");
            }
        return emailStatus.toString();
        }

    @Async
    public static String sendRegistrationSuccessfulMessage(String receiverName, String receiverEmail) {
        StringBuilder emailStatus = new StringBuilder();
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(timeWiseEmail);
            message.setTo(receiverEmail);
            message.setSubject("TimeWise Registration Successful");
            message.setText(receiverName+", Welcome to TimeWise! We hope TimeWise will help you be more productive.");
            mailSender.send(message);
            emailStatus.append("Email successfully sent to user name: ").append(receiverName).append(" user email:").append(receiverEmail).append("\n");
        } catch (Exception e) {
            emailStatus.append("Failed to send email to ").append(receiverName).append(" user email: ").append(receiverEmail)
                    .append(": ").append(e.getMessage()).append("\n");
        }
        return emailStatus.toString();
    }

    @Async
    public static String sendAccountVerificationCode( String receiverEmail,String verificationCode) {


        StringBuilder emailStatus = new StringBuilder();
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(timeWiseEmail);
            message.setTo(receiverEmail);
            message.setSubject("TimeWise Account Verification Code");
            message.setText("Your TimeWise account verification code is: " + verificationCode + ". It expires in 7 minutes.");
            mailSender.send(message);
            emailStatus.append("Email successfully sent to user email:").append(receiverEmail).append("\n");
        } catch (Exception e) {
            emailStatus.append("Failed to send email to ").append(" user email: ").append(receiverEmail)
                    .append(": ").append(e.getMessage()).append("\n");
        }
        return emailStatus.toString();
    }


    public static String addTaskToTeam(String userName,String teamName, String taskName) {

        Task task=taskRepository.findByTaskNameAndTaskOwner(taskName,userName);
        if(task==null){
            throw new IllegalArgumentException("Task not found.");
        }
        Team team=teamRepository.findByTeamName(teamName);
        if(team==null){
            throw new IllegalArgumentException("Team not found.");
        }
        if(!team.getTeamOwner().equals(userName)){
            throw new IllegalArgumentException("Only team owner can add task to team");
        }
        if(!task.getTaskOwner().equals(userName)){
            throw new IllegalArgumentException("You are not the task owner and non owned task can not be added to team");
        }
        if(team.getTeamTasks().contains(taskName)){
            throw new IllegalArgumentException("This team already contains a task with same name");

        }
        Set<String> teamMembers=team.getTeamMembers();
        task.getTaskParticipants().addAll(teamMembers);
        taskRepository.save(task);
        String message="A new task named "+task.getTaskName()+"is added to the team.";
        team.getTeamModificationHistories().add(message);
        team.getTeamTasks().add(task.getTaskName());
        teamRepository.save(team);
        sendNotification(teamName,"NEW_TEAM_TASK_ADDED",null,message);

        return "task added to team";

    }

    public static String removeTaskFromTeam(String userName,String teamName, ObjectId taskId) {
        Task task = taskRepository.findByTaskId(taskId);
        if (task == null) {
            throw new IllegalArgumentException("Task not found.");
        }
        Team team = teamRepository.findByTeamName(teamName);
        if (team == null) {
            throw new IllegalArgumentException("Team not found.");
        }
        if (!team.getTeamOwner().equals(userName)) {
            throw new IllegalArgumentException("Only team owner can remove task from team");
        }
        if(!task.getTaskOwner().equals(userName)){
            throw new IllegalArgumentException("You are not the task owner and non owned task can not be removed from team");
        }
        Set<String> teamMembers=team.getTeamMembers();
        task.getTaskParticipants().removeAll(teamMembers);
        task.getTaskParticipants().add(userName);
        taskRepository.save(task);
        String message="A task named "+task.getTaskName()+"has been removed from the team.";
        team.getTeamModificationHistories().add(message);
        team.getTeamTasks().remove(task.getTaskName());
        teamRepository.save(team);
        sendNotification(teamName,"A_TEAM_TASK_REMOVED",null,message);
        return "task removed from team";
    }

    public static String removeDeletedTaskFromTeams(String taskName,String userName){
        Task task = taskRepository.findByTaskNameAndTaskOwner(taskName,userName);
        if (task==null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found with Name: " + taskName);
        } else if (!task.getTaskOwner().equals(userName)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not the owner of the owner of this task");
        }
        Set<String> taskParticipants=task.getTaskParticipants();
        String message="The task owned by "+userName+" with task name: '" + task.getTaskName()+"', task goal : '"+task.getTaskGoal()+"' is deleted by the owner : "+userName;
        List<Team> teamsHavingTasks=teamRepository.findByTeamOwnerAndTeamTasksContaining(userName,taskName);
        if(!teamsHavingTasks.isEmpty()) {
            for (Team team : teamsHavingTasks) {
                team.getTeamTasks().remove(taskName);
                team.getTeamModificationHistories().add("A task named "+task.getTaskName()+"has been deleted and hence removed from the team.");
                Set<String> teamMembers=team.getTeamMembers();
                sendNotification(team.getTeamName(),"A_TEAM_TASK_REMOVED",null, message+"and hence removed from the team.");
                taskParticipants.removeAll(teamMembers);
                task.getTaskParticipants().removeAll(teamMembers);

            }
            teamRepository.saveAll(teamsHavingTasks);
        }
        if(!taskParticipants.isEmpty()){
            Notification notification = createNotification(task.getTaskOwner(), "TASK_DELETED", String.valueOf(task.getTaskId()), null, message);
            notification.setRecipients(new HashSet<>(taskParticipants));
            sendEmail(notification);
            notificationRepository.save(notification);
            task.getTaskParticipants().removeAll(taskParticipants);
        }
        taskRepository.save(task);

        return "Task removed from the team";

    }

    public static String  removeMemberFromTeam(String teamOwner,String teamName, String userName) {
        Team team=teamRepository.findByTeamName(teamName);
        if(team==null){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Team not found");
        }
        if(!team.getTeamOwner().equals(teamOwner)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not the team owner");
        }
        if(!team.getTeamMembers().contains(userName)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not team member");
        }
        team.getTeamMembers().remove(userName);
        String message="A team member "+userName+"has been removed from the team.";
        team.getTeamModificationHistories().add(message);
        Notification notification=createNotification(teamOwner,"YOU_GOT_REMOVED_FROM_TEAM",teamName,userName,"You have been removed from the team "+teamName);
        List<Task> tasks=taskRepository.findByTaskOwnerAndTaskNameIn(teamOwner,team.getTeamTasks());
        if(!tasks.isEmpty()){
            for(Task task:tasks){
                task.getTaskParticipants().remove(userName);
            }
        }
        taskRepository.saveAll(tasks);
        sendEmail(notification);
        notificationRepository.save(notification);
        team.getTeamMembers().remove(userName);
        teamRepository.save(team);

        return "User removed from the team";

    }
    public static String  leaveTeam(String teamName, String userName) {
        Team team=teamRepository.findByTeamName(teamName);
        if(team==null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Team not found");
        }

        if(!team.getTeamMembers().contains(userName)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not a team member");
        }
        if(team.getTeamOwner().equals(userName)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You are the owner of the team and can not leave the team");
        }
        team.getTeamMembers().remove(userName);
        String message="A team member "+userName+"has left the team.";
        team.getTeamModificationHistories().add(message);
        Notification notification=createNotification(userName,"A_MEMBER_LEFT_TEAM",teamName,team.getTeamOwner(),"A team member named "+userName+" has left the team "+teamName);
        List<Task> tasks=taskRepository.findByTaskOwnerAndTaskNameIn(team.getTeamOwner(),team.getTeamTasks());
        if(!tasks.isEmpty()){
            for(Task task:tasks){
                task.getTaskParticipants().remove(userName);
            }
        }
        taskRepository.saveAll(tasks);
        sendEmail(notification);
        notificationRepository.save(notification);
        team.getTeamMembers().remove(userName);
        teamRepository.save(team);

        return "User left and hence user removed from the team";

    }

}
