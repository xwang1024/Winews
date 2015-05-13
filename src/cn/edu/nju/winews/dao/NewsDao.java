package cn.edu.nju.winews.dao;

import cn.edu.nju.winews.model.BriefNews;
import cn.edu.nju.winews.model.News;

public interface NewsDao {
	public boolean exists(String url) throws Exception;
	public String add(News news) throws Exception;
	public News get(String url) throws Exception;
	public BriefNews[] search(String[] keywords) throws Exception;
}
