package com.tricon.redisinterceptor.calls;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.tricon.redisinterceptor.model.Auth;
import com.tricon.redisinterceptor.service.UserService;

import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;

@Component
public class Mutation {

	private UserService userService;
	
	public Mutation(UserService userService) {
		this.userService = userService;
	}
	
	@GraphQLMutation(name = "add") 
	public String add(@GraphQLArgument(name = "id") String id, @GraphQLArgument(name = "name") String name, @GraphQLArgument(name = "roles") Set<String> roles) {
		return userService.add(id, name, roles);
	}
	
	@GraphQLMutation(name="delete")
	@Auth(rolesRequired = {"Admin"})
	public String delete(@GraphQLArgument(name = "id") String id) {
		System.out.println("my id: "+id);
		return userService.delete(id);
	}
}
