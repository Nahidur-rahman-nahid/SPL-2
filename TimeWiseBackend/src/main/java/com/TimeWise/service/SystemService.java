package com.TimeWise.service;

import com.TimeWise.engine.AnalyticsEngine;
import com.TimeWise.engine.CollaborationEngine;
import com.TimeWise.model.*;
import com.TimeWise.repository.*;
import com.TimeWise.utils.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.repository.Query;
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
import org.springframework.transaction.annotation.Transactional;


import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.regex.Pattern;
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

    @Autowired
    private MongoTemplate mongoTemplateInstance;

    private static JWTService jwtService;
    private static AuthenticationManager authManager;
    private static UserRepository userRepository;
    private static TaskRepository taskRepository;
    private static TeamRepository teamRepository;
    private static ProgressReportRepository progressReportRepository;
    private static UserVerificationMessageRepository userVerificationMessageRepository;
    private static MongoTemplate mongoTemplate;

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);



    @PostConstruct
    private void initStaticDependencies() {
        jwtService = jwtServiceInstance;
        authManager = authManagerInstance;
        userRepository = userRepositoryInstance;
        taskRepository = taskRepositoryInstance;
        teamRepository = teamRepositoryInstance;
        userVerificationMessageRepository = userVerificationMessageRepositoryInstance;
        mongoTemplate=mongoTemplateInstance;
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
        user.setAccountVisibility("Public");
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
       // user.setPassword(rawPassword); // Reset raw password for login
        LoginCredentials loginCredentials=new LoginCredentials(user.getUserName(),rawPassword);
        return performUserLogin(loginCredentials);
    }

    public static ResponseEntity<?> performUserLogin(LoginCredentials loginCredentials) {
        try {
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginCredentials.getUserName(), loginCredentials.getPassword())
            );

            if (authentication.isAuthenticated()) {
                User authenticatedUser = userRepository.findByUserName(loginCredentials.getUserName());
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

    @Transactional
    public static ResponseEntity<?> handleForgotUserCredentials(String userEmail) {
        List<User> users = userRepository.findByUserEmail(userEmail);
        if (users.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        String code = UUID.randomUUID().toString().substring(0, 6).toUpperCase(); // Generate a 6-character code

        // Generate a verification code
        UserVerificationMessage verificationCode=new UserVerificationMessage(code,"User",userEmail,new Date(System.currentTimeMillis() + (7 * 60 * 1000)));
        UserVerificationMessage verificationMessage1=userVerificationMessageRepository.findByUserEmail(userEmail);
        System.out.println(verificationMessage1);
        userVerificationMessageRepository.deleteByUserEmail(userEmail);
        UserVerificationMessage verificationMessage2=userVerificationMessageRepository.findByUserEmail(userEmail);
        System.out.println(verificationMessage2);
        // Save verification code
        userVerificationMessageRepository.save(verificationCode);
        System.out.println(verificationCode);
        UserVerificationMessage verificationMessage=userVerificationMessageRepository.findByUserEmail(userEmail);
        System.out.println(verificationMessage);

        Set<String> matchedUserNames=new HashSet<>();
        for(User user:users){
            matchedUserNames.add(user.getUserName());
        }

        return CollaborationEngine.sendForgottenAccountCredentials(userEmail,verificationCode.getCode(),matchedUserNames);

    }
    public static ResponseEntity<?> verifyVerificationCodeForAccountVerification(String code,String userEmail,String userName,String updatedPassword) {

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
        User user=userRepository.findByUserName(userName);
        user.setPassword(encoder.encode(updatedPassword));
        userRepository.save(user);
        userVerificationMessageRepository.delete(verificationCode);
        return performUserLogin(new LoginCredentials(userName,updatedPassword));

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
    public static ResponseEntity<?> updateUserAccountDetails( String userName,UpdatedUserAccount updatedUserDetails) {
        boolean newTokenNeeded=false;
        // Find the user by userName
        User existingUser = userRepository.findByUserName(userName);

        if (existingUser == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found");
        }

        if (updatedUserDetails.getUserEmail() != null && !updatedUserDetails.getUserEmail().isEmpty()) {
            existingUser.setUserEmail(updatedUserDetails.getUserEmail());
            newTokenNeeded=true;
        }

        if (updatedUserDetails.getPreviousPassword() != null && !updatedUserDetails.getPreviousPassword().isEmpty() && updatedUserDetails.getNewPassword() != null && !updatedUserDetails.getNewPassword().isEmpty()) {
            if(encoder.matches(updatedUserDetails.getPreviousPassword(), existingUser.getPassword())){
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
        if (updatedUserDetails.getAccountVisibility() != null && !updatedUserDetails.getAccountVisibility().isEmpty()) {
            existingUser.setAccountVisibility(updatedUserDetails.getAccountVisibility());
        }

        // Save the updated user to the repository
        userRepository.save(existingUser);
        if(newTokenNeeded) {
            return ResponseEntity.ok("Account Updated Successfully.Session Expired. Log in again.");
        }
        return  ResponseEntity.ok("Account Updated Successfully");
    }

    public static ResponseEntity<?> getUserAccountDetails(String currentUserName, String userName) {
        // Check if the user exists
        User user;
        String targetUserName; // Declare a new variable for the target username

        if (userName == null || userName.isEmpty()) {
            user = userRepository.findByUserName(currentUserName);
            targetUserName = currentUserName; // Assign to the new variable
        } else {
            user = userRepository.findByUserName(userName);
            targetUserName = userName; // Assign to the new variable
        }

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        if (user.getAccountVisibility().equals("Private") && !user.getUserName().equals(currentUserName)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The account is private");
        }

        // Initialize UserDetailResponse
        UserDetailResponse userDetailResponse = new UserDetailResponse();
        userDetailResponse.setUserName(user.getUserName());
        userDetailResponse.setUserEmail(user.getUserEmail());
        userDetailResponse.setShortBioData(user.getShortBioData());
        userDetailResponse.setRole(user.getRole());
        userDetailResponse.setUserStatus(user.getUserStatus());
        userDetailResponse.setAccountVisibility(user.getAccountVisibility());
        userDetailResponse.setUsersFollowing(user.getUsersFollowing());

        List<UserDetailResponse.UserTasks> userTasks = taskRepository.findAll().stream()
                .filter(task -> task.getTaskParticipants().contains(targetUserName) &&
                        task.getTaskVisibilityStatus().equalsIgnoreCase("public") &&
                        task.getTaskCurrentProgress() != 100)
                .map(task -> new UserDetailResponse.UserTasks(
                        task.getTaskName(),
                        task.getTaskOwner(),
                        task.getTaskCategory(),
                        task.getTaskPriority(),
                        task.getTaskDescription(),
                        task.getTaskCurrentProgress(),
                        task.getTaskDeadline(),
                        task.getTaskTodos().stream()
                                .map(Task.TaskTodo::getDescription)
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());

        userDetailResponse.setUserTasks(userTasks);

        List<UserDetailResponse.UserTeams> userTeams = teamRepository.findAll().stream()
                .filter(team -> team.getTeamMembers().contains(targetUserName) &&
                        team.getTeamVisibilityStatus().equalsIgnoreCase("public"))
                .map(team -> new UserDetailResponse.UserTeams(
                        team.getTeamName(),
                        team.getTeamOwner(),
                        team.getTeamDescription()
                ))
                .collect(Collectors.toList());

        userDetailResponse.setUserTeams(userTeams);

        return ResponseEntity.ok(userDetailResponse);
    }
    @Scheduled(cron = "0 0 9,17,22 * * *") // 9 AM, 5 PM, 10 PM daily
    @Async("taskExecutor")
    public void sendDailyProgressReportToAllUsers() {
        Set<String> userNames = userRepository.findAllUserNames();
        processReports(userNames, this::generateAndSendDailyProgressReport);
    }




    @Scheduled(cron = "0 0 20 * * THU") // 8 PM on Thursday (20 is 8 PM in 24 hour time)
    @Async("taskExecutor")
    public void sendWeeklyPerformanceReportToAllUsers() {
        Set<String> userNames = userRepository.findAllUserNames();
        processReports(userNames, this::generateAndSendWeeklyPerformanceReport);
    }

    private void processReports(Set<String> userNames, Consumer<String> reportProcessor) {
        for (String userName : userNames) {
            CompletableFuture.runAsync(() -> reportProcessor.accept(userName));
        }
    }

    @Async
    private void generateAndSendDailyProgressReport(String userName) {
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
    @Async
    private void generateAndSendWeeklyPerformanceReport(String userName) {
        try {
            ProgressReport report = AnalyticsEngine.generateProgressReport(userName);
            if (report != null) {
                progressReportRepository.save(report);
                String message = generateMessageDescriptionFromProgressReport(report);
                CollaborationEngine.sendProgressAndPerformanceReport(message, userName, true);
            }
        } catch (Exception e) {
            logError(e, "Weekly Performance Report", userName);
        }
    }

    private void logError(Exception e, String reportType, String userName) {
        System.err.println("Failed to generate " + reportType + " for user: " + userName);

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
    @Async
    public String generateMessageDescriptionFromPerformanceReport(
            UsersAccountStatistics accountStats,
            UsersFeedbackStatistics feedbackStats,
            UsersSessionStatistics sessionStats,
            UsersTaskStatistics taskStats,
            String userName) {

        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("Weekly Performance Report Summary for ").append(userName).append(":\n\n");

        // Account Statistics
        messageBuilder.append("Account Statistics:\n");
        messageBuilder.append("- Number of Users Following: ").append(accountStats.getNumberOfUserFollowing()).append("\n");
        messageBuilder.append("- Teams Participated: ").append(accountStats.getTeamsParticipated()).append("\n");
        messageBuilder.append("- Number of Tasks Participated: ").append(accountStats.getNumberOfTasksParticipated()).append("\n");
        messageBuilder.append("- Number of Sessions Created: ").append(accountStats.getNumberOfSessionsCreated()).append("\n");
        messageBuilder.append("- Number of Messages Sent: ").append(accountStats.getNumberOfMessagesSent()).append("\n");
        messageBuilder.append("- Number of Messages Received: ").append(accountStats.getNumberOfMessagesReceived()).append("\n");

        if (accountStats.getPreviousFeedbackScores() != null && !accountStats.getPreviousFeedbackScores().isEmpty()) {
            messageBuilder.append("- Previous Feedback Scores: ").append(accountStats.getPreviousFeedbackScores()).append("\n");
        }

        messageBuilder.append("\n");

        // Feedback Statistics
        messageBuilder.append("Feedback Statistics:\n");
        messageBuilder.append("- Feedback Count: ").append(feedbackStats.getFeedbackCount()).append("\n");
        messageBuilder.append("- Average Feedback Score: ").append(feedbackStats.getAverageFeedbackScore()).append("\n");

        if (feedbackStats.getFeedbackMessages() != null && !feedbackStats.getFeedbackMessages().isEmpty()) {
            messageBuilder.append("- Feedback Messages:\n");
            for (String feedbackMessage : feedbackStats.getFeedbackMessages()) {
                messageBuilder.append("  - ").append(feedbackMessage).append("\n");
            }
        }
        if (feedbackStats.getPreviousFeedbackScores() != null && !feedbackStats.getPreviousFeedbackScores().isEmpty()) {
            messageBuilder.append("- Previous Feedback Scores: ").append(feedbackStats.getPreviousFeedbackScores()).append("\n");
        }

        messageBuilder.append("\n");

        // Session Statistics
        messageBuilder.append("Session Statistics:\n");
        messageBuilder.append("- Number of Sessions: ").append(sessionStats.getNumberOfSession()).append("\n");
        messageBuilder.append("- Total Session Time: ").append(sessionStats.getTotalSessionTime()).append("\n");
        messageBuilder.append("- Average Session Efficiency: ").append(sessionStats.getAverageSessionEfficiency()).append("\n");
        messageBuilder.append("- Total Tasks Operated: ").append(sessionStats.getTotalTasksOperated()).append("\n");

        if (sessionStats.getSessionNames() != null && !sessionStats.getSessionNames().isEmpty()) {
            messageBuilder.append("- Session Names:\n");
            for (String sessionName : sessionStats.getSessionNames()) {
                messageBuilder.append("  - ").append(sessionName).append("\n");
            }
        }
        if (sessionStats.getPreviousSessionsEfficiencyScores() != null && !sessionStats.getPreviousSessionsEfficiencyScores().isEmpty()) {
            messageBuilder.append("- Previous Session Efficiency Scores: ").append(sessionStats.getPreviousSessionsEfficiencyScores()).append("\n");
        }

        messageBuilder.append("\n");

        // Task Statistics
        messageBuilder.append("Task Statistics:\n");
        if (taskStats.getTasksCompletedBeforeDeadline() != null && !taskStats.getTasksCompletedBeforeDeadline().isEmpty()) {
            messageBuilder.append("- Tasks Completed Before Deadline:\n");
            for (String taskName : taskStats.getTasksCompletedBeforeDeadline()) {
                messageBuilder.append("  - ").append(taskName).append("\n");
            }
        }
        if (taskStats.getTasksCompletedAfterDeadline() != null && !taskStats.getTasksCompletedAfterDeadline().isEmpty()) {
            messageBuilder.append("- Tasks Completed After Deadline:\n");
            for (String taskName : taskStats.getTasksCompletedAfterDeadline()) {
                messageBuilder.append("  - ").append(taskName).append("\n");
            }
        }
        if (taskStats.getDeadlineUncrossedAndUnfinishedTasks() != null && !taskStats.getDeadlineUncrossedAndUnfinishedTasks().isEmpty()) {
            messageBuilder.append("- Deadline Uncrossed and Unfinished Tasks:\n");
            for (String taskName : taskStats.getDeadlineUncrossedAndUnfinishedTasks()) {
                messageBuilder.append("  - ").append(taskName).append("\n");
            }
        }
        if (taskStats.getDeadlineCrossedAndUnfinishedTasks() != null && !taskStats.getDeadlineCrossedAndUnfinishedTasks().isEmpty()) {
            messageBuilder.append("- Deadline Crossed and Unfinished Tasks:\n");
            for (String taskName : taskStats.getDeadlineCrossedAndUnfinishedTasks()) {
                messageBuilder.append("  - ").append(taskName).append("\n");
            }
        }

        return messageBuilder.toString();
    }
    public static ResponseEntity<?> getSearchResult(String currentUserName, String keyWord) {
        // Find the current user
        User currentUser = userRepository.findByUserName(currentUserName);
        if (currentUser == null) {
            return ResponseEntity.badRequest().body("Current user not found");
        }

        // Get all users from the database
        List<User> allUsers = userRepository.findAll();

        // Prepare search results
        List<SearchResult> searchResults = new ArrayList<>();

        // Filter users based on the keyword
        for (User user : allUsers) {
            // Exclude users with accountVisibility "private" or if the user is the current user
            if (!user.getUserName().equals(currentUserName) && !user.getAccountVisibility().equals("private")) {
                // Check if the user matches the keyword in any field
                if (matchesKeyword(user, keyWord)) {
                    SearchResult result = new SearchResult();
                    result.setUserName(user.getUserName());
                    result.setUserEmail(user.getUserEmail());
                    result.setShortBioData(user.getShortBioData());
                    result.setRole(user.getRole());

                    // Check if the current user is following this user
                    result.setFollowing(currentUser.getUsersFollowing().contains(user.getUserName()));

                    // Calculate match score
                    result.setMatchScore(calculateMatchScore(keyWord, user));

                    searchResults.add(result);
                }
            }
        }

        // Sort results by match score in descending order
        searchResults.sort((a, b) -> Double.compare(b.getMatchScore(), a.getMatchScore()));

        return ResponseEntity.ok(searchResults);
    }

    // Helper method to check if a user matches the keyword
    private static boolean matchesKeyword(User user, String keyword) {
        String lowerCaseKeyword = keyword.toLowerCase();

        return user.getUserName().toLowerCase().contains(lowerCaseKeyword) ||
                user.getUserEmail().toLowerCase().contains(lowerCaseKeyword) ||
                (user.getShortBioData() != null &&
                        user.getShortBioData().toLowerCase().contains(lowerCaseKeyword));
    }

    // Helper method to calculate match score
    private static double calculateMatchScore(String keyword, User user) {
        double baseScore = 0.0;
        String lowerCaseKeyword = keyword.toLowerCase();

        // Exact match gets highest priority
        if (user.getUserName().equalsIgnoreCase(keyword)) {
            baseScore += 1.0;
        }

        // Partial username match
        if (user.getUserName().toLowerCase().contains(lowerCaseKeyword)) {
            baseScore += 0.8;
        }

        // Email match
        if (user.getUserEmail().toLowerCase().contains(lowerCaseKeyword)) {
            baseScore += 0.5;
        }

        // Bio data match
        if (user.getShortBioData() != null &&
                user.getShortBioData().toLowerCase().contains(lowerCaseKeyword)) {
            baseScore += 0.3;
        }

        return baseScore;
    }
}
