package com.TimeWise.utils;


import lombok.*;

import java.util.Date;
import java.util.Set;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public  class TaskProfile {
        private String taskName;
        private String taskCategory;
        private Date taskCreationDate;
        private String taskDescription;
        private String taskGoal;
    }

