package com.TimeWise.repository;


import com.TimeWise.model.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface UserRepository extends MongoRepository<User, ObjectId> {

    User findByUserName(String username);
    User findByUserNameAndPassword(String username, String password);
    User findByUserId(ObjectId userId);
    List<User> findByUserNameIn(Set<String> userNames);

    List<User> findByUserEmail(String userEmail);

    @Query(value = "{}", fields = "{'userName': 1, '_id': 0}")
    Set<String> findAllUserNames();
}
