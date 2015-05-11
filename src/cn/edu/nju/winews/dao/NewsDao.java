package cn.edu.nju.winews.dao;

import cn.edu.nju.winews.po.BriefNewsPO;
import cn.edu.nju.winews.po.NewsPO;

public interface NewsDao {
	public boolean exists(String url) throws Exception;
	public String add(NewsPO news) throws Exception;
	public NewsPO get(String url) throws Exception;
	public BriefNewsPO[] search(String[] keywords) throws Exception;
}
