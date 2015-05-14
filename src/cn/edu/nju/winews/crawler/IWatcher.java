package cn.edu.nju.winews.crawler;

public interface IWatcher {
	public void start() throws Exception;

	public void close() throws Exception;

	public void restart() throws Exception;

	public void status() throws Exception;

}
