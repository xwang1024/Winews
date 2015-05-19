package cn.edu.nju.winews.tool;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import cn.edu.nju.winews.config.NewspaperConfigManager;
import cn.edu.nju.winews.crawler.KeywordProcessor;
import cn.edu.nju.winews.dao.impl.NewsDaoImpl;
import cn.edu.nju.winews.dao.impl.VisitedDaoImpl;
import cn.edu.nju.winews.model.News;
import cn.edu.nju.winews.model.NewsPicture;
import cn.edu.nju.winews.model.VisitedRecord;

import com.mongodb.BasicDBList;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class OldMongoTrans {
	public static News DBObject2News(DBObject o) {
		News news = new News();
		news.setUrl(o.get("url").toString());
		news.setDomain(o.get("domain").toString());
		news.setNewspaper(o.get("newspaper").toString());
		news.setPreTitle(o.get("preTitle").toString());
		news.setTitle(o.get("title").toString());
		news.setSubTitle(o.get("subTitle").toString());
		news.setAuthor("");
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
	
	public static void main(String[] args) throws Exception {
		VisitedDaoImpl vistiedDao = new VisitedDaoImpl();
		NewsDaoImpl newsDao = new NewsDaoImpl();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		MongoClient mongo = new MongoClient("localhost", 27017);
		DB db = mongo.getDB("oldwinews");
		DBCollection newColl = db.getCollection("news");
		for (String name : db.getCollectionNames()) {
			if (name.contains("报")) {
				DBCollection coll = db.getCollection(name);
				DBCursor cur = coll.find();
				while (cur.hasNext()) {
					DBObject origin = cur.next();
					origin.put("domain", NewspaperConfigManager.getInstance().getCommonConfig(name, NewspaperConfigManager.CommonConfig.domain));
					origin.put("newspaper",name);
					origin.removeField("source");
					String subTitle = (String) origin.get("sub_title");
					if(subTitle.startsWith("——")) {
						origin.put("preTitle","");
						origin.put("subTitle",subTitle);
					} else {
						origin.put("subTitle","");
						origin.put("preTitle",subTitle);
					}
					origin.removeField("sub_Title");
					String layout = (String) origin.get("layout");
					layout = layout.replace(" ", "").replace(":", "：").replace("●", "：").trim();
					origin.put("layout",layout);
					String dateStr = (String) origin.get("date");
					Date d = sdf.parse(dateStr);
					origin.put("date",d);
					News news = DBObject2News(origin);
					System.out.println(news.getUrl());
					
					
					if (news != null) {
						KeywordProcessor.getInstance().addNewsTask(news);
						try {
							newsDao.add(news);
							VisitedRecord vr = new VisitedRecord();
							vr.setNewspaper(name);
							vr.setTimestamp(new Date());
							vr.setUrl(news.getUrl());
							vistiedDao.add(vr);
						} catch (Exception e) {
							if (e.getMessage().contains("duplicate key")) {
								System.out.println("This news has been added, url: "+ news.getUrl());
								continue;
							} else {
								e.printStackTrace();
							}
						}
					}
					
					newColl.save(origin);
				}
			}
		}
	}
}
