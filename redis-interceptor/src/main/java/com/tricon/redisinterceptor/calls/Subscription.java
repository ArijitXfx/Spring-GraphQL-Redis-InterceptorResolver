package com.tricon.redisinterceptor.calls;

import org.reactivestreams.Publisher;
import org.springframework.stereotype.Component;

import com.tricon.redisinterceptor.service.UserService;
import io.leangen.graphql.annotations.GraphQLSubscription;

@Component
public class Subscription {

	private UserService userService;
	
	public Subscription(UserService userService) {
		this.userService = userService;
	}
	
	@GraphQLSubscription
	public Publisher<String> taskStatusChanged(String id){
		return userService.taskStatusChanged(id);
	}
}
