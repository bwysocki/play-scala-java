package com.theguardian.soulmates.testconfig;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

import org.junit.After;
import org.junit.Before;

import com.fasterxml.jackson.databind.JsonNode;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;
import play.test.WithServer;

public class WithMongoDB extends WithServer {

	private static final MongodStarter starter = MongodStarter.getDefaultInstance();
	private MongodExecutable _mongodExe;
	private MongodProcess _mongod;
	private MongoClient _mongo;

	@Before
	@Override
	public void startServer() {
		try {
			_mongodExe = starter.prepare(new MongodConfigBuilder().version(Version.Main.PRODUCTION)
					.net(new Net("localhost", 12345, false)).build());
			_mongod = _mongodExe.start();
			_mongo = new MongoClient("localhost", 12345);
			super.startServer();
		} catch (Exception e) {
			throw new RuntimeException("Can not create in memory mongoDB");
		}
	}

	@After
	@Override
	public void stopServer() {
		_mongod.stop();
		_mongodExe.stop();
		super.stopServer();
	}

	protected Mongo getMongo() {
		return _mongo;
	}
	
	protected String callForToken(JsonNode json) throws Exception {
		String url = "/api/login";
		try (WSClient ws = play.test.WSTestClient.newClient(play.api.test.Helpers.testServerPort())) {
			CompletionStage<WSResponse> stage = ws.url(url).addHeader("Content-Type", "application/json").post(json);
			return stage.toCompletableFuture().get().asJson().get("access_token").asText();
		} catch (InterruptedException e) {
			fail("should not fail");
			throw e;
		}
	}
	
	protected WSResponse fetch(String url) throws IOException, ExecutionException {
		try (WSClient ws = play.test.WSTestClient.newClient(play.api.test.Helpers.testServerPort())) {
			CompletionStage<WSResponse> stage = ws.url(url).get();
			return stage.toCompletableFuture().get();
		} catch (InterruptedException e) {
			fail("should not fail");
			return null;
		}
	}

	protected WSResponse fetchWithToken(String url, String token) throws IOException, ExecutionException {
		try (WSClient ws = play.test.WSTestClient.newClient(play.api.test.Helpers.testServerPort())) {
			CompletionStage<WSResponse> stage = ws.url(url).addHeader("Authorization", "Bearer " + token).get();
			return stage.toCompletableFuture().get();
		} catch (InterruptedException e) {
			fail("should not fail");
			return null;
		}
	}

}
