package com.bsse1401_bsse1429.TimeWise.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeepWorkHours {
    private  String timeSlot; // 1,2,3...23
    private Double totalHoursWorked;
    private Integer totalTasksOperated;
    private Double totalEfficiencyScore;
    private Integer sessionCount ;
    private Double averageEfficiencyScore ;

}
