package com.tricon.redisinterceptor.calls;

import java.util.List;

import org.springframework.stereotype.Component;

import com.tricon.redisinterceptor.model.Auth;
import com.tricon.redisinterceptor.model.User;
import com.tricon.redisinterceptor.service.UserService;

import io.leangen.graphql.annotations.GraphQLQuery;

@Component
public class Query{

	private UserService userService;
	
	public Query(UserService userService) {
		this.userService = userService;
	}
	
	@GraphQLQuery(name = "findAll")
	@Auth(rolesRequired = {"RegularUser", "Admin"})
	public List<User> findAll(){
		return userService.findAll();
	}
	
	@GraphQLQuery(name="findById")
	@Auth(rolesRequired = {"RegularUser", "Admin"})
	public User findById(String id) {
		return userService.findById(id);
	}
}
