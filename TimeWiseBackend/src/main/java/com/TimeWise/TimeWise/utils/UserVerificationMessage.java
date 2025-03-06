package com.TimeWise.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserVerificationMessage {
    @Id
    private ObjectId verificationCodeId;
    private String code;
    private String userName;
    private String userEmail;
    private Date expiry;

    public UserVerificationMessage(String code, String userName, String userEmail, Date expiry) {
        this.code = code;
        this.userName = userName;
        this.userEmail = userEmail;
        this.expiry = expiry;
    }
}

