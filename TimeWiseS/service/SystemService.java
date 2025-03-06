package com.TimeWise.service;

import com.TimeWise.engine.AnalyticsEngine;
import com.TimeWise.engine.CollaborationEngine;
import com.TimeWise.model.*;
import com.TimeWise.repository.*;
import com.TimeWise.utils.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
@EnableAsync
public class SystemService {

    @Autowired
    private JWTService jwtServiceInstance;

    @Autowired
    private AuthenticationManager authManagerInstance;

    @Autowired
    private UserRepository userRepositoryInstance;

    @Autowired
    private TaskRepository taskRepositoryInstance;

    @Autowired
    private TeamRepository teamRepositoryInstance;

    @Autowired
    private ProgressReportRepository progressReportRepositoryInstance;


    @Autowired
    private UserVerificationMessageRepository userVerificationMessageRepositoryInstance;

    private static JWTService jwtService;
    private static AuthenticationManager authManager;
    private static UserRepository userRepository;
    private static TaskRepository taskRepository;
    private static TeamRepository teamRepository;
    private static ProgressReportRepository progressReportRepository;
    private static UserVerificationMessageRepository userVerificationMessageRepository;

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    @PostConstruct
    private void initStaticDependencies() {
        jwtService = jwtServiceInstance;
        authManager = authManagerInstance;
        userRepository = userRepositoryInstance;
        taskRepository = taskRepositoryInstance;
        teamRepository = teamRepositoryInstance;
        userVerificationMessageRepository = userVerificationMessageRepositoryInstance;
    }
    public static ResponseEntity<?> checkRegistrationCredentialsAndSendRegistrationVerificationCode(User user) {
        // Check if username or email already exists
        if (userRepository.findByUserName(user.getUserName())!=null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username already exists.");
        }
//        if (userRepository.existsByEmail(user.getEmail())) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email already exists.");
//        }

        // Generate a verification code
        UserVerificationMessage verificationCode=generateVerificationCode(user);
        userVerificationMessageRepository.deleteByUserNameOrUserEmail(user.getUserName(),user.getUserEmail());
        // Save verification code
        userVerificationMessageRepository.save(verificationCode);


        return ResponseEntity.ok(CollaborationEngine.sendUserRegistrationVerificationCode(user.getUserName(),user.getUserEmail(),verificationCode.getCode()));
    }

    public static ResponseEntity<?> verifyVerificationCodeAndCompleteRegistration(User user, String code) {
        // Check if the verification code is valid and not expired
        UserVerificationMessage verificationCode = userVerificationMessageRepository.findByUserEmail(user.getUserEmail());
        if (verificationCode == null ) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No verification code found for this email");
        }
        if(verificationCode.getExpiry().before(new Date())){
            userVerificationMessageRepository.delete(verificationCode);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Expired verification code.");
        }
        if(!verificationCode.getCode().equals(code)){
            userVerificationMessageRepository.delete(verificationCode);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Wrong verification code.Previous code became useless. Ask for new code if required. ");
        }

        // Verify email matches the user's email
        if (!user.getUserEmail().equals(verificationCode.getUserEmail()) || !user.getUserName().equals(verificationCode.getUserName())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Name or Email mismatch during verification.Please send the same credentials as before getting the verification code");
        }

        // Check if username or email already exists (revalidation)
        if (userRepository.findByUserName(user.getUserName())!=null) {
            userVerificationMessageRepository.delete(verificationCode);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Sorry you have to register again with another name as in the mean time a new user with that user name registered");
        }
//        if (userRepository.existsByEmail(user.getEmail())) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email already exists.");
//        }


        // Remove the verification code from the database
        userVerificationMessageRepository.delete(verificationCode);
        // Save the user in the database
        String rawPassword = user.getPassword();

