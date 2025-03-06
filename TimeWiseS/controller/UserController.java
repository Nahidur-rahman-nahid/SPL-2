package com.TimeWise.controller;

import com.TimeWise.model.User;
import com.TimeWise.service.SystemService;
import com.TimeWise.service.UserService;
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


    @GetMapping("/account/other/details")
    public ResponseEntity<?> getAnotherUserAccountDetails(@PathVariable String userName) {
        return userService.getUserDetails(userName);
    }
    @GetMapping("/account/personal/details")
    public ResponseEntity<?> getUsersPersonalAccountDetails() {
        return userService.getUsersPersonalAccountDetails();
    }

    @PutMapping("/account/update")
    public ResponseEntity<?> updateUser(@PathVariable String userName, @RequestBody UpdatedUserAccount updatedUserDetails) {
        return SystemService.updateUserAccounDetails(userName,updatedUserDetails);
    }


    @GetMapping("/check")
    public String check() {

        return "Hi there";

    }
}
