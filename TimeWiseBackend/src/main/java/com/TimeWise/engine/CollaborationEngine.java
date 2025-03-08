package com.TimeWise.engine;

import com.TimeWise.model.*;
import com.TimeWise.repository.*;
import com.TimeWise.service.MailService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


@Component
public class CollaborationEngine {

    @Autowired
    private NotificationRepository notificationRepositoryInstance;

    @Autowired
    private MessageRepository messageRepositoryInstance;

    @Autowired
    private TeamRepository teamRepositoryInstance;

    @Autowired
    private TaskRepository taskRepositoryInstance;

    @Autowired
    private UserRepository userRepositoryInstance;

    @Autowired
    private FeedbackRepository feedbackRepositoryInstance;

    @Autowired
    private MailService mailServiceInstance;



    private static UserRepository userRepository;

    private static TeamRepository teamRepository;
    private static TaskRepository taskRepository;
    private static NotificationRepository notificationRepository;
    private static MessageRepository messageRepository;
    private static FeedbackRepository feedbackRepository;
    private static MailService mailService;

    private static String timeWiseEmail;

    @Value("${timewise.email}")
    private String timeWiseEmailInstance;



    @PostConstruct
    private void initStaticDependencies() {
        userRepository = userRepositoryInstance;
        mailService = mailServiceInstance;
        teamRepository = teamRepositoryInstance;
        taskRepository = taskRepositoryInstance;
        notificationRepository = notificationRepositoryInstance;
        messageRepository = messageRepositoryInstance;
        timeWiseEmail = timeWiseEmailInstance;
        feedbackRepository=feedbackRepositoryInstance;

    }
    public static ResponseEntity<?> sendMessage(String sender, String recipient, String messageSubject, String messageDescription) {
        Notification notification = new Notification();
        switch (messageSubject) {
            case "USER_MESSAGE":
                notification = createNotification(sender, messageSubject, null, recipient,
                        "Message from " + sender + " to you: " + messageDescription);
                break;

            case "TIME_WISE_MESSAGE":
                notification = createNotification(sender, messageSubject, null, sender, "Message From TimeWise: " + messageDescription);
                break;

            case "TEAM_MESSAGE":
                Team team = teamRepository.findByTeamName(recipient);
                if (team == null) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Recipient team not found.");
                }
                if (!team.getTeamMembers().contains(sender)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Team does not have any member.");
                }
                notification = createNotification(sender, messageSubject, recipient, null, "Message from " + sender + " to team " + recipient + ": " + messageDescription);
                notification.setRecipients(team.getTeamMembers());
                break;
            default:
                throw new IllegalArgumentException("Invalid notification subject");
        }
        Message message = new Message();
        message.setSender(sender);
        message.setRecipients(notification.getRecipients());
        message.setMessageSubject(messageSubject);
        message.setMessageDescription(messageDescription);
        message.setMessageStatus("Unseen");
        message.setTimeStamp(new Date());
        messageRepository.save(message);
        sendEmail(notification);
        return ResponseEntity.ok("Successfully sent message");

    }

    public static ResponseEntity<?> sendFeedback(String sender, String recipient,String feedbackMessage, String feedbackTaskName,Integer feedbackScore) {
        Notification notification = new Notification();

                notification = createNotification(sender, "USER_FEEDBACK", null, recipient,
                        "Feedback from " + sender + " to you: " + feedbackMessage+" Feedback on task \" "+feedbackTaskName+" \" Feedback Score: "+feedbackScore);



        sendEmail(notification);
        Feedback feedback = Feedback.builder()
                .feedbackSender(sender)
                .feedbackRecipient(recipient)
                .feedbackTaskName(feedbackTaskName)
                .feedbackScore(feedbackScore)
                .feedbackMessage(feedbackMessage)
                .timeStamp(new Date())
                .build();
        feedbackRepository.save(feedback);
        return ResponseEntity.ok("Successfully sent feedback");

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

    private static void createTeamNotification(String sender, String teamName, String subject, String messageContent) {
        Team team = teamRepository.findByTeamName(teamName);
        if (team == null) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body("Recipient team not found.");
            return;
        }
        if (!team.getTeamMembers().contains(sender)) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Team does not have any member.");
            return;
        }
        Notification notification = createNotification(sender, subject, teamName, null, messageContent);
        notification.setRecipients(new HashSet<>(team.getTeamMembers()));
        notification.setNotificationStatus("Unseen");
        notificationRepository.save(notification);
        sendEmail(notification);
    }
    public static ResponseEntity<?> handleTeamJoiningInvitation(String teamName, String sender, String recipient) {
        Team team = teamRepository.findByTeamNameAndTeamOwner(teamName, sender);
        if (team == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Team not found.");
        }
        if (team.getInvitedMembers().contains(recipient)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You already invited this member to that team");
        }
        if (team.getTeamMembers().contains(recipient)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("This member is already a team member");
        }

        String subject = "TEAM JOINING INVITATION";
        String messageContent = "Myself, " + sender + " inviting you to join the team " + "\" " + teamName + " \" ";
        Notification notification = createNotification(sender, subject, teamName, recipient, messageContent);

        sendEmail(notification);
        team.getInvitedMembers().add(recipient);

        // Add the modification history entry
        if (team.getTeamModificationHistories() == null) {
            team.setTeamModificationHistories(new ArrayList<>());
        }

        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        String timestamp = LocalDateTime.now().format(formatter);
        team.getTeamModificationHistories().add(timestamp + " - " + sender + " invited " + recipient + " to the team");

        teamRepository.save(team);

        notificationRepository.save(notification);

        return ResponseEntity.ok(team);
    }

    public static ResponseEntity<?> handleTaskParticipatingInvitation(String taskName, String sender, String recipient) {
        Task task = taskRepository.findByTaskNameAndTaskOwner(taskName, sender);
        if (task == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found.");
        }

        if (task.getInvitedMembers().contains(recipient)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You already invited this member with that task name");
        }
        if (task.getTaskParticipants().contains(recipient)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("This member is already a task participant");
        }
        String subject = "TASK PARTICIPATING INVITATION";
        String messageContent = "Myself, " + sender + " inviting you to participate in the named " + "\" " + taskName + " \" ";
        Notification notification = createNotification(sender, subject, taskName, recipient, messageContent);

        sendEmail(notification);

        notificationRepository.save(notification);

        task.getInvitedMembers().add(recipient);
        taskRepository.save(task);
        return ResponseEntity.ok(task);

    }

    public static ResponseEntity<?> handleTeamJoiningInvitationResponse(String teamName, String respondedBy, String response) {
        Team team = teamRepository.findByTeamName(teamName);
        if (team == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Team " + teamName + " not found");
        }

        if (!team.getInvitedMembers().contains(respondedBy)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not invited to this team, " + teamName);
        }
        if (team.getTeamMembers().contains(respondedBy)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You are already a member of this team, " + teamName);
        }

        // If accepted, add user to all team tasks as a participant
        if ("accept".equalsIgnoreCase(response)) {
            List<Task> teamTasks = taskRepository.findByTaskOwnerAndTaskNameIn(team.getTeamOwner(), team.getTeamTasks());
            if (!teamTasks.isEmpty()) {

                // Iterate through all tasks of the team and add the new member as a participant
                for (Task task : teamTasks) {
                    task.getTaskParticipants().add(respondedBy);
                }
                taskRepository.saveAll(teamTasks);
            }

            // Log the new member addition to the team's modification history
            String message = "New member " + respondedBy + " added to the team and now can participate in team tasks.";
            team.getTeamModificationHistories().add(message);
            team.getInvitedMembers().remove(respondedBy);
            team.getTeamMembers().add(respondedBy);
            teamRepository.save(team);
            sendMailFromTimeWise(team.getTeamOwner(), "Team Joining Invitation Accepted!!!", "Invitee \" " + respondedBy + " \" has accepted your request to join the team \" " + teamName + " \"");

        } else if ("decline".equalsIgnoreCase(response)) {
            team.getInvitedMembers().remove(respondedBy);
            teamRepository.save(team);
            sendMailFromTimeWise(team.getTeamOwner(), "Team Joining Invitation Declined", "Invitee \" " + respondedBy + " \" has declined your request to join the team \" " + teamName + " \"");

        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Response either by accept or decline");
        }

        return ResponseEntity.ok("Response has benn handled.");
    }