        user.setPassword(encoder.encode(rawPassword));
        user.setUserStatus("Active");
        user.setUsersFollowing(new HashSet<>());
        if(user.getRole()==null){
            user.setRole("User");
        }
        if(user.getShortBioData()==null){
            user.setShortBioData("Myself "+user.getUserName());
        }
        userRepository.save(user);
        CollaborationEngine.sendRegistrationSuccessfulMessage(user.getUserName(),user.getUserEmail());
        // Authenticate and generate token
        user.setPassword(rawPassword); // Reset raw password for login
        return performUserLogin(user);
    }

    public static ResponseEntity<?> performUserLogin(User user) {
        try {
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword())
            );

            if (authentication.isAuthenticated()) {
                User authenticatedUser = userRepository.findByUserName(user.getUserName());
                if (authenticatedUser == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found in the database.");
                }

                return ResponseEntity.ok(jwtService.generateToken(authenticatedUser.getUserId(),authenticatedUser.getUserName(),authenticatedUser.getUserEmail()));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Authentication failed. Invalid credentials.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Authentication failed. Bad credentials.");
        }
    }

    public static ResponseEntity<?> handleForgotUserCredentials(String userEmail) {
        List<User> user = userRepository.findByUserEmail(userEmail);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        String code = UUID.randomUUID().toString().substring(0, 6).toUpperCase(); // Generate a 6-character code

        // Generate a verification code
        UserVerificationMessage verificationCode=new UserVerificationMessage(code,"User",userEmail,new Date(System.currentTimeMillis() + (7 * 60 * 1000)));

        userVerificationMessageRepository.deleteByUserEmail(userEmail);
        // Save verification code
        userVerificationMessageRepository.save(verificationCode);

        return CollaborationEngine.sendAccountVerificationCode(userEmail,verificationCode.getCode());

    }
    public static ResponseEntity<?> verifyVerificationCodeForAccountVerification(String code,String userEmail) {
        // Check if the verification code is valid and not expired
        UserVerificationMessage verificationCode = userVerificationMessageRepository.findByUserEmail(userEmail);
        if (verificationCode == null ) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No verification code found for this email");
        }
        if(verificationCode.getExpiry().before(new Date())){
            userVerificationMessageRepository.delete(verificationCode);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Expired verification code.");
        }
        if(!verificationCode.getCode().equals(code)){
            userVerificationMessageRepository.delete(verificationCode);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Wrong verification code.Previous code became useless. Ask for new code if required. ");
        }
        return ResponseEntity.ok(userRepository.findByUserEmail(userEmail));

    }

    private static UserVerificationMessage generateVerificationCode(User user) {
        String code = UUID.randomUUID().toString().substring(0, 6).toUpperCase(); // Generate a 6-character code

        return new UserVerificationMessage(
                code,
                user.getUserName(),
                user.getUserEmail(),
                new Date(System.currentTimeMillis() + (7 * 60 * 1000)) // 7 minutes expiry
        );

    }

    // Update user details
    public static ResponseEntity<?> updateUserAccounDetails(String userName, UpdatedUserAccount updatedUserDetails) {
        Boolean newTokenNeeded=false;
        // Find the user by userName
        User existingUser = userRepository.findByUserName(userName);

        if (existingUser == null) {
            throw new RuntimeException("User not found with username: " + userName);
        }

        // Update fields selectively if they are not null or empty
        if (updatedUserDetails.getUserName() != null && !updatedUserDetails.getUserName().isEmpty()) {
            existingUser.setUserName(updatedUserDetails.getUserName());
            newTokenNeeded=true;
        }

        if (updatedUserDetails.getUserEmail() != null && !updatedUserDetails.getUserEmail().isEmpty()) {
            existingUser.setUserEmail(updatedUserDetails.getUserEmail());
            newTokenNeeded=true;
        }

        if (updatedUserDetails.getPreviousPassword() != null && !updatedUserDetails.getPreviousPassword().isEmpty() && updatedUserDetails.getNewPassword() != null && !updatedUserDetails.getNewPassword().isEmpty()) {
            if(existingUser.getPassword().equals(encoder.encode(updatedUserDetails.getPreviousPassword()))){
                existingUser.setPassword(encoder.encode(updatedUserDetails.getNewPassword()));
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Incorrect previous password");
            }
        }

        if (updatedUserDetails.getShortBioData() != null && !updatedUserDetails.getShortBioData().isEmpty()) {
            existingUser.setShortBioData(updatedUserDetails.getShortBioData());
        }

        if (updatedUserDetails.getRole() != null && !updatedUserDetails.getRole().isEmpty()) {
            existingUser.setRole(updatedUserDetails.getRole());
        }

        if (updatedUserDetails.getUserStatus() != null && !updatedUserDetails.getUserStatus().isEmpty()) {
            existingUser.setUserStatus(updatedUserDetails.getUserStatus());
        }

        // Save the updated user to the repository
        userRepository.save(existingUser);
        if(newTokenNeeded) {
            return ResponseEntity.ok("Account Updated Successfully.Session Expired. Log in again.");
        }
        return  ResponseEntity.ok("Account Updated Successfully");
    }

    public static ResponseEntity<?> getUserAccountDetails(String userName) {
        // Check if the user exists
        User user = userRepository.findByUserName(userName);

        if (user != null) {
            // Initialize UserDetailResponse
            UserDetailResponse userDetailResponse = new UserDetailResponse();
            userDetailResponse.setUserName(user.getUserName());
            userDetailResponse.setUserEmail(user.getUserEmail());
            userDetailResponse.setShortBioData(user.getShortBioData());
            userDetailResponse.setRole(user.getRole());
            userDetailResponse.setUserStatus(user.getUserStatus());

            List<UserDetailResponse.UserTasks> userTasks = taskRepository.findAll().stream()
                    .filter(task ->
                            task.getTaskParticipants().contains(userName) || // Check if user is a participant
                                    "public".equalsIgnoreCase(task.getTaskVisibilityStatus())) // Check if task visibility is public
                    .map(task -> new UserDetailResponse.UserTasks(
                            task.getTaskName(),
                            task.getTaskOwner(),
                            task.getTaskCategory(),
                            task.getTaskDescription(),
                            task.getTaskTodos().stream()
                                    .map(Task.TaskTodo::getDescription)
                                    .collect(Collectors.toList())
                    ))
                    .collect(Collectors.toList());

            userDetailResponse.setUserTasks(userTasks);


            // Fetch teams where user is a member or teams marked as public
            List<UserDetailResponse.UserTeams> userTeams = teamRepository.findAll().stream()
                    .filter(team ->
                            team.getTeamMembers().contains(userName) ||
                                    "public".equalsIgnoreCase(team.getTeamVisibilityStatus()))
                    .map(team -> new UserDetailResponse.UserTeams(
                            team.getTeamName(),
                            team.getTeamOwner(),
                            team.getTeamDescription()
                    ))
                    .collect(Collectors.toList());
            userDetailResponse.setUserTeams(userTeams);

            return ResponseEntity.ok(userDetailResponse);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }

    @Scheduled(cron = "0 0 9,17,22 * * *") // 9 AM, 5 PM, 10 PM daily
    @Async("taskExecutor")
    public void sendDailyProgressReportToAllUsers() {
        Set<String> userNames = userRepository.findAllUserNames();
        processReports(userNames, this::generateAndSendDailyReport);
    }





    private void processReports(Set<String> userNames, Consumer<String> reportProcessor) {
        for (String userName : userNames) {
            CompletableFuture.runAsync(() -> reportProcessor.accept(userName));
        }
    }


    private void generateAndSendDailyReport(String userName) {
        try {
            ProgressReport report = AnalyticsEngine.generateProgressReport(userName);
            if (report != null) {
                progressReportRepository.save(report);
                String message = generateMessageDescriptionFromProgressReport(report);
                CollaborationEngine.sendProgressAndPerformanceReport(message, userName, true);
            }
        } catch (Exception e) {
            logError(e, "Daily Progress Report", userName);
        }
    }





    private void logError(Exception e, String reportType, String userName) {
        System.err.println("Failed to generate " + reportType + " for user: " + userName);
        e.printStackTrace();
    }
    @Async
    public String generateMessageDescriptionFromProgressReport(ProgressReport progressReport) {
        // Separate tasks into deadline crossed and non-crossed categories
        List<ProgressReport.TaskStatus> deadlineCrossedTasks = progressReport.getTaskStatuses().stream()
                .filter(ProgressReport.TaskStatus::getIsDeadlineCrossed)
                .toList();

        List<ProgressReport.TaskStatus> nonDeadlineCrossedTasks = progressReport.getTaskStatuses().stream()
                .filter(task -> !task.getIsDeadlineCrossed())
                .toList();

        // Build descriptive notification messages
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("Progress Report Summary for ").append(progressReport.getUserName()).append(":\n\n");

        if (!deadlineCrossedTasks.isEmpty()) {
            messageBuilder.append("Tasks with Crossed Deadlines:\n");
            for (ProgressReport.TaskStatus task : deadlineCrossedTasks) {
                messageBuilder.append("- Task: ").append(task.getTaskName())
                        .append(", Owner: ").append(task.getTaskOwner())
                        .append(", Priority: ").append(task.getTaskPriority())
                        .append(", Progress: ").append(task.getTasksCurrentProgress()).append("%\n");
            }
            messageBuilder.append("\n");
        }

        if (!nonDeadlineCrossedTasks.isEmpty()) {
            messageBuilder.append("Tasks with Deadlines Remaining:\n");
            for (ProgressReport.TaskStatus task : nonDeadlineCrossedTasks) {
                messageBuilder.append("- Task: ").append(task.getTaskName())
                        .append(", Owner: ").append(task.getTaskOwner())
                        .append(", Priority: ").append(task.getTaskPriority())
                        .append(", Progress: ").append(task.getTasksCurrentProgress()).append("%\n");
            }
            messageBuilder.append("\n");
        }

        if (deadlineCrossedTasks.isEmpty() && nonDeadlineCrossedTasks.isEmpty()) {
            messageBuilder.append("No tasks available in the progress report.\n");
        }

        return messageBuilder.toString();
    }


}
