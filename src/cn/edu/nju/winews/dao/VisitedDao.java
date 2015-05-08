package cn.edu.nju.winews.dao;

public interface VisitedDao {
	public boolean isVisited(String url) throws Exception;

	public void add(String url, String newspaper) throws Exception;

	public void clear() throws Exception;
}
