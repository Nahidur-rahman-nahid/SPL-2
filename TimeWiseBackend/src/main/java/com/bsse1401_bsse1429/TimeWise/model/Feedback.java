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
public class Feedback {
    @Id
    private ObjectId feedbackId;
    private String feedbackSender;
    private Set<String> feedbackRecipients;
    private String feedbackTaskName;
    private Integer feedbackScore; // 0.0 to 100.0
    private String feedbackMessage;
    private Date timeStamp;
}
