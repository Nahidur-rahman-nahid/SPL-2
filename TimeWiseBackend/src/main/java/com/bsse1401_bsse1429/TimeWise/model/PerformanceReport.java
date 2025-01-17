package com.bsse1401_bsse1429.TimeWise.model;


import com.bsse1401_bsse1429.TimeWise.utils.UsersAccountStatistics;
import com.bsse1401_bsse1429.TimeWise.utils.UsersFeedbackStatistics;
import com.bsse1401_bsse1429.TimeWise.utils.UsersSessionStatistics;
import com.bsse1401_bsse1429.TimeWise.utils.UsersTaskStatistics;
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
public class PerformanceReport {
    @Id
    private ObjectId performanceReportId;
    private String userName;
    private UsersAccountStatistics usersAccountStatistics;
    private UsersTaskStatistics usersTaskStatistics;
    private UsersSessionStatistics usersSessionStatistics;
    private UsersFeedbackStatistics usersFeedbackStatistics;
    private Date reportGeneratedDate; 

}
