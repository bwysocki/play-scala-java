package com.theguardian.soulmates.controllers;

import static org.junit.Assert.assertEquals;
import static play.mvc.Http.Status.FORBIDDEN;
import static play.mvc.Http.Status.OK;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.theguardian.soulmates.config.MongoDb;
import com.theguardian.soulmates.model.user.User;
import com.theguardian.soulmates.testconfig.WithMongoDB;

import play.libs.Json;

public class JwtSampleControllerTest extends WithMongoDB {

	public final String username = "IntTests";
	public final String password = "myPass";

	private String token = null;

	@Before
	public void beforeEach() throws Exception {
		MongoDb mongodb = this.app.injector().instanceOf(MongoDb.class);
		User sampleUser = new User(username, password);
		mongodb.getDatastore().save(sampleUser);

		JsonNode json = Json.newObject().put("username", username).put("password", password);
		this.token = callForToken(json);
	}

	@Test
	public void testNoJwt() throws Exception {
		String url = "/api/no-jwt";
		assertEquals(OK, fetch(url).getStatus());
	}

	@Test
	public void testRequiresJwt() throws Exception {
		String url = "/api/requires-jwt";
		assertEquals(FORBIDDEN, fetch(url).getStatus());
		assertEquals(OK, fetchWithToken(url, token).getStatus());
	}

	@Test
	public void testRequiresJwtViaFilter() throws Exception {
		String url = "/api/requires-jwt-via-filter";
		assertEquals(FORBIDDEN, fetch(url).getStatus());
		assertEquals(OK, fetchWithToken(url, token).getStatus());
	}
	
	@Test
	public void testRequiresJwtViaAnnotation() throws Exception {
		String url = "/api/jwt-annotation";
		assertEquals(FORBIDDEN, fetch(url).getStatus());
		assertEquals(OK, fetchWithToken(url, token).getStatus());
	}
}