package cn.edu.nju.winews.dao.impl;

import cn.edu.nju.winews.dao.NewsDao;
import cn.edu.nju.winews.model.BriefNews;
import cn.edu.nju.winews.model.News;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

public class NewsDaoImpl implements NewsDao {
	 private MongoClient mongo;
	 private DB db;
	 
	@Override
	public boolean exists(String url) throws Exception {
//		DBCollection coll = db.getCollection(news.getSource());
		// BasicDBObject dbObj = new BasicDBObject("url", news.getUrl().toString());
		// return coll.findOne(dbObj) != null;
		return false;
	}

	@Override
	public String add(News news) throws Exception {
		System.out.println(news);
		return null;
	}

	@Override
	public News get(String id) throws Exception {
		return null;
	}

	@Override
	public BriefNews[] search(String[] keywords) throws Exception {
		return null;
	}

}

// class WiConvertor {
// public static WiNews DBObject2News(DBObject o)
// throws MalformedURLException, ParseException {
// WiNews news = new WiNews();
// news.setContent(o.get("content").toString());
// news.setDate(new WiDate(o.get("date").toString(), "yyyy-MM-dd"));
// news.setLayout(o.get("layout").toString());
// news.setSource(o.get("source").toString());
// news.setTitle(o.get("title").toString());
// news.setSubTitle(o.get("sub_title").toString());
// news.setUrl(new URL(o.get("url").toString()));
// for (Object listObj : ((BasicDBList) o.get("pictures"))) {
// DBObject dbListObj = (DBObject) listObj;
// WiNewsPicture pic = new WiNewsPicture();
// pic.setNewsUrl(news.getUrl());
// pic.setUrl(new URL(dbListObj.get("url").toString()));
// pic.setComment(dbListObj.get("comment").toString());
// news.addPicture(pic);
// }
// return news;
// }
//
// public static DBObject news2DBObject(WiNews news) {
// DBObject o = new BasicDBObject();
// o.put("url", news.getUrl().toString());
// o.put("date", news.getDate().getFormatDate("yyyy-MM-dd"));
// o.put("layout", news.getLayout());
// o.put("source", news.getSource());
// o.put("title", news.getTitle());
// o.put("sub_title", news.getSubTitle());
// o.put("content", news.getContent());
// BasicDBList pics = new BasicDBList();
// for (WiNewsPicture pic : news.getPictures()) {
// DBObject picObj = new BasicDBObject();
// picObj.put("url", pic.getUrl().toString());
// picObj.put("comment", pic.getComment());
// pics.add(picObj);
// }
// o.put("pictures", pics);
// return o;
// }
// }
//
// class MongoHelper {
// private MongoClient client;
// private DB db;
//
// public MongoHelper() throws Exception {
// try {
// // client = new MongoClient("121.40.127.177", 18017);
// client = new MongoClient("115.29.242.187", 27017);
// // client = new MongoClient("localhost", 27017);
// } catch (Exception e) {
// throw e;
// }
// db = client.getDB("Winews");
// }
//
// public boolean existsDate(String source, WiDate date) {
// DBCollection coll = db.getCollection("_date");
// BasicDBObject dbObj = new BasicDBObject("source", source).append(
// "date", date.toString());
// return coll.findOne(dbObj) != null;
// }
//
// public void addDate(String source, WiDate date) {
// DBCollection coll = db.getCollection("_date");
// BasicDBObject dbObj = new BasicDBObject("source", source).append(
// "date", date.toString());
// coll.save(dbObj);
// }
//
// public boolean existsUrl(String source, String url) {
// DBCollection coll = db.getCollection("_url");
// BasicDBObject dbObj = new BasicDBObject("source", source).append("url",
// url);
// return coll.findOne(dbObj) != null;
// }
//
// public void addUrl(String source, String url) {
// DBCollection coll = db.getCollection("_url");
// BasicDBObject dbObj = new BasicDBObject("source", source).append("url",
// url);
// coll.save(dbObj);
// }
//
// public void clearUrl(String source) {
// DBCollection coll = db.getCollection("_url");
// coll.remove(new BasicDBObject("source", source));
// }
//
// public boolean existsNews(WiNews news) {
// DBCollection coll = db.getCollection(news.getSource());
// BasicDBObject dbObj = new BasicDBObject("url", news.getUrl().toString());
// return coll.findOne(dbObj) != null;
// }
//
// public synchronized void addNews(WiNews news) throws MongoIOException {
// DBCollection coll = db.getCollection(news.getSource());
// // if there is no reference Collection
// if (!db.getCollectionNames().contains(news.getSource())) {
// db.createCollection(news.getSource(), new BasicDBObject());
// coll = db.getCollection(news.getSource());
// coll.createIndex(new BasicDBObject("url", 1), new BasicDBObject(
// "unique", true).append("name", "url"));
// }
// if (coll.findOne(new BasicDBObject("url", news.getUrl().toString())) != null)
// {
// System.out.println("News already in the database.");
// return;
// }
// DBObject obj = WiConvertor.news2DBObject(news);
// WriteResult result = coll.save(obj);
// if (result.getN() != 0) {
// throw new MongoIOException("Can't save Mongo Object!");
// }
// }
//
// public synchronized DBCursor getNews(String source) throws MongoIOException {
// DBCollection coll = db.getCollection(source);
// if (coll == null) {
// throw new MongoIOException("Can't find this collection: " + source);
// }
// DBCursor cur = coll.find(new BasicDBObject());
// return cur;
// }
//
// public synchronized DBCursor getNews(String source, WiDate date)
// throws MongoIOException {
// DBCollection coll = db.getCollection(source);
// if (coll == null) {
// throw new MongoIOException("Can't find this collection: " + source);
// }
// DBCursor cur = coll.find(new BasicDBObject("date", date
// .getFormatDate("yyyy-MM-dd")));
// return cur;
// }
//
// public static void main(String[] args) throws MongoIOException, Exception {
// DBCursor cur = new MongoHelper()
// .getNews("北京日报", new WiDate(2015, 5, 5));
// if (cur.hasNext()) {
// System.out.println(cur.next());
// }
// }
// }
