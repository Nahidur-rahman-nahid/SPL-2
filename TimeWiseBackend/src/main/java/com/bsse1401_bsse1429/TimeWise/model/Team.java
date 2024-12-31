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
class Team {
    @Id
    private ObjectId teamId;
    private String teamName;
    private List<String> teamMembers;
    private String teamOwner;
    private Date creationDate;
    private String teamStatus;
}