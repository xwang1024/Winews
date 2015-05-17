package cn.edu.nju.winews;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import cn.edu.nju.winews.config.NewspaperConfigManager;
import cn.edu.nju.winews.crawler.DefaultWatcher;

public class NewsWatcherManager {

	private int boundaryHour = 5;

	public void start() throws Exception {
		NewspaperConfigManager ncm = NewspaperConfigManager.getInstance();
		String[] newspaperArr = ncm.getNewspaperList();
		for (int i = 0; i < newspaperArr.length; i++) {
			DefaultWatcher watcher = new DefaultWatcher(newspaperArr[i]);
			Map<String, String> conf = new HashMap<String, String>();
			conf.put("startDate", this.getStartDate());
			conf.put("mode", DefaultWatcher.MODE_TODAY);
			watcher.setConfig(conf);
			watcher.start();
			Thread.sleep(500);
		}
		
		for (int i = 0; i < newspaperArr.length; i++) {
			DefaultWatcher watcher1 = new DefaultWatcher(newspaperArr[i]);
			Map<String, String> conf1 = new HashMap<String, String>();
			conf1.put("startDate", this.getStartDate());
			conf1.put("mode", DefaultWatcher.MODE_GO_PAST);
			watcher1.setConfig(conf1);
			watcher1.start();
			Thread.sleep(500);
		}
	}

	public String getStartDate() {
		int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		Date date;
		if (hour > boundaryHour) {
			date = new Date();
		} else {
			GregorianCalendar calendar = new GregorianCalendar();
			calendar.add(Calendar.DAY_OF_MONTH, -1);
			date = calendar.getTime();
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(date);
	}
}
