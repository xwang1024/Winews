package cn.edu.nju.winews.crawler;

import java.lang.reflect.Constructor;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import cn.edu.nju.winews.config.NewspaperConfigManager;
import cn.edu.nju.winews.dao.NewsDao;
import cn.edu.nju.winews.dao.VisitedDao;
import cn.edu.nju.winews.dao.impl.NewsDaoImpl;
import cn.edu.nju.winews.dao.impl.VisitedDaoImpl;
import cn.edu.nju.winews.model.News;
import cn.edu.nju.winews.model.VisitedRecord;

public class DefaultHandler implements IHandler {
	private static final Logger log = Logger.getLogger(DefaultHandler.class.getName());
	private static final int timeoutMillis = 5000;
	private static final int maxDepth = 5;

	private VisitedDao vistiedDao;
	private NewsDao newsDao;

	private String newspaperName;
	private String domain;
	private String province;
	private URL startUrl;
	private IParser parser;

	private String pattern_node;
	private String pattern_content;
	private String pattern_date;
	private String format_date;

	private Date date;

	public DefaultHandler(String newspaperName) {
		this.newspaperName = newspaperName;

		try {
			vistiedDao = new VisitedDaoImpl();
			newsDao = new NewsDaoImpl();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void setStartUrl(URL startUrl) {
		this.startUrl = startUrl;
	}

	public void setVisitedDao(VisitedDao visitedDao) {
		this.vistiedDao = visitedDao;
	}

	public void setNewsDao(NewsDao newsDao) {
		this.newsDao = newsDao;
	}

	@Override
	public void handle() throws Exception {
		// 检查起始URL是否为空
		if (startUrl == null) {
			log.log(Level.INFO, "Handler {0}: URL is null!", newspaperName);
			return;
		}
		// 检查起始URL是否被爬取
		if (vistiedDao.isVisited(startUrl.toString())) {
			log.log(Level.INFO, "Handler {0}: URL {1} has been visited!", new String[] { newspaperName, startUrl.toString() });
			return;
		}
		// 初始化配置
		initConfig();
		// 遍历URL
		getLinks(startUrl, 0);
	}

	private void initConfig() throws Exception {
		NewspaperConfigManager ncm = NewspaperConfigManager.getInstance();
		domain = ncm.getCommonConfig(newspaperName, NewspaperConfigManager.CommonConfig.domain);
		province = ncm.getCommonConfig(newspaperName, NewspaperConfigManager.CommonConfig.province);
		pattern_node = ncm.getUrlConfig(newspaperName, date, NewspaperConfigManager.UrlConfig.pattern_node);
		pattern_content = ncm.getUrlConfig(newspaperName, date, NewspaperConfigManager.UrlConfig.pattern_content);
		pattern_date = ncm.getUrlConfig(newspaperName, date, NewspaperConfigManager.UrlConfig.pattern_date);
		format_date = ncm.getUrlConfig(newspaperName, date, NewspaperConfigManager.UrlConfig.format_date);
		// 此处必须重置时间
		date = getDateFromLink(startUrl.toString());

		String parserName = ncm.getCommonConfig(newspaperName, NewspaperConfigManager.CommonConfig.parser);
		Constructor<?> constructor = Class.forName(parserName).getConstructor(String.class, Date.class);
		parser = (IParser) constructor.newInstance(newspaperName, date);

	}

	private Date getDateFromLink(String url) throws ParseException {
		Pattern p = Pattern.compile(pattern_date);
		Matcher m = p.matcher(url);
		if (m.find()) {
			String dateStr = m.group();
			DateFormat df = new SimpleDateFormat(format_date);
			Date date = df.parse(dateStr);
			return date;
		} else {
			throw new ParseException("Can't find pattern \"" + pattern_date + "\" in url: " + url, 0);
		}
	}

	public void getLinks(URL url, int depth) throws Exception {
		if (maxDepth > 0 && depth > maxDepth) {
			return;
		}
		Document doc;
		try {
			doc = Jsoup.connect(url.toString()).ignoreContentType(true).ignoreHttpErrors(true).timeout(timeoutMillis)
					.userAgent("Mozilla/5.0 (Windows NT 6.1; rv:22.0) Gecko/20100101 Firefox/22.0").get();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		Elements links = doc.getElementsByTag("a");
		links.addAll(doc.getElementsByTag("area"));
		DefaultUrlFilter urlFilter = new DefaultUrlFilter();
		HashSet<URL> urlSet = urlFilter.filter(doc.baseUri(), links);
		for (URL link : urlSet) {
			// Check URL date
			Date linkDate = getDateFromLink(url.toString());
			// 节点链接日期应该等于当前日期

			if (linkDate.equals(date)) {
				// 如果该链接没有被爬取过
				if (!vistiedDao.isVisited(link.toString())) {
					VisitedRecord record = new VisitedRecord();
					record.setNewspaper(newspaperName);
					record.setUrl(link.toString());
					record.setTimestamp(new Date());
					vistiedDao.add(record); // 链接加入链接列表
					// 如果是节点链接
					if (Pattern.matches(pattern_node, link.toString())) {
						log.log(Level.INFO, "Newspaper: {0}, Node URL: {1}", new String[] { newspaperName, link.toString() });
						try {
							getLinks(link, depth++);
						} catch (Exception e) {
							e.printStackTrace();
							continue;
						}
						// 如果是正文链接
					} else if (Pattern.matches(pattern_content, link.toString())) {
						log.log(Level.INFO, "Newspaper: {0}, Content URL: {1}", new String[] { newspaperName, link.toString() });
						News news = null;
						try {
							news = (News) parser.parse(link);
							news.setDate(date);
							news.setDomain(domain);
							news.setProvince(province);
							// if (news.getLayout().equals("")) {
							// news.setLayout(doc.select("#banzhibar>div").first().text().trim());
							// }
						} catch (Exception e) {
							e.printStackTrace();
							continue;
						}
						if (news != null) {
							KeywordProcessor.getInstance().addNewsTask(news);
							newsDao.add(news);
						}
					}
				}
			}
		}
	}
}
