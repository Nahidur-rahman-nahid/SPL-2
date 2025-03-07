package com.TimeWise.service;

import com.TimeWise.engine.AnalyticsEngine;
import com.TimeWise.utils.DeepWorkHours;
import com.TimeWise.utils.UserCredentials;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnalyticsService {
    public List<DeepWorkHours> getDeepWorkHoursAnalytics(Integer previousNumberOfDays){
        String  currentUser= UserCredentials.getCurrentUsername();
        return AnalyticsEngine.getUsersDeepWorkingHours(currentUser,previousNumberOfDays);
    }
}
