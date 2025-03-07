package com.TimeWise.controller;

import com.TimeWise.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {
    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping("/deep-work-hours")
    public ResponseEntity<?> getDeepWorkingHoursAnalytics(@RequestParam Integer previousNumberOfDays) {
        return ResponseEntity.ok(analyticsService.getDeepWorkHoursAnalytics(previousNumberOfDays));
    }
}
