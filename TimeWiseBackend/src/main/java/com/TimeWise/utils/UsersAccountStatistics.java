package com.TimeWise.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsersAccountStatistics {
    private Long numberOfUserFollowing;
    private Long teamsParticipated;
    private Long numberOfTasksParticipated;
    private Long numberOfSessionsCreated;
    private List<Integer> previousFeedbackScores;
    private Long numberOfMessagesSent;
    private Long numberOfMessagesReceived;

}
