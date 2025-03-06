package com.TimeWise.utils;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;

// TaskNoteRequest class
@Getter
@Setter
public class AddTaskNoteRequestBody {
    private ObjectId taskId;
    private String taskNote;
}
