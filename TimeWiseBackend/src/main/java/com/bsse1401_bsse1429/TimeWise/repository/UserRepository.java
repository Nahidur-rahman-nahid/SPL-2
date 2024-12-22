package com.bsse1401_bsse1429.TimeWise.repository;

import com.bsse1401_bsse1429.TimeWise.model.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, Integer> {

    User findByUserName(String username);
    User findByEmail(String email);
    User findByUserNameAndPassword(String username, String password);
    User findByUserId(ObjectId userId);
}
