package cn.edu.nju.winews.crawler;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.WebConnection;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.edu.nju.winews.crawler.config.Config;
import cn.edu.nju.winews.crawler.config.ConfigManager;
import cn.edu.nju.winews.dao.URLDao;
import cn.edu.nju.winews.dao.impl.URLDaoImpl;
import cn.edu.nju.winews.model.UrlRecord;

public class CrawlerHandlerThread {
	private static final Logger logger = LoggerFactory.getLogger(CrawlerHandlerThread.class);

	private Thread t;
	private ConfigManager cm;
	private ContentParserThread parserThread;
	private URLDao urlDao;
	private Date date;
	private String confDate;

	public CrawlerHandlerThread() throws IOException {
		cm = ConfigManager.getInstance();
		urlDao = URLDaoImpl.getInstance();
	}

	public ContentParserThread getParserThread() {
		return parserThread;
	}

	public void setParserThread(ContentParserThread parserThread) {
		this.parserThread = parserThread;
	}

	public URLDao getUrlDao() {
		return urlDao;
	}

	public void setUrlDao(URLDao urlDao) {
		this.urlDao = urlDao;
	}

	public void start() {
		t = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					initDate();
					String[] sourceNames = cm.getConfig().getNameList("winews", "dataSource", "newspaper");
					for (int i = 0; i < sourceNames.length; i++) {
						String rootUrl = getRootUrl(sourceNames[i]);
						try {
							logger.info("Speed testing... - {} - {}", sourceNames[i], rootUrl);
							WebConnection conn = new WebConnection(rootUrl);
							int accessMillis = conn.getAccessMillis();
							SiteStatManager.getInstance().put(sourceNames[i], new StatInfo(sourceNames[i], accessMillis));
						} catch (MalformedURLException e) {
							logger.warn("{} url error: {}", sourceNames[i], rootUrl);
							continue;
						}
					}
					sourceNames = SiteStatManager.getInstance().getSpeedKeyList();
					for (int i = 0; i < sourceNames.length; i++) {
						GetContentLinksTask task = new GetContentLinksTask(sourceNames[i]);
						logger.info("{} task is added...", sourceNames[i]);
						CrawlerPool.getInstance().execute(task);
					}
					try {
						logger.info("Sleeping...");
						Thread.sleep(1800000);
						logger.info("Awake...");
					} catch (InterruptedException e) {
					}
				}
			}
		});
		t.run();
	}

	public String getRootUrl(String newspaperName) {
		String rootUrl = cm.getConfig().get("winews", "dataSource", "newspaper", newspaperName, "url", "rootFormat", confDate);
		String dateFormat = cm.getConfig().get("winews", "dataSource", "newspaper", newspaperName, "date", "format", confDate);
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		rootUrl = rootUrl.replace("{{DATE}}", sdf.format(date));
		return rootUrl;
	}

	private void initDate() {
		String conf = cm.getConfig().get("winews", "global", "dayBoundary");
		int boundary = 5;
		if (conf != null) {
			try {
				boundary = Integer.parseInt(conf);
			} catch (NumberFormatException e) {
			}
		}
		int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		if (hour > boundary) {
			date = new Date();
		} else {
			GregorianCalendar calendar = new GregorianCalendar();
			calendar.add(Calendar.DAY_OF_MONTH, -1);
			date = calendar.getTime();
		}
		confDate = "@" + Config.DEFAULT_SDF.format(date);
	}

	public class GetContentLinksTask implements Runnable {
		private String newspaperName;
		private String charset;
		private String nodePattern;
		private String contentPattern;
		private String datePattern;
		private String dateFormat;
		private String rootUrl;
		private HashSet<ContentUrl> contentUrlSet = new HashSet<ContentUrl>();

		public GetContentLinksTask(String newspaperName) {
			this.newspaperName = newspaperName;
			this.charset = cm.getConfig().get("winews", "dataSource", "newspaper", newspaperName, "charset", confDate);
			this.nodePattern = cm.getConfig().get("winews", "dataSource", "newspaper", newspaperName, "url", "nodePattern", confDate);
			this.contentPattern = cm.getConfig().get("winews", "dataSource", "newspaper", newspaperName, "url", "contentPattern", confDate);
			this.datePattern = cm.getConfig().get("winews", "dataSource", "newspaper", newspaperName, "date", "pattern", confDate);
			this.dateFormat = cm.getConfig().get("winews", "dataSource", "newspaper", newspaperName, "date", "format", confDate);
			this.rootUrl = getRootUrl(newspaperName);
		}

		public void run() {
			logger.info("{} task is starting...", newspaperName);
			try {
				getLinks(new URL(rootUrl));
				// 执行之前失败的任务，有多少取多少
				UrlRecord[] records = urlDao.getSuspend(newspaperName, -1);
				for(int i = 0; i < records.length; i++) {
					getLinks(new URL(records[i].getUrl()));
				}
				parserThread.addTask(contentUrlSet);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			logger.info("{} task is end...", newspaperName);
		}

		public void getLinks(URL url) {
			Document doc;
			HashSet<URL> childrenUrl;
			try {
				WebConnection conn = new WebConnection(url, charset);
				doc = conn.get();
				Elements links = doc.getElementsByTag("a");
				links.addAll(doc.getElementsByTag("area"));
				UrlFilter urlFilter = new UrlFilter();
				childrenUrl = urlFilter.filter(doc.baseUri(), links);
			} catch (IOException e) {
				logger.info("Suspend url: {}, reason: {}", url, e.getMessage());
				urlDao.suspend(new UrlRecord(url.toString(), newspaperName, new Date().getTime() / 1000));
				return;
			}
			for (URL link : childrenUrl) {
				// 节点链接日期应该等于当前日期
				if (dateEquals(url.toString())) {
					// 如果该链接没有被访问/失效/预备过
					if (!urlDao.isVisited(link.toString())) {
						UrlRecord record = new UrlRecord(link.toString(), newspaperName, new Date().getTime() / 1000);
						urlDao.visit(record); // 链接加入链接列表
						// 如果是节点链接
						if (Pattern.matches(nodePattern, link.toString())) {
							logger.debug(newspaperName + "\t  Node URL: " + link);
							getLinks(link);
							// 如果是正文链接
						} else if (Pattern.matches(contentPattern, link.toString())) {
							logger.debug(newspaperName + "\t  Content URL: " + link);
							ContentUrl contentUrl = new ContentUrl(link, newspaperName, doc, date, confDate);
							contentUrlSet.add(contentUrl);
						}
					}
				}
			}
		}

		private boolean dateEquals(String url) {
			try {
				Pattern p = Pattern.compile(datePattern);
				Matcher m = p.matcher(url);
				if (m.find()) {
					String dateStr = m.group();
					DateFormat df = new SimpleDateFormat(dateFormat);
					Date date = df.parse(dateStr);
					String criteriaDateStr = Config.DEFAULT_SDF.format(date);
					return criteriaDateStr.equals(confDate.substring(1));
				} else {
					return false;
				}
			} catch (ParseException e) {
				logger.debug("url: {}, datePattern: {}, dateFormat: {}, date: {}", url, datePattern, dateFormat, confDate.substring(1));
				return false;
			}
		}
	}
}
