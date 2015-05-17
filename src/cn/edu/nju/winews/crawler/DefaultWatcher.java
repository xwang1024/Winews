package cn.edu.nju.winews.crawler;

import java.lang.reflect.Constructor;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import cn.edu.nju.winews.config.NewspaperConfigManager;

public class DefaultWatcher {
	public static final String MODE_TODAY = "today";
	public static final String MODE_GO_PAST = "toPast";
	public static final GregorianCalendar EDGE_CALENDAR = new GregorianCalendar(2015, 0, 1);
	private static final Logger logger = Logger.getLogger(DefaultWatcher.class.getName());
	private String mode = MODE_TODAY;
	private Thread timer = null;
	private String name;
	private Status status;
	private Date startDate;

	public DefaultWatcher(String name) throws Exception {
		this.name = name;
		this.status = Status.close;
	}

	public Date today() {
		int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		Date date;
		if (hour > 5) {
			date = new Date();
		} else {
			GregorianCalendar calendar = new GregorianCalendar();
			calendar.add(Calendar.DAY_OF_MONTH, -1);
			date = calendar.getTime();
		}
		return date;
	}

	public void start() throws Exception {
		if (status != Status.run) {
			status = Status.run;
			if (mode.equals(MODE_TODAY)) {
				timer = new Thread(new TimerTask() {
					int errorTime = 0;

					@Override
					public void run() {
						logger.log(Level.INFO, "Today {0} thread start...", name);
						while (true) {
							try {
								// logger.log(Level.INFO,
								// "Watcher {0} is added to task pool...",
								// name);
								CrawlerPool.getInstance().addTask(new WatchThread(name, today()));
							} catch (Exception e) {
								if (errorTime > 3) {
									timer.interrupt();
								}
							}
							try {
								Thread.sleep(600000);
							} catch (InterruptedException e) {
								logger.log(Level.INFO, "Today {0} thread interrupted...", name);
							}
						}
					}
				});
				timer.start();
			} else if (mode.equals(MODE_GO_PAST)) {
				final GregorianCalendar calendar = new GregorianCalendar();
				calendar.setTime(startDate);
				timer = new Thread(new TimerTask() {
					int errorTime = 0;

					@Override
					public void run() {
						logger.log(Level.INFO, "Past {0} thread start...", name);
						while (true) {
							try {
								// logger.log(Level.INFO,
								// "Watcher {0} is added to task pool...",
								// name);
								CrawlerPool.getInstance().addLongTask(new WatchThread(name, calendar.getTime()));
								calendar.add(GregorianCalendar.DAY_OF_MONTH, -1);
							} catch (Exception e) {
								e.printStackTrace();
								if (errorTime > 3) {
									errorTime = 0;
									calendar.add(GregorianCalendar.DAY_OF_MONTH, -1);
								}
								errorTime++;
							}
							try {
								Thread.sleep(5000);
							} catch (InterruptedException e) {
								logger.log(Level.INFO, "Past {0} thread interrupted...", name);
							}
						}
					}
				});
				timer.start();
			} else {
				logger.log(Level.WARNING, "Unknown Mode.");
			}
		} else {
			logger.log(Level.WARNING, "Watcher {0} is busy...", name);
		}
	}

	private enum Status {
		close, run
	}

	/**
	 * 使用线程池控制
	 * 
	 * @author Xin Wang
	 *
	 */
	private class WatchThread implements Runnable {
		private final Logger logger = Logger.getLogger(WatchThread.class.getName());
		private String newspaperName;
		private final NewspaperConfigManager ncm;
		private IHandler handler;
		private Date date;

		public WatchThread(String newspaperName, Date date) throws Exception {
			this.newspaperName = newspaperName;
			this.date = date;
			ncm = NewspaperConfigManager.getInstance();
			updateConfig();
		}

		public void updateConfig() throws Exception {
			String handlerName = ncm.getCommonConfig(newspaperName, NewspaperConfigManager.CommonConfig.handler);
			Class<?> handlerClass = Class.forName(handlerName);
			Constructor<?> handleConstructor = handlerClass.getConstructor(String.class);
			handler = (IHandler) handleConstructor.newInstance(newspaperName);

		}

		private String fillDate(String url, String dateFormat, Date date) {
			DateFormat df = new SimpleDateFormat(dateFormat);
			String dateStr = df.format(date.getTime());
			return url.replace("{{DATE}}", dateStr);
		}

		public void run() {
			// 产生监控的URL
			logger.log(Level.INFO, "Task start: {0} - {1}", new Object[] { newspaperName, date });
			if (date == null) {
				date = new Date();
			}
			String dateFormat = ncm.getUrlConfig(newspaperName, date, NewspaperConfigManager.UrlConfig.format_date);
			String nodeFormat = ncm.getUrlConfig(newspaperName, date, NewspaperConfigManager.UrlConfig.format_node);
			String startUrlStr = fillDate(nodeFormat, dateFormat, date);
			URL url = null;
			try {
				url = new URL(startUrlStr);
			} catch (Exception e) {
				logger.log(Level.SEVERE, "Root Url Error: {0}", startUrlStr);
			}

			try {
				handler.setDate(date);
				handler.setStartUrl(url);
				handler.handle();
			} catch (Exception e1) {
				logger.log(Level.SEVERE, "Handler Error: {0}", e1.getMessage());
				e1.printStackTrace();
				status = Status.close;
			}
			logger.log(Level.INFO, "Task end: {0} - {1}", new Object[] { newspaperName, date });
		}
	}

	public void setConfig(Map<String, String> conf) throws Exception {
		if (conf.containsKey("startDate")) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			startDate = sdf.parse(conf.get("startDate"));
		}
		if (conf.containsKey("mode")) {
			String _mode = conf.get("mode");
			switch (_mode) {
			case MODE_TODAY:
			case MODE_GO_PAST:
				mode = _mode;
				break;
			default:
				logger.log(Level.WARNING, "Unknown Mode.");
			}
		}
	}
}
