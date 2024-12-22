package com.bsse1401_bsse1429.TimeWise.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
class Progress {
    @Id
    private ObjectId progressId;
    private ObjectId userId;
    private ObjectId taskId;
    private Double progressAmount;
    private List<String> progressHistory;
    private String progressReport;
}
