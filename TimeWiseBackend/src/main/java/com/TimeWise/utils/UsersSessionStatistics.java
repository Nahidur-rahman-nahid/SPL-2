package com.TimeWise.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsersSessionStatistics {
    private Integer numberOfSession;
    private Double totalSessionTime;
    private Double averageSessionEfficiency;
    private Integer totalTasksOperated;
    private List<String> sessionNames;
    private List<Integer> previousSessionsEfficiencyScores;
}
