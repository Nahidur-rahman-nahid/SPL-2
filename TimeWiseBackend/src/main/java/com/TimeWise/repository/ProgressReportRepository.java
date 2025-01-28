package com.TimeWise.repository;

import com.TimeWise.model.ProgressReport;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ProgressReportRepository extends MongoRepository<ProgressReport, ObjectId> {
    List<ProgressReport> findByUserName(String userName);
    void deleteByUserNameAndTimeStamp(String userName, Date timeStamp);
}
