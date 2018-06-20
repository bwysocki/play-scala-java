package com.theguardian.soulmates.controllers;

import java.io.UnsupportedEncodingException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

import javax.inject.Inject;

import org.bson.types.ObjectId;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.theguardian.soulmates.config.MongoDb;
import com.theguardian.soulmates.model.user.User;
import com.typesafe.config.Config;

import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

public class LoginController extends Controller {
	
	@Inject
    private Config config;
	
	@Inject
	private MongoDb mongoDb;
	
	public Result login() throws UnsupportedEncodingException {
        JsonNode body = request().body().asJson();
        if (body == null) {
            Logger.error("json body is null");
            return forbidden();
        }

        if (body.hasNonNull("username") && body.hasNonNull("password")) {
            
        	String username = body.get("username").asText();
        	String password = body.get("password").asText();
        	
        	//TODO - add hashing 
        	
        	User user = mongoDb.getDatastore().createQuery(User.class)
            	.filter("username =", username)
            	.filter("password =", password)
            	.get();
        	
        	if (user == null) {
        		return forbidden();
        	}
        	
        	ObjectNode result = Json.newObject();
            result.put("access_token", getSignedToken(user.getId().toString()));
            return ok(result);
        } else {
            Logger.error("json body not as expected: {}", body.toString());
        }

        return forbidden();
    }
	
	private String getSignedToken(String userId) throws UnsupportedEncodingException {
        String secret = config.getString("jwt.token.secret");
        Integer tokenTimeInMinutes = config.getInt("jwt.token.timeInMinutes");
        
        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.create()
                .withIssuer("soulmates-frontend")
                .withClaim("user_id", userId)
                .withExpiresAt(Date.from(ZonedDateTime.now(ZoneId.systemDefault()).plusMinutes(tokenTimeInMinutes).toInstant()))
                .sign(algorithm);
    }
	
}
