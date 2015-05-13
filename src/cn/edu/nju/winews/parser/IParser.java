package cn.edu.nju.winews.parser;

import java.io.Serializable;
import java.net.URL;

public interface IParser {
	public Serializable parse(URL url) throws Exception;
}
