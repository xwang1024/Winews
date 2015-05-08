package cn.edu.nju.winews.dao;

import cn.edu.nju.winews.po.BriefNewsPO;
import cn.edu.nju.winews.po.NewsPO;

public interface NewsDao {
	public boolean exists(String id) throws Exception;
	public String add(NewsPO news) throws Exception;
	public NewsPO get(String id) throws Exception;
	public BriefNewsPO[] search(String[] keywords) throws Exception;
}
