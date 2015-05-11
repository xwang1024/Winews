package cn.edu.nju.winews.parser;

import java.net.URL;

import cn.edu.nju.winews.po.IPO;

public interface IParser {
	public IPO parse(URL url) throws Exception;
}
