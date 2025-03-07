package com.TimeWise.utils;

import com.TimeWise.model.Task;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeleteTaskNoteRequestBody {
    private String taskName;
    private String taskOwner;
    private Task.Note taskNote;
}