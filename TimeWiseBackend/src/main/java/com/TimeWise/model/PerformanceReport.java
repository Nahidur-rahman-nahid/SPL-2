package com.TimeWise.model;


import com.TimeWise.utils.UsersAccountStatistics;
import com.TimeWise.utils.UsersFeedbackStatistics;
import com.TimeWise.utils.UsersSessionStatistics;
import com.TimeWise.utils.UsersTaskStatistics;
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