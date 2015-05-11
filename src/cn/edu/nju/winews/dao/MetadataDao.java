package cn.edu.nju.winews.dao;

public interface MetadataDao {
	public String registerSource(String sourceName, String domain, String type) throws Exception;
	public String getSourceName(String url) throws Exception;
	public String getDomain(String sourceName) throws Exception;
	public String getType(String sourceName) throws Exception;
	public int getDataCount(String sourceName) throws Exception;
	public String[] getSourceList(String type) throws Exception;
	public String[] getSourceTypeList() throws Exception;
}
