package com.bsse1401_bsse1429.TimeWise.repository;

import com.bsse1401_bsse1429.TimeWise.model.Task;
import com.bsse1401_bsse1429.TimeWise.model.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface UserRepository extends MongoRepository<User, Integer> {

    User findByUserName(String username);
    User findByUserNameAndPassword(String username, String password);
    User findByUserId(ObjectId userId);
    List<User> findByUserNameIn(Set<String> userNames);

    List<User> findByUserEmail(String userEmail);
}
