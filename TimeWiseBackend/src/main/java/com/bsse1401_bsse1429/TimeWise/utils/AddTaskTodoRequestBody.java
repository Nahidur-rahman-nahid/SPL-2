package com.bsse1401_bsse1429.TimeWise.utils;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;

@Getter
@Setter
public class AddTaskTodoRequestBody {
    private ObjectId taskId;
    private String taskTodo;
}