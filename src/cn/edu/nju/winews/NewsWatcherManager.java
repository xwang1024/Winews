package cn.edu.nju.winews;

import java.util.HashMap;
import java.util.Map;

import cn.edu.nju.winews.config.NewspaperConfigManager;
import cn.edu.nju.winews.crawler.DefaultWatcher;
import cn.edu.nju.winews.crawler.IWatcher;

public class NewsWatcherManager {
	public void start() throws Exception {
		NewspaperConfigManager ncm = NewspaperConfigManager.getInstance();
		String[] newspaperArr = ncm.getNewspaperList();
		for (int i = 0; i < newspaperArr.length; i++) {
			IWatcher watcher = new DefaultWatcher(newspaperArr[i]);
			Map<String, String> conf = new HashMap<String, String>();
			conf.put("startDate", "2015-05-17");
			watcher.setConfig(conf);
			watcher.start();
			Thread.sleep(200);
		}
	}
}
