package cn.edu.nju.winews.handler.impl;

import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
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
import cn.edu.nju.winews.po.NewsPO;
import cn.nju.edu.winews.crawler.handler.impl.ParserFactory;
import cn.nju.edu.winews.crawler.handler.impl.SimpleWiParser;

public class DefaultHandler implements IHandler {
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
	private String format_node;
	private String format_date;
	private String selector_preTitle;
	private String selector_title;
	private String selector_subTitle;
	private String selector_author;
	private String selector_content;
	private String selector_picture;
	
	private Date curDate;

	public DefaultHandler(String newspaperName, String domain, String province) throws Exception {
		this.newspaperName = newspaperName;
		this.domain = domain;
		this.province = province;
	}

	public void setStartUrl(URL startUrl) {
		this.startUrl = startUrl;
	}

	public void setParser(IParser parser) {
		this.parser = parser;
	}

	@Override
	public void handle() throws Exception {
		initConfig();
		getLinks(startUrl,0);
	}
	
	private void initConfig() throws Exception {
		NewspaperConfigManager ncm = NewspaperConfigManager.getInstance();
		pattern_node = ncm.getUrlConfig(newspaperName, NewspaperConfigManager.UrlConfig.pattern_node);
		pattern_content = ncm.getUrlConfig(newspaperName, NewspaperConfigManager.UrlConfig.pattern_content);
		pattern_date = ncm.getUrlConfig(newspaperName, NewspaperConfigManager.UrlConfig.pattern_date);
		format_node = ncm.getUrlConfig(newspaperName, NewspaperConfigManager.UrlConfig.format_node);
		format_date = ncm.getUrlConfig(newspaperName, NewspaperConfigManager.UrlConfig.format_date);
		selector_preTitle = ncm.getSelector(newspaperName, NewspaperConfigManager.Selector.preTitle);
		selector_title = ncm.getSelector(newspaperName, NewspaperConfigManager.Selector.title);
		selector_subTitle = ncm.getSelector(newspaperName, NewspaperConfigManager.Selector.subTitle);
		selector_author = ncm.getSelector(newspaperName, NewspaperConfigManager.Selector.author);
		selector_content = ncm.getSelector(newspaperName, NewspaperConfigManager.Selector.content);
		selector_picture = ncm.getSelector(newspaperName, NewspaperConfigManager.Selector.picture);
		
		curDate = getDateFromLink(startUrl.toString());
		vistiedDao = new VisitedDaoImpl();
		newsDao = new NewsDaoImpl();
	}

	private Date getDateFromLink(String url)
			throws ParseException {
		Pattern p = Pattern.compile(pattern_date);
		Matcher m = p.matcher(url);
		if (m.find()) {
			String dateStr = m.group();
			DateFormat df = new SimpleDateFormat(format_date);
			Date date = df.parse(dateStr);
			return date;
		} else {
			throw new ParseException("Can't find pattern \"" + pattern_date
					+ "\" in url: " + url, 0);
		}
	}

	public void getLinks(URL url, int depth) throws Exception {
		if(maxDepth>0 && depth > maxDepth) {
			return;
		}
		Document doc;
		try {
			doc = Jsoup
					.connect(url.toString())
					.ignoreContentType(true)
					.ignoreHttpErrors(true)
					.timeout(timeoutMillis)
					.userAgent(
							"Mozilla/5.0 (Windows NT 6.1; rv:22.0) Gecko/20100101 Firefox/22.0")
					.get();
		} catch (Exception e1) {
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
					vistiedDao.add(link.toString(),newspaperName); // 链接加入链接列表
					// 如果是节点链接
					if (Pattern.matches(pattern_node, link.toString())) {
						try {
							System.out.println("Node Link: " + link);
							// System.out.print(".");
							getLinks(link, depth++);
						} catch (Exception e) {
							e.printStackTrace();
							continue;
						}
						// 如果是正文链接
					} else if (Pattern.matches(pattern_content,
							link.toString())) {
						System.out.println("Content Link: " + link);
						// System.out.print(".");
						NewsPO news = null;
//						try {
//							SimpleWiParser parser = ParserFactory
//									.createSimpleParser(newspaperName);
//							news = parser.parse(link);
//							news.setDate(curDate);
//							if (news.getLayout().equals("")) {
//								news.setLayout(doc.select("#banzhibar>div")
//										.first().text().trim());
//							}
//						} catch (Exception e) {
//							e.printStackTrace();
//							continue;
//						}
						// 如果有标题就保存
//						if (!news.getTitle().equals("")) {
						newsDao.add(news);
//						}
					}
				}
			}
		}
	}
}