public static ResponseEntity<?> handleTaskParticipatingInvitationResponse(String taskName,String taskOwner, String respondedBy,  String response) {

    Task task = taskRepository.findByTaskNameAndTaskOwner(taskName, taskOwner);
            if (task == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found");
            }
            if (!task.getInvitedMembers().contains(respondedBy)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not invited to this task");
            }
            if (task.getTaskParticipants().contains(respondedBy)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You are already a participant in this task");
            }
            if ("accept".equalsIgnoreCase(response)) {
                task.getInvitedMembers().remove(respondedBy);
                task.getTaskParticipants().add(respondedBy);
                taskRepository.save(task);
                sendMailFromTimeWise(taskOwner, "Task Participating Invitation Accepted!!!", "Invitee \" " + respondedBy + " \" has accepted your request to participate in the task \" " + taskName + " \"");

            }
            else if("decline".equalsIgnoreCase(response)){
                task.getInvitedMembers().remove(respondedBy);
                taskRepository.save(task);
                sendMailFromTimeWise(taskOwner, "Task Participating Invitation Declined", "Invitee \" " + respondedBy + " \" has declined your request to participate in the task \" " + taskName + " \"");
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Response either by accept or decline");
            }



        return ResponseEntity.ok("Response has benn handled.");
    }
    private static void sendMailFromTimeWise(String recipient, String messageSubject, String messageBody) {
        User user = userRepository.findByUserName(recipient);

        // Create and save notification first
        Notification notification = new Notification();
        notification.setSender("TimeWise"); // Sender is TimeWise
        Set<String> recipients = new HashSet<>();
        recipients.add(recipient);
        notification.setRecipients(recipients);
        notification.setNotificationSubject(messageSubject);
        notification.setNotificationMessage(messageBody);
        notification.setNotificationStatus("Unseen"); // Initial status
        notification.setTimeStamp(new Date());

        notificationRepository.save(notification);

        // Send the email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(timeWiseEmail);
        message.setTo(user.getUserEmail());
        message.setSubject(messageSubject);
        message.setText(messageBody);
        mailService.sendMessage(message);
    }

    public static void sendEmail(Notification notification) {
        StringBuilder emailStatus = new StringBuilder();
        String timeWiseEmail = "bsse1401@iit.du.ac.bd"; // TimeWise official email

        // Find all recipients and sender
        List<User> recipients = userRepository.findByUserNameIn(notification.getRecipients());
        User sender = userRepository.findByUserName(notification.getSender());

        // Validate sender details
        if (sender == null || sender.getUserEmail() == null) {
            emailStatus.append("Invalid sender details.");
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(emailStatus.toString());
            return;
        }
        if (recipients.isEmpty()) {
            emailStatus.append("No recipients found for the notification. No emails sent.");
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(emailStatus.toString());
            return;
        }

        // Identify missing recipients
        Set<String> missingRecipients = new HashSet<>(notification.getRecipients());
        for (User recipient : recipients) {
            missingRecipients.remove(recipient.getUserName());
        }

        // Handle missing recipients
        if (missingRecipients.size() == notification.getRecipients().size()) {
            emailStatus.append("No valid recipients found for the notification. No emails sent.");
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(emailStatus.toString());
            return;
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
                message.setFrom(timeWiseEmail); // Use TimeWise official email
                message.setTo(recipient.getUserEmail());
                message.setSubject("[TimeWise Notification] " + notification.getNotificationSubject()); // Add TimeWise context
                message.setText("Hello " + recipient.getUserName() + ",\n\n" +
                        "You have received a notification from TimeWise:\n\n" +
                        "Subject: " + notification.getNotificationSubject() + "\n" +
                        "Message: " + notification.getNotificationMessage() + "\n\n" +
                        "This notification was sent by " + sender.getUserName() + " via TimeWise.\n\n" +
                        "Stay productive!\n\n" +
                        "Best regards,\n" +
                        "The TimeWise Team");
                mailService.sendMessage(message);
                emailStatus.append("Email successfully sent to ").append(recipient.getUserName()).append("\n");
            } catch (Exception e) {
                emailStatus.append("Failed to send email to ").append(recipient.getUserName())
                        .append(": ").append(e.getMessage()).append("\n");
                ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(emailStatus.toString());
                return;
            }
        }

        ResponseEntity.ok(emailStatus.toString());
    }

    public static void sendProgressAndPerformanceReport(String messageBody, String userName, Boolean isProgressReport) {
        User recipient =userRepository.findByUserName(userName);
        if(recipient==null){
            ResponseEntity.status(HttpStatus.NOT_FOUND).body("No recipient is selected.");
            return;
        }
        if(recipient.getUserEmail()==null){
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Recipients email is not given.");
            return;
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
            mailService.sendMessage(message);
            emailStatus.append("Email successfully sent to ").append(recipient.getUserName()).append("\n");
        } catch (Exception e) {
            emailStatus.append("Failed to send email to ").append(recipient.getUserName())
                    .append(": ").append(e.getMessage()).append("\n");
            ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(emailStatus.toString());
            return;
        }
        ResponseEntity.ok(emailStatus.toString());

    }

    public static ResponseEntity<?> sendUserRegistrationVerificationCode(String receiverName, String receiverEmail,String verificationCode) {

        StringBuilder emailStatus = new StringBuilder();
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(timeWiseEmail);
            message.setTo(receiverEmail);
            message.setSubject("TimeWise Registration Verification Code");
            message.setText("Your TimeWise account registration verification code is: " + verificationCode + ". It expires in 7 minutes.");
            mailService.sendMessage(message);
            emailStatus.append("Email successfully sent to user name: ").append(receiverName).append(" user email:").append(receiverEmail).append("\n");
        } catch (Exception e) {
            emailStatus.append("Failed to send email to ").append(receiverName).append(" user email: ").append(receiverEmail)
                    .append(": ").append(e.getMessage()).append("\n");
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(emailStatus.toString());
        }
        return ResponseEntity.ok(emailStatus.toString());
    }


    public static void sendRegistrationSuccessfulMessage(String receiverName, String receiverEmail) {
        StringBuilder emailStatus = new StringBuilder();
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(timeWiseEmail);
            message.setTo(receiverEmail);
            message.setSubject("TimeWise Registration Successful");
            message.setText(receiverName+", Welcome to TimeWise! We hope TimeWise will help you be more productive.");
            mailService.sendMessage(message);
            emailStatus.append("Email successfully sent to user name: ").append(receiverName).append(" user email:").append(receiverEmail).append("\n");
        } catch (Exception e) {
            throw new IllegalArgumentException("Error sending mail to "+receiverName+" with receiver email "+receiverEmail+" about successful registration"+e.getMessage());
        }

    }

    public static ResponseEntity<?> sendForgottenAccountCredentials(String receiverEmail, String verificationCode, Set<String> userNames) {
        StringBuilder emailStatus = new StringBuilder();

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(timeWiseEmail);
            message.setTo(receiverEmail);
            message.setSubject("TimeWise: Account Recovery - Verification Code & Usernames");

            StringBuilder text = new StringBuilder();
            text.append("Dear TimeWise User,\n\n");
            text.append("We received a request to recover your TimeWise account.\n\n");
            text.append("Your verification code is: ").append(verificationCode).append("\n");
            text.append("This code will expire in 7 minutes.\n\n");

            if (userNames != null && !userNames.isEmpty()) {
                text.append("The following usernames are associated with this email address:\n");
                for (String userName : userNames) {
                    text.append("- ").append(userName).append("\n");
                }
                text.append("\n");
            }

            text.append("If you did not request this account recovery, please ignore this email.\n\n");
            text.append("For your security, do not share this code with anyone.\n\n");
            text.append("Thank you for using TimeWise!\n\n");
            text.append("Best regards,\n");
            text.append("The TimeWise Support Team");

            message.setText(text.toString());
            mailService.sendMessage(message);
            emailStatus.append("Email successfully sent to user email: ").append(receiverEmail).append("\n");
        } catch (Exception e) {
            emailStatus.append("Failed to send email to user email: ").append(receiverEmail)
                    .append(": ").append(e.getMessage()).append("\n");
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(emailStatus.toString());
        }
        return ResponseEntity.ok(emailStatus.toString());
    }


    public static ResponseEntity<?> sendAccountVerificationCode( String receiverEmail,String verificationCode) {

        StringBuilder emailStatus = new StringBuilder();
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(timeWiseEmail);
            message.setTo(receiverEmail);
            message.setSubject("TimeWise Account Verification Code");
            message.setText("Your TimeWise account verification code is: " + verificationCode + ". It expires in 7 minutes.");
            mailService.sendMessage(message);
            emailStatus.append("Email successfully sent to user email:").append(receiverEmail).append("\n");
        } catch (Exception e) {
            emailStatus.append("Failed to send email to ").append(" user email: ").append(receiverEmail)
                    .append(": ").append(e.getMessage()).append("\n");
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(emailStatus.toString());
        }
        return ResponseEntity.ok(emailStatus.toString());
    }


    public static ResponseEntity<?> addTaskToTeam(String userName,String teamName, String taskName) {

        Task task=taskRepository.findByTaskNameAndTaskOwner(taskName,userName);
        if(task==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found.");
        }
        Team team=teamRepository.findByTeamName(teamName);
        if(team==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Team not found.");
        }
        if(!team.getTeamOwner().equals(userName)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Only team owner can add task to team");
        }
        if(!task.getTaskOwner().equals(userName)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not the task owner and non owned task can not be added to team");
        }
        if(team.getTeamTasks().contains(taskName)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("This team already contains a task with same name");

        }
        Set<String> teamMembers=team.getTeamMembers();
        task.getTaskParticipants().addAll(teamMembers);
        taskRepository.save(task);
        String message="A new task named "+task.getTaskName()+"is added to the team.";
        team.getTeamModificationHistories().add(message);
        team.getTeamTasks().add(task.getTaskName());
        teamRepository.save(team);
        createTeamNotification(userName,teamName,"NEW_TEAM_TASK_ADDED",message);

        return ResponseEntity.ok(team);

    }

    public static ResponseEntity<?> removeTaskFromTeam(String userName,String teamName, String taskName) {
        Task task = taskRepository.findByTaskNameAndTaskOwner(taskName,userName);
        if (task == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found.");
        }
        Team team = teamRepository.findByTeamNameAndTeamOwner(teamName,userName);
        if (team == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Team not found.");
        }
        if (!team.getTeamOwner().equals(userName)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Only team owner can remove task from team");
        }
        if(!task.getTaskOwner().equals(userName)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not the task owner and non owned task can not be removed from team");
        }
        Set<String> teamMembers=team.getTeamMembers();
        task.getTaskParticipants().removeAll(teamMembers);
        task.getTaskParticipants().add(userName);
        taskRepository.save(task);
        String message="A task named "+task.getTaskName()+"has been removed from the team "+teamName;
        team.getTeamModificationHistories().add(message);
        team.getTeamTasks().remove(task.getTaskName());
        teamRepository.save(team);
        createTeamNotification(userName,teamName,"A_TEAM_TASK_REMOVED",message);
        return ResponseEntity.ok(team);
    }

    public static ResponseEntity<?> removeDeletedTaskFromTeams(Task task){
        String taskName=task.getTaskName();
        String taskOwner=task.getTaskOwner();
        Set<String> taskParticipants=task.getTaskParticipants();
        String message="The task owned by "+taskOwner+" with task name: '" + taskName+"', task goal : '"+task.getTaskGoal()+"' is deleted by the owner : "+taskOwner;
        List<Team> teamsHavingTasks=teamRepository.findByTeamOwnerAndTeamTasksContaining(task.getTaskOwner(),taskName);
        if(!teamsHavingTasks.isEmpty()) {
            for (Team team : teamsHavingTasks) {
                team.getTeamTasks().remove(taskName);
                team.getTeamModificationHistories().add("A task named "+taskName+"has been deleted and hence removed from the team.");
                Set<String> teamMembers=team.getTeamMembers();
                createTeamNotification(taskOwner,team.getTeamName(),"A_TEAM_TASK_REMOVED", message+"and hence removed from the team.");
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

        return ResponseEntity.ok("Task deleted successfully");

    }

    public static ResponseEntity<?>  removeMemberFromTeam(String teamOwner,String teamName, String userName) {
        Team team=teamRepository.findByTeamName(teamName);
        if(team==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Team not found.");
        }
        if(!team.getTeamOwner().equals(teamOwner)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not the team owner");
        }
        if(!team.getTeamMembers().contains(userName)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not team member");
        }
        team.getTeamMembers().remove(userName);
        String message="A team member named \""+userName+"\" has been removed from the team.";
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

        return ResponseEntity.ok(team);

    }
    public static ResponseEntity<?>  leaveTeam(String teamName, String userName) {
        Team team=teamRepository.findByTeamName(teamName);
        if(team==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Team not found.");
        }

        if(!team.getTeamMembers().contains(userName)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not a team member");
        }
        if(team.getTeamOwner().equals(userName)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You are the owner of the team and can not leave the team");
        }
        team.getTeamMembers().remove(userName);
        String message="A team member named \""+userName+"\" has left the team.";
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

        return ResponseEntity.ok("You successfully left the team.");

    }
    public static ResponseEntity<?> handleTeamJoiningRequest(String sender, String teamName) {
        Team team = teamRepository.findByTeamName(teamName);
        if (team == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Team not found.");
        }

        if (team.getTeamMembers().contains(sender)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You are already a member of this team");
        }
        if(team.getRequestedToJoinMembers() == null){
            team.setRequestedToJoinMembers(new HashSet<>());
        }
        if (team.getRequestedToJoinMembers().contains(sender)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You have already requested to join this team.");
        }

        team.getRequestedToJoinMembers().add(sender);
        teamRepository.save(team);

        String subject = "TEAM JOINING REQUEST";
        String messageContent = sender + " has requested to join your team " + teamName;

        Notification notification = createNotification(sender, subject, teamName, team.getTeamOwner(), messageContent);

        sendEmail(notification);
        notificationRepository.save(notification);

        return ResponseEntity.ok("Team joining request sent successfully.");
    }

    public static ResponseEntity<?> handleTeamJoiningRequestResponse(String respondedBy, String teamName, String respondedTo, String response) {
        Team team = teamRepository.findByTeamNameAndTeamOwner(teamName, respondedBy);
        if (team == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Team not found.");
        }
        if(team.getRequestedToJoinMembers() == null || !team.getRequestedToJoinMembers().contains(respondedTo)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("This user has not requested to join this team.");
        }

        if ("accept".equalsIgnoreCase(response)) {
            List<Task> teamTasks = taskRepository.findByTaskOwnerAndTaskNameIn(team.getTeamOwner(), team.getTeamTasks());
            if (!teamTasks.isEmpty()) {
                for (Task task : teamTasks) {
                    task.getTaskParticipants().add(respondedTo);
                }
                taskRepository.saveAll(teamTasks);
            }

            team.getTeamMembers().add(respondedTo);
            team.getRequestedToJoinMembers().remove(respondedTo);
            String message="A team member named \""+respondedTo+"\" has been added to the team.";
            team.getTeamModificationHistories().add(message);
            teamRepository.save(team);
            sendMailFromTimeWise(respondedBy, "Team Joining Request Accepted", "Your request to join team \"" + teamName + "\" has been accepted.");
        } else if ("decline".equalsIgnoreCase(response)) {
            team.getRequestedToJoinMembers().remove(respondedTo);
            teamRepository.save(team);
            sendMailFromTimeWise(respondedBy, "Team Joining Request Declined", "Your request to join team \"" + teamName + "\" has been declined.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Response must be either 'accept' or 'decline'.");
        }

        return ResponseEntity.ok(team);
    }
    public static ResponseEntity<List<Notification>> getAllNotificationsForUser(String currentUserName) {
        List<Notification> notifications = notificationRepository.findByRecipientsContains(currentUserName);

        if (notifications.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        // Update notification status to "Seen" and save to the database
        List<Notification> updatedNotifications = notifications.stream()
                .peek(notification -> {
                    notification.setNotificationStatus("Seen");
                    notificationRepository.save(notification);
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(updatedNotifications);
    }
}