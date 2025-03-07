package com.TimeWise.utils;

import com.TimeWise.model.Task;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

@Getter
@Setter
public class GeneratedTask {
    private String taskName;
    private String taskCategory;
    private String taskDescription;
    private String taskPriority;
    private String taskVisibilityStatus;
    private Date taskDeadline;
    private String taskGoal;
    private List<String> taskTodos;

}
