package com.TimeWise.utils;

import com.TimeWise.model.Task;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;

import java.util.List;

@Getter
@Setter
public class TaskTodoStatusModificationRequestBody {
    private String taskName;
    private String taskOwner;
    private Task.TaskTodo updatedTaskTodoStatus;
}
