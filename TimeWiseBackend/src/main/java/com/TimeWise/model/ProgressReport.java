package com.TimeWise.model;

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
    private List<com.TimeWise.model.ProgressReport.TaskStatus> taskStatuses;
    private Date timeStamp;

    // Method to add a task status
    public void addTaskStatus(com.TimeWise.model.ProgressReport.TaskDetails taskDetails) {
        com.TimeWise.model.ProgressReport.TaskStatus taskStatus = new com.TimeWise.model.ProgressReport.TaskStatus(
                taskDetails.getTaskName(),
                taskDetails.getTaskOwner(),
                taskDetails.getTaskPriority(),
                taskDetails.getTaskCurrentProgress(),
                taskDetails.getTaskCreationDate(),
                taskDetails.getTaskDeadline(),
                taskDetails.getIsDeadlineCrossed()
        );
        this.taskStatuses.add(taskStatus);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TaskStatus {
        private String taskName;
        private String taskOwner;
        private String taskPriority;
        private Integer taskCurrentProgress;
        private Date taskCreationDate;
        private Date taskDeadline;
        private Boolean isDeadlineCrossed;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TaskDetails {
        private String taskName;
        private String taskOwner;
        private String taskPriority;
        private Integer taskCurrentProgress;
        private Date taskCreationDate;
        private Date taskDeadline;
        private Boolean isDeadlineCrossed;
    }
}