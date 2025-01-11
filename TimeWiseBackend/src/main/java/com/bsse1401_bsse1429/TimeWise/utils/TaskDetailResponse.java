package com.bsse1401_bsse1429.TimeWise.utils;


import lombok.*;

import java.util.Date;
import java.util.Set;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public  class TaskDetailResponse {
        private String taskName;
        private String taskCategory;
        private Date taskCreationDate;
        private String taskOwner;
        private String taskGoal;
        private Set<String> taskParticipants;
    }

