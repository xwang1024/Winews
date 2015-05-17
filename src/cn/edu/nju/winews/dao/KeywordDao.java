package cn.edu.nju.winews.dao;

public interface KeywordDao {

	public String[] search(String[] keywords) throws Exception;

	public void addSearchIndex(String kw, String url) throws Exception;
}
