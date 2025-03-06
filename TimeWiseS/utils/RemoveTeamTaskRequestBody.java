package com.TimeWise.utils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RemoveTeamTaskRequestBody {
    private String teamName;
    private String taskName;
}
