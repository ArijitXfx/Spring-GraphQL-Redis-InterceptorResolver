package com.tricon.redisinterceptor.model;

import java.io.Serializable;
import java.util.Set;

import org.springframework.data.annotation.Id;

import io.leangen.graphql.annotations.GraphQLQuery;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@SuppressWarnings("serial")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User implements Serializable{
	@Id
	@GraphQLQuery(name = "id")
	private String id;
	
	@GraphQLQuery(name = "name")
	private String name;
	
	@GraphQLQuery(name = "roles")
	private Set<String> roles;
}
