package cn.edu.nju.winews.dao;

import cn.edu.nju.winews.model.VisitedRecord;

public interface VisitedDao {
	public boolean isVisited(String url) throws Exception;

	public void add(VisitedRecord record) throws Exception;

	public void clear() throws Exception;
}
