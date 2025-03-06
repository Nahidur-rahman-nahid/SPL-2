package com.TimeWise.model;

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
public class Session {
    @Id
    private ObjectId sessionId;
    private String sessionCreator;
    private Date sessionTimeStamp;
    private Integer duration;
    private String sessionGoal;
    private String sessionOutcome;
    private Integer sessionEfficiency;
    private Set<String> tasksOperated;
}
