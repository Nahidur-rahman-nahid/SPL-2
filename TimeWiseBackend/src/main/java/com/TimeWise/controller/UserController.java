package com.TimeWise.controller;

import com.TimeWise.model.User;
import com.TimeWise.service.SystemService;
import com.TimeWise.service.UserService;
import com.TimeWise.utils.LoginCredentials;
import com.TimeWise.utils.TimeWiseRegainAccountCredentials;
import com.TimeWise.utils.UpdatedUserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register/request")
    public ResponseEntity<?> initiateRegistration(@RequestBody User user) {
        return userService.initiateRegistration(user);
    }

    @PostMapping("/register/complete")
    public ResponseEntity<?> completeRegistration(@RequestBody User user, @RequestParam String code) {
        return userService.completeRegistration(user,code);
    }

    @PostMapping("/login")
    public ResponseEntity<?> userLogin(@RequestBody LoginCredentials loginCredentials) {

        return userService.userLogin(loginCredentials);
    }


    @GetMapping("/login/forgot")
    public ResponseEntity<?> forgotUserCredentials(@RequestParam String userEmail) {
        return userService.forgotUserCredentials(userEmail);
    }
    @PostMapping("/login/forgot/verify")
    public ResponseEntity<?> forgottenAccountVerification(@RequestBody TimeWiseRegainAccountCredentials requestBody) {

        return userService.forgottenAccountVerification(requestBody.getCode(),requestBody.getUserEmail(),requestBody.getUserName(),requestBody.getUpdatedPassword());
    }


    @GetMapping("/account/details")
    public ResponseEntity<?> getAccountDetails(@RequestParam(required = false) String userName) {
        return userService.getUserDetails(userName);
    }

    @PutMapping("/account/update")
    public ResponseEntity<?> updateUser( @RequestBody UpdatedUserAccount updatedUserDetails) {
        return  userService.updateUserAccountDetails(updatedUserDetails);
    }

    @GetMapping("/notifications/all")
    public ResponseEntity<?> getAllNotifications() {
        return userService.getAllNotifications();
    }

    @GetMapping("/check")
    public String check() {

        return "Hi there";

    }
}
