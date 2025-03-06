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
    @Getter
    private String password;
    private String shortBioData;
    private String role;
    private String userStatus; // active or inactive(logged out or manual inactive setting)
    private List<Todo> todos;
    private List<Note> notes;
    private Set<String> usersFollowing;


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Todo {
        private String description;
        private String status; // complete or incomplete
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Note {
        private String description;
        private Date timeStamp; // complete or incomplete
    }

}
