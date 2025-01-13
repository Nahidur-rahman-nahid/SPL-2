package com.bsse1401_bsse1429.TimeWise.service;

import ch.qos.logback.classic.encoder.JsonEncoder;
import com.bsse1401_bsse1429.TimeWise.engine.CollaborationEngine;
import com.bsse1401_bsse1429.TimeWise.model.Notification;
import com.bsse1401_bsse1429.TimeWise.repository.UserRepository;
import com.bsse1401_bsse1429.TimeWise.repository.TeamRepository;
import com.bsse1401_bsse1429.TimeWise.repository.TaskRepository;
import com.bsse1401_bsse1429.TimeWise.repository.VerificationCodeRepository;
import com.bsse1401_bsse1429.TimeWise.utils.UserDetailResponse;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.bsse1401_bsse1429.TimeWise.model.User;
import com.bsse1401_bsse1429.TimeWise.utils.VerificationCode;

import java.util.*;
import java.util.stream.Collectors;

@Service
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
    private VerificationCodeRepository verificationCodeRepositoryInstance;

    private static JWTService jwtService;
    private static AuthenticationManager authManager;
    private static UserRepository userRepository;
    private static TaskRepository taskRepository;
    private static TeamRepository teamRepository;
    private static VerificationCodeRepository verificationCodeRepository;

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    @PostConstruct
    private void initStaticDependencies() {
        jwtService = jwtServiceInstance;
        authManager = authManagerInstance;
        userRepository = userRepositoryInstance;
        taskRepository = taskRepositoryInstance;
        teamRepository = teamRepositoryInstance;
        verificationCodeRepository = verificationCodeRepositoryInstance;
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
        VerificationCode verificationCode=generateVerificationCode(user);
        verificationCodeRepository.deleteByUserNameOrUserEmail(user.getUserName(),user.getUserEmail());
        // Save verification code
        verificationCodeRepository.save(verificationCode);


        return ResponseEntity.ok(CollaborationEngine.sendUserRegistrationVerificationCode(user.getUserName(),user.getUserEmail(),verificationCode.getCode()));
    }

    public static ResponseEntity<?> verifyVerificationCodeAndCompleteRegistration(User user, String code) {
        // Check if the verification code is valid and not expired
        VerificationCode verificationCode = verificationCodeRepository.findByUserEmail(user.getUserEmail());
        if (verificationCode == null ) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No verification code found for this email");
        }
        if(verificationCode.getExpiry().before(new Date())){
            verificationCodeRepository.delete(verificationCode);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Expired verification code.");
        }
        if(!verificationCode.getCode().equals(code)){
            verificationCodeRepository.delete(verificationCode);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Wrong verification code.Previous code became useless. Ask for new code if required. ");
        }

        // Verify email matches the user's email
        if (!user.getUserEmail().equals(verificationCode.getUserEmail()) || !user.getUserName().equals(verificationCode.getUserName())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Name or Email mismatch during verification.Please send the same credentials as before getting the verification code");
        }

        // Check if username or email already exists (revalidation)
        if (userRepository.findByUserName(user.getUserName())!=null) {
            verificationCodeRepository.delete(verificationCode);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Sorry you have to register again with another name as in the mean time a new user with that user name registered");
        }
//        if (userRepository.existsByEmail(user.getEmail())) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email already exists.");
//        }


        // Remove the verification code from the database
        verificationCodeRepository.delete(verificationCode);
        // Save the user in the database
        String rawPassword = user.getPassword();

        user.setPassword(encoder.encode(rawPassword));
        user.setUserStatus("Active");
        user.setTodos(new ArrayList<>());
        user.setUsersFollowing(new HashSet<>());
        if(user.getRole()==null){
            user.setRole("User");
        }
        if(user.getShortBioData()==null){
            user.setShortBioData("Myself "+user.getUserName());
        }

        userRepository.save(user);


        String response = CollaborationEngine.sendRegistrationSuccessfulMessage(user.getUserName(),user.getUserEmail());

        // Log saved data
        System.out.println("User saved: " + user);

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

                 return ResponseEntity.ok("User logged in  successfully! Token: "+jwtService.generateToken(authenticatedUser.getUserId()));
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
        VerificationCode verificationCode=new VerificationCode(code,"User",userEmail,new Date(System.currentTimeMillis() + (7 * 60 * 1000)));

        verificationCodeRepository.deleteByUserEmail(userEmail);
        // Save verification code
        verificationCodeRepository.save(verificationCode);

        String response=CollaborationEngine.sendAccountVerificationCode(userEmail,verificationCode.getCode());

        return ResponseEntity.ok(response);

    }
    public static ResponseEntity<?> verifyVerificationCodeForAccountVerification(String code,String userEmail) {
        // Check if the verification code is valid and not expired
        VerificationCode verificationCode = verificationCodeRepository.findByUserEmail(userEmail);
        if (verificationCode == null ) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No verification code found for this email");
        }
        if(verificationCode.getExpiry().before(new Date())){
            verificationCodeRepository.delete(verificationCode);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Expired verification code.");
        }
        if(!verificationCode.getCode().equals(code)){
            verificationCodeRepository.delete(verificationCode);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Wrong verification code.Previous code became useless. Ask for new code if required. ");
        }
        return ResponseEntity.ok(userRepository.findByUserEmail(userEmail));

    }

    private static VerificationCode generateVerificationCode(User user) {
        String code = UUID.randomUUID().toString().substring(0, 6).toUpperCase(); // Generate a 6-character code

        return new VerificationCode(
                code,
                user.getUserName(),
                user.getUserEmail(),
                new Date(System.currentTimeMillis() + (7 * 60 * 1000)) // 7 minutes expiry
        );

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

            // Fetch tasks where user is a participant or tasks marked as public
            List<UserDetailResponse.UserTasks> userTasks = taskRepository.findAll().stream()
                    .filter(task ->
                            task.getTaskParticipants().contains(userName) ||
                                    "public".equalsIgnoreCase(task.getTaskVisibilityStatus()))
                    .map(task -> new UserDetailResponse.UserTasks(
                            task.getTaskName(),
                            task.getTaskCategory(),
                            task.getTaskDescription()
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

}
