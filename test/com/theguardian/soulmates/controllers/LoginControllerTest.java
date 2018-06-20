package com.theguardian.soulmates.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static play.mvc.Http.Status.FORBIDDEN;
import static play.mvc.Http.Status.OK;

import java.util.concurrent.CompletionStage;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.theguardian.soulmates.config.MongoDb;
import com.theguardian.soulmates.model.user.User;
import com.theguardian.soulmates.testconfig.WithMongoDB;

import play.libs.Json;
import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;

public class LoginControllerTest extends WithMongoDB {

	public final String username = "LoginControllerTestUser";
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
	public void testWithNotExistingCredentials() throws Exception {
		WSResponse r = callLoginAction("notExisting", "notExisting");
		assertEquals(FORBIDDEN, r.getStatus());
	}
	
	@Test
	public void testWithWrongPassword() throws Exception {
		WSResponse r = callLoginAction(username, "notExisting");
		assertEquals(FORBIDDEN, r.getStatus());
	}
	
	@Test
	public void testCorrectLogin() throws Exception {
		WSResponse r = callLoginAction(username, password);
		assertEquals(OK, r.getStatus());
	}
	
	
	private WSResponse callLoginAction(String username, String password) throws Exception {
		JsonNode json = Json.newObject().put("username", username).put("password", password);
		String url = "/api/login";
		try (WSClient ws = play.test.WSTestClient.newClient(play.api.test.Helpers.testServerPort())) {
			CompletionStage<WSResponse> stage = ws.url(url).addHeader("Content-Type", "application/json").post(json);
			return stage.toCompletableFuture().get();
		} catch (InterruptedException e) {
			fail("should not fail");
			throw e;
		}
	}
	
}