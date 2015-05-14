package cn.edu.nju.winews.dao.impl;

import java.net.UnknownHostException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import cn.edu.nju.winews.dao.impl.exception.ConfigException;

import com.mongodb.DB;
import com.mongodb.MongoClient;

public class MongoHelper {
	private static final Logger logger = Logger.getLogger(VisitedDaoImpl.class.getName());
	private static final String CONFIG_FILENAME = "mongo.properties";
	private static MongoHelper instance;
	
	private String host, username, password;
	private int port;

	private static MongoClient mongo;
	private static DB db;

	private MongoHelper() throws Exception {
		initConfig();
		initConnection();
	}

	public static MongoHelper getInstance() throws Exception {
		if (instance == null) {
			instance = new MongoHelper();
		}
		return instance;
	}

	private void initConfig() throws Exception {
		Properties prop = new Properties();
		prop.load(this.getClass().getResourceAsStream(CONFIG_FILENAME));
		host = prop.getProperty("host", "");
		port = Integer.parseInt(prop.getProperty("port", "-1"));
		username = prop.getProperty("username", "");
		password = prop.getProperty("password", "");
		logger.log(Level.INFO, "初始化Mongodb连接，url={0}:{1}，username={2}，password={3}", new String[] { host, port + "", username, password });
		if (host.equals("") || port == -1 || username.equals("") || password.equals("")) {
			throw new ConfigException("配置文件不完整！");
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
		MongoHelper.mongo = mongo;
	}

	public DB getDb() {
		return db;
	}

	public void setDb(DB db) {
		MongoHelper.db = db;
	}
	
	
}
