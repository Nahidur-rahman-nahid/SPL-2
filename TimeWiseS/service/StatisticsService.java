package com.TimeWise.service;

import com.TimeWise.engine.StatisticsEngine;
import com.TimeWise.repository.SessionRepository;
import com.TimeWise.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StatisticsService {


    public UsersFeedbackStatistics getUsersFeedBackStatistics(Integer previousNumberOfDays){
        String  currentUser= UserCredentials.getCurrentUsername();
        return StatisticsEngine.calculateFeedbackStatistics(currentUser,previousNumberOfDays);
    }

    public UsersSessionStatistics getUsersSessionStatistics(Integer previousNumberOfDays){
        String  currentUser= UserCredentials.getCurrentUsername();
        return StatisticsEngine.calculateSessionStatistics(currentUser,previousNumberOfDays);
    }
    public UsersTaskStatistics getUsersTaskStatistics(Integer previousNumberOfDays){
        String  currentUser= UserCredentials.getCurrentUsername();
        return StatisticsEngine.calculateTaskStatistics(currentUser,previousNumberOfDays);
    }

    public UsersAccountStatistics getUsersAccountStatistics(Integer previousNumberOfDays){
        String  currentUser= UserCredentials.getCurrentUsername();
        return StatisticsEngine.calculateUserAccountStatistics(currentUser,previousNumberOfDays);
    }


}
