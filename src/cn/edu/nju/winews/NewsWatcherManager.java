package cn.edu.nju.winews;

import cn.edu.nju.winews.config.NewspaperConfigManager;
import cn.edu.nju.winews.watcher.IWatcher;
import cn.edu.nju.winews.watcher.impl.DefaultWatcher;

public class NewsWatcherManager {
	public static void main(String[] args) throws Exception {
		NewspaperConfigManager ncm = NewspaperConfigManager.getInstance();
		String[] newspaperArr = ncm.getNewspaperList();
		for (int i = 0; i < newspaperArr.length; i++) {
			IWatcher watcher = new DefaultWatcher(newspaperArr[i]);
			watcher.start();
		}
	}
}
