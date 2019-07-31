package com.tricon.redisinterceptor.controller;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.servlet.AbstractGraphQLHttpServlet;
import graphql.servlet.GraphQLErrorHandler;
import graphql.servlet.GraphQLInvocationInputFactory;
import graphql.servlet.GraphQLObjectMapper;
import graphql.servlet.GraphQLQueryInvoker;
import graphql.servlet.GraphQLSingleInvocationInput;
import graphql.servlet.internal.GraphQLRequest;
import graphql.servlet.DefaultGraphQLErrorHandler;

@SuppressWarnings("serial")
@WebServlet(value = "/graphql")
public class GraphQLEndpoint extends AbstractGraphQLHttpServlet {

	private GraphQLSchema schema;
	
	private GraphQLErrorHandler errorHandler = new DefaultGraphQLErrorHandler();
	
	public GraphQLEndpoint(GraphQLSchema schema) {
		this.schema = schema;
		GraphQL.newGraphQL(schema).build();
	}
	
	
	@Override
    protected GraphQLQueryInvoker getQueryInvoker() {
        return GraphQLQueryInvoker.newBuilder().build();
    }

    @Override
    protected GraphQLInvocationInputFactory getInvocationInputFactory() {
        return GraphQLInvocationInputFactory.newBuilder(schema).build();
    }

    @Override
    protected GraphQLObjectMapper getGraphQLObjectMapper() {
    	return GraphQLObjectMapper.newBuilder().withGraphQLErrorHandler(this::getErrorHandler).build();
    }
    
    public GraphQLErrorHandler getErrorHandler() {
		return errorHandler;
	}

	public void setErrorHandler(GraphQLErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
	}
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	GraphQLInvocationInputFactory invocationInputFactory = getInvocationInputFactory();
		GraphQLObjectMapper graphQLObjectMapper = getGraphQLObjectMapper();
		GraphQLQueryInvoker queryInvoker = getQueryInvoker();
		try {
			InputStream inputStream = req.getInputStream();
			if (!inputStream.markSupported()) {
				inputStream = new BufferedInputStream(inputStream);
			}

			GraphQLRequest gReq = graphQLObjectMapper.readGraphQLRequest(inputStream);

			GraphQLSingleInvocationInput invocInput = invocationInputFactory.create(gReq, req);
			query(queryInvoker, graphQLObjectMapper, invocInput, resp);
			
		} catch (Exception e) {
			log.info("Bad POST request: parsing failed", e);
			resp.setStatus(STATUS_BAD_REQUEST);
		}
    }
    
    private void query(GraphQLQueryInvoker queryInvoker, GraphQLObjectMapper graphQLObjectMapper,
            GraphQLSingleInvocationInput invocationInput, HttpServletResponse resp) throws IOException {
        ExecutionResult result = queryInvoker.query(invocationInput);
        resp.setContentType(APPLICATION_JSON_UTF8);
        resp.setStatus(STATUS_OK);
        resp.setHeader("Access-Control-Allow-Origin","*");
        resp.setHeader("Access-Control-Allow-Headers","Content-Type, Accept, X-Requested-With, remember-me");
        resp.getWriter().write(graphQLObjectMapper.serializeResultAsJson(result));
    }
	
}
