package com.bsse1401_bsse1429.TimeWise.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsersFeedbackStatistics {
    private Integer feedbackCount;
    private Double averageFeedbackScore;
    private List<String> feedbackMessages;
}
