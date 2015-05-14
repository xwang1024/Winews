package cn.edu.nju.winews.crawler;

import java.net.URL;
import java.util.Date;

public interface IHandler {
	public void setDate(Date date);

	public void setStartUrl(URL url);

	public void handle() throws Exception;

}
