package cn.edu.nju.winews.dao.impl;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import cn.edu.nju.winews.dao.NewsDao;
import cn.edu.nju.winews.model.BriefNews;
import cn.edu.nju.winews.model.News;
import cn.edu.nju.winews.model.NewsPicture;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class NewsDaoImpl implements NewsDao {
	private static final Logger logger = Logger.getLogger(NewsDaoImpl.class.getName());

	private DB db;
	private static final String tableName = "news";

	public NewsDaoImpl() throws Exception {
		db = MongoHelper.getInstance().getDb();
	}

	@Override
	public boolean exists(String url) throws Exception {
		DBCollection coll = db.getCollection(tableName);
		BasicDBObject dbObj = new BasicDBObject("url", url);
		return coll.findOne(dbObj) != null;
	}

	@Override
	public String add(News news) throws Exception {
		DBCollection coll = db.getCollection(tableName);
		DBObject dbObj = WiConvertor.news2DBObject(news);
		coll.insert(dbObj);
		return news.getUrl();
	}

	@Override
	public News get(String url) throws Exception {
		DBCollection coll = db.getCollection(tableName);
		BasicDBObject dbObj = new BasicDBObject("url", url);
		DBObject result = coll.findOne(dbObj);
		return WiConvertor.DBObject2News(result);
	}

	@Override
	public BriefNews[] search(String[] keywords) throws Exception {
		return null;
	}

}

class WiConvertor {
	public static News DBObject2News(DBObject o) {
		News news = new News();
		news.setUrl(o.get("url").toString());
		news.setDomain(o.get("domail").toString());
		news.setNewspaper(o.get("newspaper").toString());
		news.setPreTitle(o.get("preTitle").toString());
		news.setTitle(o.get("title").toString());
		news.setSubTitle(o.get("subTitle").toString());
		news.setAuthor(o.get("author").toString());
		news.setDate((Date) o.get("date"));
		news.setLayout(o.get("layout").toString());
		news.setContent(o.get("content").toString());
		Set<NewsPicture> set = new HashSet<>();
		for (Object listObj : ((BasicDBList) o.get("pictures"))) {
			DBObject dbListObj = (DBObject) listObj;
			NewsPicture pic = new NewsPicture();
			pic.setUrl(dbListObj.get("url").toString());
			pic.setComment(dbListObj.get("comment").toString());
			set.add(pic);
		}
		news.setPictures(set.toArray(new NewsPicture[set.size()]));
		return news;
	}

	public static DBObject news2DBObject(News news) {
		DBObject o = new BasicDBObject();
		o.put("url", news.getUrl().toString());
		o.put("domain", news.getDomain());
		o.put("newspaper", news.getNewspaper());
		o.put("preTitle", news.getPreTitle());
		o.put("title", news.getTitle());
		o.put("subTitle", news.getSubTitle());
		o.put("author", news.getAuthor());
		o.put("layout", news.getLayout());
		o.put("date", news.getDate());
		o.put("content", news.getContent());
		BasicDBList pics = new BasicDBList();
		for (NewsPicture pic : news.getPictures()) {
			DBObject picObj = new BasicDBObject();
			picObj.put("url", pic.getUrl().toString());
			picObj.put("comment", pic.getComment());
			pics.add(picObj);
		}
		o.put("pictures", pics);
		return o;
	}
}
