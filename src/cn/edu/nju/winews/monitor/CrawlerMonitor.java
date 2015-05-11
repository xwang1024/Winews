package cn.edu.nju.winews.monitor;

public class CrawlerMonitor {
	private static CrawlerMonitor instance = null;

	private CrawlerMonitor() {
	}

	public static CrawlerMonitor getInstance() {
		if (instance == null) {
			instance = new CrawlerMonitor();
		}
		return instance;
	}
	
	public void register(String name) {
		
	}
	
	public void update() {
		
	}
}
