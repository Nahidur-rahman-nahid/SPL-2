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
class Session {
    @Id
    private ObjectId sessionId;
    private ObjectId userId;
    private Date sessionStartTime;
    private Date sessionEndTime;
    private Double duration;
    private String activitySummary;
}
