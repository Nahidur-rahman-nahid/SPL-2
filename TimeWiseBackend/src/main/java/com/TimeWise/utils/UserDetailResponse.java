package com.TimeWise.utils;

import lombok.*;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;
import java.util.Set;


@Data
@NoArgsConstructor
@AllArgsConstructor
public  class UserDetailResponse {
    private String userName;
    private String userEmail;
    private String shortBioData;
    private String role;
    private String userStatus;
    private List<UserTasks> userTasks;
    private List<UserTeams> userTeams;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserTasks {
        private String taskName;
        private String taskOwner;
        private String taskCategory;
        private String taskDescription;
        private List<String> subTasks;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserTeams {
        private String teamName;
        private String teamOwner;
        private String teamDescription;
    }
}

