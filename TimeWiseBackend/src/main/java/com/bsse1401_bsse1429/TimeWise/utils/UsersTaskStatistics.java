package com.bsse1401_bsse1429.TimeWise.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsersTaskStatistics {
    private List<String> tasksCompletedBeforeDeadline;
    private List<String> tasksCompletedAfterDeadline;
    private List<String> deadlineUncrossedAndUnfinishedTasks;
    private List<String> deadlineCrossedAndUnfinishedTasks;
}
