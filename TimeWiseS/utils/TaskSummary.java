package com.TimeWise.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskSummary  {
    private String taskName;
    private String taskCategory;
    private Date taskCreationDate;
    private String taskDescription;
    private String taskGoal;
    private String taskPriority;
    private Date taskDeadline;
    private String taskOwner;
    private Integer taskCurrentProgress;

}
