package cn.edu.nju.winews.handler.impl;

import java.io.BufferedWriter;
import java.io.FileWriter;
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
import cn.edu.nju.winews.handler.IHandler;
import cn.edu.nju.winews.parser.IParser;
import cn.edu.nju.winews.po.BriefNewsPO;
import cn.edu.nju.winews.po.NewsPO;

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

	private Date curDate;

	public DefaultHandler(String newspaperName) throws Exception {
		this.newspaperName = newspaperName;
		vistiedDao = new VisitedDaoImpl();
		newsDao = new NewsDaoImpl();
		vistiedDao.clear();
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
			log.log(Level.INFO, "Handler {0}: URL is null!");
			return;
		}
		// 检查起始URL是否被爬取
		if (vistiedDao.isVisited(startUrl.toString())) {
			log.log(Level.INFO, "Handler {0}: URL {1} has been visited!");
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
		pattern_node = ncm.getUrlConfig(newspaperName, NewspaperConfigManager.UrlConfig.pattern_node);
		pattern_content = ncm.getUrlConfig(newspaperName, NewspaperConfigManager.UrlConfig.pattern_content);
		pattern_date = ncm.getUrlConfig(newspaperName, NewspaperConfigManager.UrlConfig.pattern_date);
		format_date = ncm.getUrlConfig(newspaperName, NewspaperConfigManager.UrlConfig.format_date);

		String parserName = ncm.getCommonConfig(newspaperName, NewspaperConfigManager.CommonConfig.parser);
		Constructor<?> constructor = Class.forName(parserName).getConstructor(String.class);
		parser = (IParser) constructor.newInstance(newspaperName);

		curDate = getDateFromLink(startUrl.toString());
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

			if (linkDate.equals(curDate)) {
				// 如果该链接没有被爬取过
				if (!vistiedDao.isVisited(link.toString())) {
					vistiedDao.add(link.toString(), newspaperName); // 链接加入链接列表
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
						NewsPO news = null;
						try {
							news = (NewsPO) parser.parse(link);
							news.setDate(curDate);
							news.setDomain(domain);
							news.setProvince(province);
							if (news.getLayout().equals("")) {
								news.setLayout(doc.select("#banzhibar>div").first().text().trim());
							}
						} catch (Exception e) {
							e.printStackTrace();
							continue;
						}
						// 如果有标题就保存
						newsDao.add(news);
					}
				}
			}
		}
	}

	public static void main(String[] args) throws Exception {
		DefaultHandler h = new DefaultHandler("新华日报");
		h.setNewsDao(new NewsDao() {
			public BriefNewsPO[] search(String[] keywords) throws Exception {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public NewsPO get(String id) throws Exception {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean exists(String id) throws Exception {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public String add(NewsPO news) throws Exception {
				BufferedWriter bfr = new BufferedWriter(new FileWriter("tmp/" + news.getTitle() + ".txt"));
				bfr.write(news.toString());
				bfr.close();
				return null;
			}
		});
		h.setVisitedDao(new VisitedDao() {
			HashSet<String> set = new HashSet<String>();

			public boolean isVisited(String url) throws Exception {
				return set.contains(url);
			}

			@Override
			public void clear() throws Exception {
				set.clear();
			}

			@Override
			public void add(String url, String newspaper) throws Exception {
				set.add(url);
			}
		});
		h.setStartUrl(new URL("http://xh.xhby.net/mp2/html/2015-05/11/node_2.htm"));
		h.handle();
	}

}
