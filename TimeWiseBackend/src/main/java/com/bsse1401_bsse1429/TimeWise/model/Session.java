package com.bsse1401_bsse1429.TimeWise.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
class Session {
    @Id
    private ObjectId sessionId;
    private String userName;
    private Date sessionStartTime;
    private Date sessionEndTime;
    private Double duration;
    private String sessionSummary;
    private List<ObjectId> tasksOperated;
}
