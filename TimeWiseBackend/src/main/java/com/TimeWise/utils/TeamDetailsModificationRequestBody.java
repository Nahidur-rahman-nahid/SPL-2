package com.TimeWise.utils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeamDetailsModificationRequestBody {
    private String teamName;
    private String teamDescription;
    private String teamVisibilityStatus;
    private String previousTeamName;
}
