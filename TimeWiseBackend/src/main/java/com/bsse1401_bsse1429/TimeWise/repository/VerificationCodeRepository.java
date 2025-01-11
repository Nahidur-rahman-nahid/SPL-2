package com.bsse1401_bsse1429.TimeWise.repository;

import com.bsse1401_bsse1429.TimeWise.utils.VerificationCode;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationCodeRepository extends MongoRepository<VerificationCode, ObjectId> {
    VerificationCode findByCodeAndUserNameAndUserEmail(String code,String userName, String email);


    void deleteByUserEmail(String userEmail);

    void deleteByUserNameOrUserEmail(String userName, String userEmail);

    VerificationCode findByCodeAndUserEmail(String code, String userEmail);

    VerificationCode findByUserEmail(String userEmail);
}
