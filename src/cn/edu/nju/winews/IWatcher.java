package cn.edu.nju.winews;

public interface IWatcher {
	public void start() throws Exception;
	public void close() throws Exception;
	public void restart() throws Exception;
	public void pause() throws Exception;
	public void status() throws Exception;
	
	public int bindHandler(IHandler handler) throws Exception;
	public void unbindHandler(int handlerID) throws Exception;
}
