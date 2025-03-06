package com.TimeWise.repository;

import com.TimeWise.utils.UserVerificationMessage;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserVerificationMessageRepository extends MongoRepository<UserVerificationMessage, ObjectId> {
    UserVerificationMessage findByCodeAndUserNameAndUserEmail(String code, String userName, String email);


    void deleteByUserEmail(String userEmail);

    void deleteByUserNameOrUserEmail(String userName, String userEmail);

    UserVerificationMessage findByCodeAndUserEmail(String code, String userEmail);

    UserVerificationMessage findByUserEmail(String userEmail);
}
