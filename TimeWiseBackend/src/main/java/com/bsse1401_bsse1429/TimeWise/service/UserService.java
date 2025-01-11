package com.bsse1401_bsse1429.TimeWise.service;

import com.bsse1401_bsse1429.TimeWise.engine.CollaborationEngine;
import com.bsse1401_bsse1429.TimeWise.model.User;
import com.bsse1401_bsse1429.TimeWise.repository.UserRepository;
import com.bsse1401_bsse1429.TimeWise.utils.NotificationRequestBody;
import com.bsse1401_bsse1429.TimeWise.utils.UserDetailResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {


    @Autowired
    private UserRepository userRepository;

   // private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    // Register a new user and generate a token
    public ResponseEntity<?> initiateRegistration(User user) {
        return SystemService.checkRegistrationCredentialsAndSendRegistrationVerificationCode(user);
    }
    public ResponseEntity<?> completeRegistration(User user,String code) {
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
       return SystemService.getUserAccountDetails(userName);
    }




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