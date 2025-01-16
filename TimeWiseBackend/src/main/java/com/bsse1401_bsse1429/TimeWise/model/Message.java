package com.bsse1401_bsse1429.TimeWise.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.Set;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    @Id
    private ObjectId messageId;
    private String sender; // Username of the sender
    private Set<String> recipients; // Usernames of the recipients
    private String messageSubject;
    private String messageDescription;
    private String messageStatus; // Seen, Unseen
    private Date timeStamp;

}
