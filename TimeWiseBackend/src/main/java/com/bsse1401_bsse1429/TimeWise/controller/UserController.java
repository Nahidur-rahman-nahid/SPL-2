package com.bsse1401_bsse1429.TimeWise.controller;

import com.bsse1401_bsse1429.TimeWise.engine.CollaborationEngine;
import com.bsse1401_bsse1429.TimeWise.model.User;
import com.bsse1401_bsse1429.TimeWise.service.SystemService;
import com.bsse1401_bsse1429.TimeWise.service.UserService;
import com.bsse1401_bsse1429.TimeWise.utils.NotificationRequestBody;
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
    public ResponseEntity<?> completeRegistration(@RequestBody User user,@RequestParam String code) {
        return userService.completeRegistration(user,code);
    }

    @PostMapping("/login")
    public ResponseEntity<?> userLogin(@RequestBody User user) {

        return userService.userLogin(user);
    }


    @GetMapping("/login/forgot")
    public ResponseEntity<?> forgotUserCredentials(@RequestParam String userEmail) {
        return userService.forgotUserCredentials(userEmail);
    }
    @PostMapping("/login/forgot/verify")
    public ResponseEntity<?> forgottenAccountVerification(@RequestParam String code,@RequestParam String userEmail) {
        return userService.forgottenAccountVerification(code,userEmail);
    }

    @GetMapping("/details")
    public ResponseEntity<?> getUserDetails(@PathVariable String userName) {
        return userService.getUserDetails(userName);
    }




//    @PostMapping("/sendresponse")
//    public String sendNotification(@RequestBody NotificationRequestBody.SendNotification notificationBody) {
//
//        return service.sendNotification(notificationBody);
//    }

    @GetMapping("/check")
    public String check() {

        return "Hi there";

    }
}
