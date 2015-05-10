package cn.edu.nju.winews.watcher.impl;

import java.lang.reflect.Constructor;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import cn.edu.nju.winews.config.NewspaperConfigManager;
import cn.edu.nju.winews.handler.IHandler;
import cn.edu.nju.winews.parser.IParser;
import cn.edu.nju.winews.watcher.IWatcher;

public class DefaultWatcher implements IWatcher {
	private static final Logger log = Logger.getLogger(DefaultWatcher.class
			.getName());
	private String name;
	private Thread t;
	private Status status;

	// TODO 状态监控，如果线程突然结束

	public DefaultWatcher(String name) throws Exception {
		this.name = name;
		this.status = Status.close;
		t = new Thread(new WatchThread(name));
	}

	@Override
	public void start() throws Exception {
		if (status != Status.run && status != Status.restart) {
			log.log(Level.INFO, "Watcher {0} is starting...", name);
			status = Status.run;
			t.start();
		} else {
			log.log(Level.INFO, "Watcher {0} is busy...", name);
		}
	}

	@Override
	public void close() throws Exception {
		if (status == Status.run) {
			log.log(Level.INFO, "Watcher {0} is closing...", name);
			status = Status.close;
			if (t.isAlive()) {
				t.interrupt();
			} else {
				log.log(Level.INFO, "Watcher {0} was already dead...", name);
			}
		} else {
			log.log(Level.INFO, "Watcher {0} is busy or not running...", name);
		}
	}

	@Override
	public void restart() throws Exception {
		if (status == Status.run) {
			log.log(Level.INFO, "Watcher {0} is restarting...", name);
			status = Status.restart;
			if (t.isAlive()) {
				t.interrupt();
			} else {
				log.log(Level.INFO, "Watcher {0} was already dead...", name);
			}
			status = Status.run;
			t.start();
		} else {
			log.log(Level.INFO, "Watcher {0} is busy or not running...", name);
		}
	}

	@Override
	public void status() throws Exception {
		log.log(Level.INFO, "Watcher {0}: {1}",
				new String[] { name, status.toString() });
	}

	private enum Status {
		close, pause, run, restart
	}

	/**
	 * 一个线程只监控一个报刊网站
	 * 
	 * @author Xin Wang
	 *
	 */
	private class WatchThread implements Runnable {
		private String newspaperName;

		private static final int interval = 60000;

		private final NewspaperConfigManager ncm;

		private String domain;
		private String province;
		private IHandler handler;
		private IParser parser;

		public WatchThread(String newspaperName) throws Exception {
			this.newspaperName = newspaperName;
			ncm = NewspaperConfigManager.getInstance();
			updateConfig();
		}

		public void updateConfig() throws Exception {
			domain = ncm.getCommonConfig(newspaperName,
					NewspaperConfigManager.CommonConfig.domain);
			province = ncm.getCommonConfig(newspaperName,
					NewspaperConfigManager.CommonConfig.province);
			String handlerName = ncm.getCommonConfig(newspaperName,
					NewspaperConfigManager.CommonConfig.handler);
			String parserName = ncm.getCommonConfig(newspaperName,
					NewspaperConfigManager.CommonConfig.parser);

			Class<?> handlerClass = Class.forName(handlerName);
			Constructor<?> handleConstructor = handlerClass.getConstructor(
					String.class, String.class, String.class);
			handler = (IHandler) handleConstructor.newInstance(newspaperName,
					domain, province);
			parser = (IParser) Class.forName(parserName).newInstance();
		}

		private String fillDate(String url, String dateFormat, Date date) {
			DateFormat df = new SimpleDateFormat(dateFormat);
			String dateStr = df.format(date.getTime());
			return url.replace("{{DATE}}", dateStr);
		}

		public void run() {
			// 无限执行，除非打断
			while (true) {
				// 产生监控的URL
				String startUrlStr = fillDate(ncm.getUrlConfig(newspaperName,
						NewspaperConfigManager.UrlConfig.format_node),
						ncm.getUrlConfig(newspaperName,
								NewspaperConfigManager.UrlConfig.format_date),
						new Date());

				URL url;
				try {
					url = new URL(startUrlStr);
				} catch (Exception e) {
					log.log(Level.SEVERE, "Root Url Error: {0}", startUrlStr);
					break;
				}

				try {
					handler.setStartUrl(url);
					handler.setParser(parser);
					handler.handle();
				} catch (Exception e1) {
					log.log(Level.SEVERE, "Handler Error: {0}", e1.getMessage());
					e1.printStackTrace();
					break;
				}

				try {
					Thread.sleep(interval);
				} catch (InterruptedException e) {
					log.log(Level.INFO, "Thread {0} was interrupted...",
							newspaperName);
					break;
				}
			}
		}
	}
}
