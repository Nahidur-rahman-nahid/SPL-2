package com.TimeWise.controller;

import com.TimeWise.model.Session;
import com.TimeWise.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/report")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/progress/current")
    public ResponseEntity<?> getCurrentProgressReport() {
        return ResponseEntity.ok(reportService.getCurrentProgressReport());
    }
    @GetMapping("/progress/previous")
    public ResponseEntity<?> getPreviousProgressReports() {
        return ResponseEntity.ok(reportService.getPreviousProgressReports());
    }


}
