package com.TimeWise.model;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    private ObjectId userId;
    private String userName;
    private String userEmail;
    private String password;
    private String shortBioData;
    private String role;
    private String userStatus;
    private Set<String> usersFollowing;

}
