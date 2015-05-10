package cn.edu.nju.winews;

import cn.edu.nju.winews.config.NewspaperConfigManager;
import cn.edu.nju.winews.dao.NewsDao;
import cn.edu.nju.winews.dao.impl.NewsDaoImpl;

public class NewsWatcherManager {
	public static void main(String[] args) throws Exception {
		NewspaperConfigManager ncm = NewspaperConfigManager.getInstance();
		String[] newspaperArr = ncm.getNewspaperList();
		for(int i = 0; i < newspaperArr.length; i++) {
			System.out.println(newspaperArr[i]);
		}
	}
}
