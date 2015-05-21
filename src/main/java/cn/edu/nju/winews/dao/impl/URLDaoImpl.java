package cn.edu.nju.winews.dao.impl;

import java.io.IOException;
import java.util.ArrayList;

import cn.edu.nju.winews.dao.URLDao;
import cn.edu.nju.winews.model.UrlRecord;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

public class URLDaoImpl implements URLDao {
	private static URLDaoImpl instance;
	private DB db;

	private URLDaoImpl() throws IOException {
		db = new MongoConnection().getDb();
	}

	public static URLDaoImpl getInstance() throws IOException {
		if (instance == null) {
			instance = new URLDaoImpl();
		}
		return instance;
	}

	@Override
	public synchronized URLDao visit(UrlRecord record) {
		DBCollection visited = db.getCollection("visitedUrl");
		DBCollection suspendNode = db.getCollection("suspendNodeUrl");
		BasicDBObject query = new BasicDBObject().append("url", record.getUrl());
		BasicDBObject insert = new BasicDBObject(query).append("sourceName", record.getSourceName()).append("timestamp", record.getTimestamp());
		visited.insert(insert);
		suspendNode.remove(query);
		return this;
	}

	private void errorUrl(UrlRecord record) {
		DBCollection error = db.getCollection("errorUrl");
		BasicDBObject query = new BasicDBObject().append("url", record.getUrl());
		BasicDBObject insert = new BasicDBObject(query).append("sourceName", record.getSourceName()).append("timestamp", record.getTimestamp());
		error.remove(query);
		error.insert(insert);
	}

	@Override
	public synchronized URLDao suspend(UrlRecord record) {
		DBCollection suspendNode = db.getCollection("suspendNodeUrl");
		BasicDBObject query = new BasicDBObject().append("url", record.getUrl());
		BasicDBObject insert;
		try {
			if ((insert = (BasicDBObject) suspendNode.find(query).next()) != null) {
				int visitCnt = insert.getInt("visitCnt");
				if (visitCnt == 2) {
					errorUrl(record);
					suspendNode.remove(query);
				} else {
					insert.put("visitCnt", insert.getInt("visitCnt") + 1);
					suspendNode.update(query, insert);
				}
			} else {
				insert = new BasicDBObject(query).append("sourceName", record.getSourceName()).append("timestamp", record.getTimestamp())
						.append("visitCnt", 1);
				suspendNode.insert(insert);
			}
		} catch (Exception e) {
			insert = new BasicDBObject(query).append("sourceName", record.getSourceName()).append("timestamp", record.getTimestamp())
					.append("visitCnt", 1);
			suspendNode.insert(insert);
		}
		return this;
	}

	@Override
	public boolean isVisited(String url) {
		DBCollection visited = db.getCollection("visitedUrl");
		BasicDBObject query = new BasicDBObject().append("url", url);
		return visited.find(query).hasNext();
	}

	@Override
	public boolean isSuspend(String url) {
		DBCollection suspendNode = db.getCollection("suspendNodeUrl");
		BasicDBObject query = new BasicDBObject().append("url", url);
		return suspendNode.find(query).hasNext();
	}

	@Override
	public UrlRecord[] getSuspend(String sourceName, int vol) {
		int maxSize = vol;
		if (maxSize < 0) {
			maxSize = 128;
		}
		ArrayList<UrlRecord> l = new ArrayList<UrlRecord>(maxSize);
		DBCollection suspendNode = db.getCollection("suspendNodeUrl");
		BasicDBObject query = new BasicDBObject();
		if (sourceName != null) {
			query.append("sourceName", sourceName);
		}
		for (int i = 1; i < 3; i++) {
			query.append("visitCnt", i);
			DBCursor cur = suspendNode.find(query);
			while (cur.hasNext()) {
				if (l.size() < maxSize) {
					BasicDBObject obj = (BasicDBObject) cur.next();
					l.add(new UrlRecord(obj.getString("url"), obj.getString("sourceName"), obj.getLong("timestamp")));
				} else {
					break;
				}
			}
			if (!(l.size() < maxSize)) {
				break;
			}
		}
		return l.toArray(new UrlRecord[l.size()]);
	}
}
