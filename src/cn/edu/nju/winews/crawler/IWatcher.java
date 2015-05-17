package cn.edu.nju.winews.crawler;

import java.util.Map;

public interface IWatcher {
	public void start() throws Exception;

	public void close() throws Exception;

	public void restart() throws Exception;

	public void status() throws Exception;
	
	public void setConfig(Map<String,String> conf) throws Exception;

}
