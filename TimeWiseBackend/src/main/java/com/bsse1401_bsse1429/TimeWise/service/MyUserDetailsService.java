package com.bsse1401_bsse1429.TimeWise.service;

import com.bsse1401_bsse1429.TimeWise.model.UserPrincipal;
import com.bsse1401_bsse1429.TimeWise.model.User;
import com.bsse1401_bsse1429.TimeWise.repository.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("Insde load user name .");
        User user = userRepo.findByUserName(username); // Ensure this method exists in the repository
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        return new UserPrincipal(user);
    }

    public UserDetails loadUserByUserId(String userId) throws UsernameNotFoundException {
        System.out.println("Insde load user iD .");
        // Convert the string userId to an ObjectId
        ObjectId objectId;
        try {
            objectId = new ObjectId(userId);
        } catch (IllegalArgumentException e) {
            throw new UsernameNotFoundException("Invalid userId format");
        }

        // Query the user from the repository
        User user = userRepo.findByUserId(objectId);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with ID: " + userId);
        }

        return new UserPrincipal(user);
    }
}
