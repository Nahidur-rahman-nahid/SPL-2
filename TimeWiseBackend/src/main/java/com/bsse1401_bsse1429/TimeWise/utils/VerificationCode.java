package com.bsse1401_bsse1429.TimeWise.utils;

import com.bsse1401_bsse1429.TimeWise.model.User;
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
public class VerificationCode {
    @Id
    private ObjectId verificationCodeId;
    private String code;
    private String userName;
    private String userEmail;
    private Date expiry;

    public VerificationCode(String code, String userName, String userEmail, Date expiry) {
        this.code = code;
        this.userName = userName;
        this.userEmail = userEmail;
        this.expiry = expiry;
    }
}

