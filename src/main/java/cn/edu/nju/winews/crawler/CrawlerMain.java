package cn.edu.nju.winews.crawler;

import org.apache.commons.vfs2.FileSystemException;

import cn.edu.nju.winews.plugin.IPlugin;

public class CrawlerMain {
	public void start() throws FileSystemException {
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
