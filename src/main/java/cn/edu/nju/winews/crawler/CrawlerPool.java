package cn.edu.nju.winews.crawler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CrawlerPool {
	private static CrawlerPool instance = new CrawlerPool();
	private ExecutorService es;

	private CrawlerPool() {
		es = Executors.newFixedThreadPool(6);
	}

	public static CrawlerPool getInstance() {
		return instance;
	}
	
	public void execute(Runnable command) {
		es.execute(command);
	}
	
	public void shutdown() throws InterruptedException {
		es.awaitTermination(10, TimeUnit.MINUTES);
	}
}
