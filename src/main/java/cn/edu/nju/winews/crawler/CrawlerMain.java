package cn.edu.nju.winews.crawler;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import cn.edu.nju.winews.crawler.config.ConfigManager;
import cn.edu.nju.winews.plugin.IPlugin;

public class CrawlerMain {
	private static final Logger logger = LoggerFactory.getLogger(CrawlerMain.class);

	public void start() throws ParserConfigurationException, SAXException, IOException {
		ConfigManager configManager = ConfigManager.getInstance();
		configManager.init();
		configManager.startWatchThread();

		ContentParserThread parserThread = new ContentParserThread();
		parserThread.addPlugin(new IPlugin() {

			@Override
			public void process(Document doc, ContentUrl url) {
				// TODO Auto-generated method stub
				
			}
		});

		CrawlerHandlerThread handlerThread = new CrawlerHandlerThread();
		handlerThread.setParserThread(parserThread);
		handlerThread.start();

		HistoryCrawlerHandler historyHandler = new HistoryCrawlerHandler(configManager.getConfig());
		historyHandler.start();
	}
}
