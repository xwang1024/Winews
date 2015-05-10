package cn.edu.nju.winews.watcher;

import cn.edu.nju.winews.handler.IHandler;

public interface IWatcher {
	public void start() throws Exception;
	public void close() throws Exception;
	public void restart() throws Exception;
	public void status() throws Exception;
	
}
