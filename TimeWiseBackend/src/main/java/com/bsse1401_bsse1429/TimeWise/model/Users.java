package com.bsse1401_bsse1429.TimeWise.model;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Users {
    @Id
    private ObjectId userId;
    private String username;
    private String email;
    private String password;
    private String shortBiodata;
    private String role;
    private String userStatus;

}
