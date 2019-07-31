package com.tricon.redisinterceptor.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;

import com.tricon.redisinterceptor.model.User;
import com.tricon.redisinterceptor.repository.UserRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

@Service
public class UserService {

	private UserRepository userRepository;
	
	private final ConcurrentHashMap<String, FluxSink<String>> subscribers = new ConcurrentHashMap<>();
	
	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	public String add(String id, String name, Set<String> roles) {
		userRepository.save(new User(id, name, roles));
		return "Added Successfully";
	}
	
	public List<User> findAll(){
		List<User> userList = new LinkedList<User>();
		userRepository.findAll().forEach((k,v)->{
			userList.add(v);
		});
		return userList;
	}
	
	public String delete(String id) {
		userRepository.delete(id);
		return "Deleted!";
	}
	
	public User findById(String id) {
		return userRepository.findById(id);
	}
	
	public Publisher<String> taskStatusChanged(String id){
		return Flux.create(subscriber->subscribers.put(id, subscriber.onDispose(()->{
			subscribers.remove(id, subscriber);
		})), FluxSink.OverflowStrategy.LATEST);
	}
}
