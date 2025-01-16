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
public class ProgressReport {
    @Id
    private ObjectId progressReportId;
    private String userName;
    private List<TaskStatus> taskStatuses;
    private Date timeStamp;

    // Method to add a comment
    public void addTaskStatuses(String taskName,String taskOwner,String taskPriority, Integer taskCurrentProgress,Date taskCreationDate,Date taskDeadline) {
        TaskStatus taskStatus = new TaskStatus(taskName,taskOwner,taskPriority, taskCurrentProgress,taskCreationDate, taskDeadline);
        this.taskStatuses.add(taskStatus);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TaskStatus {
        private String taskName;
        private String taskOwner;
        private String taskPriority;
        private Integer tasksCurrentProgress;
        private Date taskCreationDate;
        private Date taskDeadline;
    }
}
