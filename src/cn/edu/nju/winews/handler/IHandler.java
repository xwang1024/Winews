package cn.edu.nju.winews.handler;

import java.net.URL;

public interface IHandler {
	public void setStartUrl(URL url);

	public void handle() throws Exception;
}
