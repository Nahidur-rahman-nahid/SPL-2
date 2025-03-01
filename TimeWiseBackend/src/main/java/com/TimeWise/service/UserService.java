package com.TimeWise.service;

import com.TimeWise.engine.CollaborationEngine;
import com.TimeWise.model.User;
import com.TimeWise.repository.UserRepository;
import com.TimeWise.utils.UpdatedUserAccount;
import com.TimeWise.utils.UserCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserService {


    @Autowired
    private UserRepository userRepository;

    // private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    // Register a new user and generate a token
    public ResponseEntity<?> initiateRegistration(User user) {
        return SystemService.checkRegistrationCredentialsAndSendRegistrationVerificationCode(user);
    }
    public ResponseEntity<?> completeRegistration(User user, String code) {
        return SystemService.verifyVerificationCodeAndCompleteRegistration(user,code);
    }
    public ResponseEntity<?> userLogin(User user) {
        return SystemService.performUserLogin(user);
    }

    public ResponseEntity<?> forgotUserCredentials(String userEmail) {
        return SystemService.handleForgotUserCredentials(userEmail);
    }

    public ResponseEntity<?> forgottenAccountVerification(String code,String userEmail) {
        return SystemService.verifyVerificationCodeForAccountVerification(code,userEmail);
    }

    public ResponseEntity<?> getUserDetails(String userName) {
        String  currentUserName= UserCredentials.getCurrentUsername();
        return SystemService.getUserAccountDetails(currentUserName,userName);
    }



    public ResponseEntity<?> getUsersPersonalAccountDetails() {
        String  currentUserName= UserCredentials.getCurrentUsername();
        User user=userRepository.findByUserName(currentUserName);
        if(user==null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }
        user.setUserId(null);
        user.setPassword(null);
        return ResponseEntity.ok(user);
    }

    public ResponseEntity<?> updateUserAccountDetails(UpdatedUserAccount updatedUserDetails) {
        String  currentUserName= UserCredentials.getCurrentUsername();
        return SystemService.updateUserAccountDetails(currentUserName,updatedUserDetails);
    }

    public ResponseEntity<?> getAllNotifications() {
        String  currentUserName= UserCredentials.getCurrentUsername();
        return CollaborationEngine.getUsersAllNotification(currentUserName);
    }


    // Update user details





//    public ResponseEntity<?> resetPassword(String email, String verificationCode, String newPassword) {
//        User user = userRepository.findByEmail(email);
//        if (user == null) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
//        }
//
//        // Verify the code and check if it's expired
//        if (user.getVerificationCode() == null ||
//                !user.getVerificationCode().equals(verificationCode) ||
//                user.getVerificationCodeExpiry().before(new Date())) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired verification code");
//        }
//
//        // Reset the password
//        user.setPassword(newPassword);
//        user.setVerificationCode(null); // Invalidate the code
//        user.setVerificationCodeExpiry(null);
//
//        userRepository.save(user);
//
//        return ResponseEntity.ok("Password reset successfully.");
//    }


}