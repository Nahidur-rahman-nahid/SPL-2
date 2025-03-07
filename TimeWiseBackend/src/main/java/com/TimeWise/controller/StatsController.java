package com.TimeWise.controller;

import com.TimeWise.engine.StatisticsEngine;
import com.TimeWise.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/statistics")
public class StatsController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/account")
    public ResponseEntity<?> getUsersAccountStatistics(@RequestParam Integer previousNumberOfDays) {
        return ResponseEntity.ok(statisticsService.getUsersAccountStatistics(previousNumberOfDays));
    }

    @GetMapping("/session")
    public ResponseEntity<?> getUsersSessionStatistics(@RequestParam Integer previousNumberOfDays) {
        return ResponseEntity.ok(statisticsService.getUsersSessionStatistics(previousNumberOfDays));
    }

    @GetMapping("/task")
    public ResponseEntity<?> getUsersTaskStatistics(@RequestParam Integer previousNumberOfDays) {
        return ResponseEntity.ok(statisticsService.getUsersTaskStatistics(previousNumberOfDays));
    }

    @GetMapping("/feedback")
    public ResponseEntity<?> getUsersFeedbackStatistics(@RequestParam Integer previousNumberOfDays) {
        return ResponseEntity.ok(statisticsService.getUsersFeedBackStatistics(previousNumberOfDays));
    }
}
