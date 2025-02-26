package com.TimeWise.utils;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;

@Getter
@Setter
public class TaskTodoRequestBody {
    private String taskName;
    private String taskOwner;
    private String taskTodo;
}