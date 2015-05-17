package cn.edu.nju.winews.crawler;

import java.io.Serializable;

import org.jsoup.nodes.Document;

public interface IParser {
	public Serializable parse(Document doc) throws Exception;
}
