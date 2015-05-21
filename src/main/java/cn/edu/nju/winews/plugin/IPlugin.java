package cn.edu.nju.winews.plugin;

import org.jsoup.nodes.Document;

import cn.edu.nju.winews.crawler.ContentUrl;

public interface IPlugin {
	public void process(Document doc, ContentUrl url);
}
