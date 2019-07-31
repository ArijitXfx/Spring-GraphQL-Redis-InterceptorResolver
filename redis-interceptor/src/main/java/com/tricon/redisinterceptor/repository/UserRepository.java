package com.tricon.redisinterceptor.repository;

import java.util.Map;

import com.tricon.redisinterceptor.model.User;

public interface UserRepository {
	
	void save(User user);
	Map<String,User> findAll();
	void update(User user);
	void delete(String id);
	User findById(String id);
}
