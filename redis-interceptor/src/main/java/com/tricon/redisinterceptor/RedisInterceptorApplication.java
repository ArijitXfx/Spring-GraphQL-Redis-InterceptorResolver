package com.tricon.redisinterceptor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import com.tricon.redisinterceptor.calls.Mutation;
import com.tricon.redisinterceptor.calls.Query;
import com.tricon.redisinterceptor.calls.Subscription;
import com.tricon.redisinterceptor.model.Auth;
import com.tricon.redisinterceptor.model.User;
import com.tricon.redisinterceptor.repository.UserRepository;
import com.tricon.redisinterceptor.repository.UserRepositoryImpl;
import com.tricon.redisinterceptor.service.UserService;

import graphql.schema.GraphQLSchema;
import io.leangen.graphql.GraphQLSchemaGenerator;
import io.leangen.graphql.execution.InvocationContext;
import io.leangen.graphql.execution.ResolverInterceptor;

@ServletComponentScan
@SpringBootApplication
public class RedisInterceptorApplication {
	
	@Bean
	LettuceConnectionFactory lettuceConnectionFactory() {
		return new LettuceConnectionFactory();
	}
	
	@Bean
	RedisTemplate<String, User> redisTemplate(){
		RedisTemplate<String, User> redisTemplate = new RedisTemplate<String, User>();
		redisTemplate.setConnectionFactory(lettuceConnectionFactory());
		return redisTemplate;
	}
	
	@Bean
	public GraphQLSchema schema(Query query, Mutation mutation, Subscription subscription) {

		ResolverInterceptor intercp = new ResolverInterceptor() {

			@Override
			public Object aroundInvoke(InvocationContext context, Continuation continuation) throws Exception {
				if(!context.getOperation().getName().equals("add")) {
					Auth auth = context.getResolver().getExecutable().getDelegate().getAnnotation(Auth.class);
		            if (auth != null && !query.findById("3").getRoles().containsAll(Arrays.asList(auth.rolesRequired()))) {
		                throw new IllegalAccessException("Access denied"); // or return null
		            }
				}
	            return continuation.proceed(context);
			}

		};

		return new GraphQLSchemaGenerator()
				.withBasePackages("com.tricon.redisinterceptor")
				.withOperationsFromSingletons(query, mutation, subscription)
				.withResolverInterceptors(intercp)
				.generate();
	}
	
	public static void main(String[] args) {
		SpringApplication.run(RedisInterceptorApplication.class, args);
	}

}
