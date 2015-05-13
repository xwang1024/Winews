package cn.edu.nju.winews.dao.impl;

import java.net.UnknownHostException;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import cn.edu.nju.winews.dao.VisitedDao;
import cn.edu.nju.winews.dao.impl.exception.ConfigException;
import cn.edu.nju.winews.model.VisitedRecord;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

public class VisitedDaoImpl implements VisitedDao {
	private static final Logger logger = Logger.getLogger(VisitedDaoImpl.class.getName());

	private static final String CONFIG_FILENAME = "mongo.properties";
	private static MongoClient mongo;
	private static DB db;

	private String host, username, password;
	private int port;

	public VisitedDaoImpl() throws Exception {
		initConfig();
		initConnection();
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

	@Override
	public boolean isVisited(String url) throws Exception {
		logger.log(Level.FINE, "Search visited url");
		DBCollection coll = db.getCollection("visited_url");
		BasicDBObject dbObj = new BasicDBObject("url", url);
		return coll.findOne(dbObj) != null;
	}

	@Override
	public void add(VisitedRecord record) throws Exception {
		String url = record.getUrl();
		String newspaper = record.getNewspaper();
		Date timestamp = record.getTimestamp();
		logger.log(Level.FINE, "Add visited url");
		DBCollection coll = db.getCollection("visited_url");
		BasicDBObject dbObj = new BasicDBObject("url", url).append("newspaper", newspaper).append("timestamp", timestamp);
		try {
			coll.save(dbObj);
		} catch (Exception e) {
			if (e.getMessage().contains("duplicate key")) {
				logger.log(Level.WARNING, "Duplicate key, url={0}", url);
			}
		}
	}

	@Override
	public void clear() throws Exception {
		logger.log(Level.FINE, "Clear visited url");
		DBCollection coll = db.getCollection("visited_url");
		coll.remove(new BasicDBObject());
	}

	public static void main(String[] args) throws Exception {
		VisitedRecord re = new VisitedRecord();
		re.setNewspaper("123");
		re.setUrl("333");
		re.setTimestamp(new Date());
		new VisitedDaoImpl().add(re);
	}
}
