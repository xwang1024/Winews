package cn.edu.nju.winews.dao;

import cn.edu.nju.winews.model.UrlRecord;

public interface URLDao {
	public URLDao visit(UrlRecord record); // 删掉suspend或candidate
	public URLDao suspend(UrlRecord record); // 删掉candidate
	
	public boolean isVisited(String url);
	public boolean isSuspend(String url);
	
	public UrlRecord[] getSuspend(String sourceName, int vol);
}
