package cn.edu.nju.winews.dao.impl;

import java.util.logging.Logger;

import cn.edu.nju.winews.dao.KeywordDao;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class KeywordDaoImpl implements KeywordDao {
	private static final Logger logger = Logger.getLogger(NewsDaoImpl.class.getName());

	private DB db;
	private static final String tableName = "keyword";

	public KeywordDaoImpl() throws Exception {
		db = MongoHelper.getInstance().getDb();
	}

	@Override
	public String[] search(String[] keywords) throws Exception {
		DBCollection coll = db.getCollection(tableName);
		BasicDBList resultList = new BasicDBList();
		for (int i = 0; i < keywords.length; i++) {
			BasicDBObject queryObj = new BasicDBObject("key", keywords[i]);
			DBObject resultObj = coll.findOne(queryObj);
			if (resultObj == null) {
				continue;
			}
			BasicDBList urlList = (BasicDBList) resultObj.get("urls");
			if (resultList.isEmpty()) {
				resultList = urlList;
			} else {
				resultList.retainAll(urlList);
			}
		}
		String[] resultArr = resultList.toArray(new String[resultList.size()]);
		return resultArr;
	}

	@Override
	public void addSearchIndex(String kw, String url) throws Exception {
		DBCollection coll = db.getCollection(tableName);
		BasicDBObject queryObj = new BasicDBObject("key", kw);
		DBObject resultObj = coll.findOne(queryObj);
		if (resultObj == null) {
			resultObj = new BasicDBObject();
			resultObj.put("key", kw);
			BasicDBList urlList = new BasicDBList();
			urlList.add(url);
			resultObj.put("urls", urlList);
			coll.insert(resultObj);
		} else {
			BasicDBList urlList = (BasicDBList) resultObj.get("urls");
			if (!urlList.contains(url)) {
				urlList.add(url);
			}
			resultObj.put("urls", urlList);
			coll.update(queryObj, resultObj);
		}
	}

}
