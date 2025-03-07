package com.TimeWise.utils;

import com.TimeWise.model.Task;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;
import java.util.Set;


@Getter
@Setter
public class TaskDetailsModificationRequestBody {
    private String taskName;
    private String taskCategory;
    private String taskDescription;
    private String taskPriority;
    private String taskVisibilityStatus;
    private Date taskDeadline;
    private String taskGoal;
    private Set<String> taskParticipants;
    private Set<String> invitedMembers;
    private List<Task.Comment> taskComments;
    private List<Task.TaskModification> taskModificationHistory;
    private List<Task.TaskTodo> taskTodos;

}
