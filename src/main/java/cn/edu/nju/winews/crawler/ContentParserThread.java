package cn.edu.nju.winews.crawler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.WebConnection;

import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.edu.nju.winews.crawler.config.ConfigManager;
import cn.edu.nju.winews.dao.URLDao;
import cn.edu.nju.winews.dao.impl.URLDaoImpl;
import cn.edu.nju.winews.model.UrlRecord;
import cn.edu.nju.winews.plugin.IPlugin;

public class ContentParserThread {
	private static final Logger logger = LoggerFactory.getLogger(ContentParserThread.class);
	private static final int PACK_SIZE = 20;

	private ConfigManager cm;
	private URLDao urlDao;
	private List<IPlugin> plugins = new ArrayList<IPlugin>();
	private Set<ContentUrl> contentUrls = Collections.synchronizedSet(new HashSet<ContentUrl>());

	public ContentParserThread() throws IOException {
		cm = ConfigManager.getInstance();
		urlDao = URLDaoImpl.getInstance();
	}

	public void addPlugin(IPlugin plugin) {
		plugins.add(plugin);
	}

	public void addTask(HashSet<ContentUrl> urlSet) {
		logger.info("Receive task pack, size={}", urlSet.size());
		contentUrls.addAll(urlSet);
		weakup();
	}

	private void weakup() {
		int times = contentUrls.size() / PACK_SIZE;
		Iterator<ContentUrl> itr = contentUrls.iterator();
		for (int i = 0; i < times; i++) {
			ContentUrl[] urls = new ContentUrl[PACK_SIZE];
			for (int j = 0; j < PACK_SIZE; j++) {
				urls[j] = itr.next();
				itr.remove();
			}
			CrawlerPool.getInstance().execute(new ParseContentTask(urls));
		}
	}

	private class ParseContentTask implements Runnable {
		private ContentUrl[] urls;

		public ParseContentTask(ContentUrl[] urls) {
			this.urls = urls;
		}

		public void run() {
			for (int i = 0; i < urls.length; i++) {
				logger.info("Processing content url: {}", urls[i].getUrl());
				ContentUrl cUrl = urls[i];
				String charset = cm.getConfig().get("winews", "dataSource", "newspaper", cUrl.getSourceName(), "charset", cUrl.getConfDate());
				Document doc;
				UrlRecord record = new UrlRecord(urls[i].getUrl().toString(), urls[i].getSourceName(), new Date().getTime() / 1000);
				try {
					WebConnection conn = new WebConnection(cUrl.getUrl(), charset);
					doc = conn.get();
					urlDao.visit(record);
					for (IPlugin plugin : plugins) {
						plugin.process(doc, urls[i]);
					}
				} catch (IOException e) {
					logger.info("Suspend content: {}, reason: {}", urls[i].getUrl(), e.getMessage());
					urlDao.suspend(new UrlRecord(urls[i].getFatherDoc().baseUri().toString(), urls[i].getSourceName(), new Date().getTime() / 1000));
				}
			}
		}
	}
}
