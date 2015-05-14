package cn.edu.nju.winews.dao.impl;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import cn.edu.nju.winews.dao.VisitedDao;
import cn.edu.nju.winews.model.VisitedRecord;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;

public class VisitedDaoImpl implements VisitedDao {
	private static final Logger logger = Logger.getLogger(VisitedDaoImpl.class.getName());

	private DB db;

	public VisitedDaoImpl() throws Exception {
		db = MongoHelper.getInstance().getDb();
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
