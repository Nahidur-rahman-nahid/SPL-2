package com.TimeWise.service;

import com.TimeWise.engine.AnalyticsEngine;
import com.TimeWise.model.PerformanceReport;
import com.TimeWise.model.ProgressReport;
import com.TimeWise.repository.PerformanceReportRepository;
import com.TimeWise.repository.ProgressReportRepository;
import com.TimeWise.utils.UserCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportService {

    @Autowired
    private ProgressReportRepository progressReportRepository;

    @Autowired
    private PerformanceReportRepository performanceReportRepository;

   public ProgressReport getCurrentProgressReport() {
       String  currentUser= UserCredentials.getCurrentUsername();
       return AnalyticsEngine.generateProgressReport(currentUser);
   }

    public List<ProgressReport> getPreviousProgressReports() {
        String  currentUser= UserCredentials.getCurrentUsername();
        return progressReportRepository.findByUserName(currentUser);
    }
    public PerformanceReport getPerformanceReport(int previousNumberOfDays) {
        String  currentUser= UserCredentials.getCurrentUsername();
        return AnalyticsEngine.generatePerformanceReport(currentUser,previousNumberOfDays);
    }

    public List<PerformanceReport> getPreviousPerformanceReports() {
        String  currentUser= UserCredentials.getCurrentUsername();
        return performanceReportRepository.findByUserName(currentUser);
    }
}
