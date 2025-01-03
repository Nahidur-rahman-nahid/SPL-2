package com.bsse1401_bsse1429.TimeWise.utils;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;


@Getter
@Setter
public class TaskModificationRequestBody {
    private ObjectId taskId;
    private String fieldName;
    private String updatedBy;
    private Object newValue;
}
