package cn.edu.nju.winews.handler;

import java.net.URL;

import cn.edu.nju.winews.parser.IParser;

public interface IHandler {
	public void setStartUrl(URL url);

	public void setParser(IParser parser);

	public void handle() throws Exception;
}
