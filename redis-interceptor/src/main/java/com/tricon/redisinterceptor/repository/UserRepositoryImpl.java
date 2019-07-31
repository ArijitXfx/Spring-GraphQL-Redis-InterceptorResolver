package com.tricon.redisinterceptor.repository;

import java.util.Map;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.tricon.redisinterceptor.model.User;

@Repository
public class UserRepositoryImpl implements UserRepository{

	private RedisTemplate<String, User> redisTemplate;
	private HashOperations<String, String , User> hashOperation;
	
	private final String REDIS_KEY = "TABLE_USER";
	private final String KEY = "USERID_";
	
	public UserRepositoryImpl(RedisTemplate<String, User> redisTemplate) {
		this.redisTemplate = redisTemplate;
		hashOperation = this.redisTemplate.opsForHash();
	}
	
	@Override
	public void save(User user) {
		hashOperation.put(REDIS_KEY, KEY+user.getId(), user);
	}

	@Override
	public Map<String, User> findAll() {
		return hashOperation.entries(REDIS_KEY);
	}

	@Override
	public void update(User user) {
		save(user);
	}

	@Override
	public void delete(String id) {
		hashOperation.delete(REDIS_KEY, KEY+id);
	}

	@Override
	public User findById(String id) {
		return hashOperation.get(REDIS_KEY, KEY+id);
	}

}
