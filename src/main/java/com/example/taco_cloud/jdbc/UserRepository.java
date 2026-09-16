package com.example.taco_cloud.jdbc;

import com.example.taco_cloud.data.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User,Long> {
    User findByUsername(String username);
}
