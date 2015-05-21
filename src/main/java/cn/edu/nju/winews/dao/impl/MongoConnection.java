package cn.edu.nju.winews.dao.impl;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.DB;
import com.mongodb.MongoClient;

public class MongoConnection {
	private static final Logger logger = LoggerFactory.getLogger(MongoConnection.class);
	private static final String CONFIG_FILENAME = "mongo.properties";

	private String host, username, password;
	private int port;

	private MongoClient mongo;
	private DB db;

	public MongoConnection() throws IOException {
		initConfig();
		initConnection();
	}

	private void initConfig() throws IOException {
		Properties prop = new Properties();
		prop.load(this.getClass().getResourceAsStream(CONFIG_FILENAME));
		host = prop.getProperty("host", "");
		port = Integer.parseInt(prop.getProperty("port", "-1"));
		username = prop.getProperty("username", "");
		password = prop.getProperty("password", "");
		logger.info("初始化Mongodb连接，url={}:{}，username={}，password={}", host, port, username, password);
		if (host.equals("") || port == -1 || username.equals("") || password.equals("")) {
			throw new IOException("Can't find config file");
		}
	}

	private void initConnection() throws UnknownHostException {
		mongo = new MongoClient(host, port);
		db = mongo.getDB("winews");
	}

	public MongoClient getMongo() {
		return mongo;
	}

	public void setMongo(MongoClient mongo) {
		this.mongo = mongo;
	}

	public DB getDb() {
		return db;
	}

	public void setDb(DB db) {
		this.db = db;
	}

	public void close() {
		mongo.close();
	}
}