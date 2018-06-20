package com.theguardian.soulmates.config;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.lang3.StringUtils;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.theguardian.soulmates.model.user.User;
import com.typesafe.config.Config;

@Singleton
public class MongoDb {
	
	private final Morphia morphia = new Morphia();
	
	private final Datastore datastore;
	
	@Inject
	public MongoDb(Config config) {

		// tell Morphia where to find your classes
		// can be called multiple times with different packages or classes
		morphia.mapPackage("com.theguardian.soulmates.model.user");

		
		String host = config.getString("mongo.host");
		Integer port = Integer.valueOf(config.getString("mongo.port"));
		String dbName = config.getString("mongo.database");
		String password = config.getString("mongo.password");
		String username = config.getString("mongo.username");
		
		final List<MongoCredential> credentialsList = new ArrayList<>();
		if (StringUtils.isNotBlank(password) && StringUtils.isNotBlank(username)) {
			credentialsList.add(MongoCredential.createCredential(username, dbName, password.toCharArray()));
		}
		
		ServerAddress serverAddress = new ServerAddress(host, port);
		MongoClient mongoClient = new MongoClient(serverAddress, credentialsList);
		
		datastore = morphia.createDatastore(mongoClient, dbName);
		datastore.ensureIndexes();
		
	}

	public Datastore getDatastore() {
		return datastore;
	}
	
}
