package cn.edu.nju.winews.crawler;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import cn.edu.nju.winews.crawler.config.ConfigManager;
import cn.edu.nju.winews.plugin.IPlugin;

public class CrawlerMain {
	public void start() throws ParserConfigurationException, SAXException, IOException {
		ConfigManager configManager = ConfigManager.getInstance();
		configManager.init();
		configManager.startWatchThread();

		ContentParser parser = new ContentParser();
		parser.start();
		parser.addPlugin(new IPlugin() {
		});

		CrawlerHandler handler = new CrawlerHandler(configManager.getConfig());
		handler.start();

		HistoryCrawlerHandler historyHandler = new HistoryCrawlerHandler(configManager.getConfig());
		historyHandler.start();
	}
}
