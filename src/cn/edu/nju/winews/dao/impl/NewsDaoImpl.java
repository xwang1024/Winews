package cn.edu.nju.winews.dao.impl;

import cn.edu.nju.winews.dao.NewsDao;
import cn.edu.nju.winews.po.BriefNewsPO;
import cn.edu.nju.winews.po.NewsPO;

public class NewsDaoImpl implements NewsDao{

	@Override
	public boolean exists(String id) throws Exception {
		return false;
	}

	@Override
	public String add(NewsPO news) throws Exception {
		return null;
	}

	@Override
	public NewsPO get(String id) throws Exception {
		return null;
	}

	@Override
	public BriefNewsPO[] search(String[] keywords) throws Exception {
		return null;
	}

}
