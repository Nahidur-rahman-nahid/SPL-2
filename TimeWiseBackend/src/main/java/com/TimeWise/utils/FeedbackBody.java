package com.TimeWise.utils;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class FeedbackBody {

    private String feedbackRecipient;
    private String feedbackTaskName;
    private Integer feedbackScore; // 0.0 to 100.0

    private String feedbackMessage;

}
