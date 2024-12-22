package com.bsse1401_bsse1429.TimeWise.model;

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
class Notification {
    @Id
    private ObjectId notificationId;
    private ObjectId userId;
    private String notificationType;
    private String notificationMessage;
    private String notificationStatus;
    private Date timestamp;
}