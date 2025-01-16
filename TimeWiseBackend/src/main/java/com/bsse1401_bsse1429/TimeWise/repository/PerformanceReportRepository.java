package com.bsse1401_bsse1429.TimeWise.repository;

import com.bsse1401_bsse1429.TimeWise.model.PerformanceReport;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PerformanceReportRepository extends MongoRepository<PerformanceReport, ObjectId> {
    List<PerformanceReport> findByUserName(String userName);
}
