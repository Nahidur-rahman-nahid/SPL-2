package com.bsse1401_bsse1429.TimeWise.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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
public class Session {
    @Id
    private ObjectId sessionId;
    private String sessionCreator;
    private Date sessionTimeStamp;
    private Double duration;
    private String sessionGoal;
    private String sessionSummary;
    private Integer sessionEfficiency;
    private Set<String> tasksOperated;
}
