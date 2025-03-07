package com.TimeWise.utils;

import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Getter
@Setter
public class GenerateTaskRequestBody {
    private String goalName;
    private Date goalDeadline;
    private Integer numberOfTask;
    private String userPrompt;
}
