package com.TimeWise.service;

import com.TimeWise.engine.AnalyticsEngine;
import com.TimeWise.model.ProgressReport;
import com.TimeWise.repository.ProgressReportRepository;
import com.TimeWise.utils.UserCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportService {

    @Autowired
    private ProgressReportRepository progressReportRepository;


   public ProgressReport getCurrentProgressReport() {
       String  currentUser= UserCredentials.getCurrentUsername();
       return AnalyticsEngine.generateProgressReport(currentUser);
   }

    public List<ProgressReport> getPreviousProgressReports() {
        String  currentUser= UserCredentials.getCurrentUsername();
        return progressReportRepository.findByUserName(currentUser);
    }
}
