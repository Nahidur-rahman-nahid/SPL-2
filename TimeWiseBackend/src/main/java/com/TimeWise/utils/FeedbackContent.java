package com.TimeWise.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackContent {
    private String feedbackTaskName;
    private Integer feedbackScore; // 0.0 to 100.0
    private String feedbackMessage;
}
