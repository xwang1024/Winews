package cn.edu.nju.winews.crawler;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import cn.edu.nju.winews.config.NewspaperConfigManager;
import cn.edu.nju.winews.model.News;
import cn.edu.nju.winews.model.NewsPicture;

public class DefaultParser implements IParser {
	private static final Logger log = Logger.getLogger(DefaultParser.class.getName());

	private String newspaperName;

	private int timeoutMillis;
	private Date date;

	private String selector_preTitle;
	private String selector_title;
	private String selector_subTitle;
	private String selector_layout;
	private String selector_author;
	private String selector_content;
	private String selector_picture;

	public DefaultParser(String newspaperName, Date date) throws Exception {
		this.newspaperName = newspaperName;
		this.date = date;
		initConfig();
	}

	protected void initConfig() throws Exception {
		NewspaperConfigManager ncm = NewspaperConfigManager.getInstance();
		selector_preTitle = ncm.getSelector(newspaperName, date, NewspaperConfigManager.Selector.preTitle);
		selector_title = ncm.getSelector(newspaperName, date, NewspaperConfigManager.Selector.title);
		selector_subTitle = ncm.getSelector(newspaperName, date, NewspaperConfigManager.Selector.subTitle);
		selector_layout = ncm.getSelector(newspaperName, date, NewspaperConfigManager.Selector.layout);
		selector_author = ncm.getSelector(newspaperName, date, NewspaperConfigManager.Selector.author);
		selector_content = ncm.getSelector(newspaperName, date, NewspaperConfigManager.Selector.content);
		selector_picture = ncm.getSelector(newspaperName, date, NewspaperConfigManager.Selector.picture);
	}

	private String select(Document doc, String selector) {
		String s = null;
		// 如果是复合式selector
		if (selector.contains(":")) {
			String[] sp = selector.split(":");
			String mainSelector = sp[0];
			String condition = sp[1];
			if (condition.toLowerCase().contains("nth-child")) {
				int index = Integer.parseInt(condition.split("\\(")[1].replace(')', ' ').trim()) - 1;
				s = doc.select(mainSelector).get(index).text();
			} else if (condition.toLowerCase().contains("not")) {
				s = doc.select(selector).text();
			} else {
				s = "";
				log.log(Level.INFO, "Unimplemented condition: {0}", condition);
			}
		} else {
			s = doc.select(selector).text();
		}
		s = s.trim().replaceAll(" ", "").replaceAll(" ", "").replaceAll("&nbsp;", "").trim();
		return s;
	}

	public Serializable parse(URL url) throws Exception {
		Document doc = Jsoup.connect(url.toString()).ignoreContentType(true).ignoreHttpErrors(true).timeout(timeoutMillis)
				.userAgent("Mozilla/5.0 (Windows NT 6.1; rv:22.0) Gecko/20100101 Firefox/22.0").get();
		News news = new News();
		news.setUrl(url.toString());
		news.setNewspaper(newspaperName);

		news.setPreTitle(select(doc, selector_preTitle));
		news.setTitle(select(doc, selector_title));
		news.setSubTitle(select(doc, selector_subTitle));
		news.setAuthor(select(doc, selector_author));
		news.setLayout(select(doc, selector_layout).replace(":", "：").replace("●", "："));
		StringBuilder sb = new StringBuilder();
		for (Element e : doc.select(selector_content)) {
			if (!e.getElementsByTag("br").isEmpty()) {
				String raw = e.html();
				// 把br替换为换行符
				HashSet<String> brTypes = new HashSet<String>();
				for (Element bre : e.getElementsByTag("br")) {
					brTypes.add(bre.toString());
				}
				for (String brStr : brTypes) {
					raw = raw.replace(brStr, "[BREnter]");
				}
				Document newDoc = Jsoup.parse(raw);
				String[] lines = newDoc.text().split("\\[BREnter\\]");
				for (int i = 0; i < lines.length; i++) {
					String line = lines[i].trim().replaceAll("^( |　)*", "").replaceAll("( |　)*$", "").replace("　　", "\n").trim() + "\n";
					if (line.length() > 1) {
						sb.append(line);
					}
				}
			} else {
				String line = e.text().trim().replaceAll("^( |　)*", "").replaceAll("( |　)*$", "").replace("　　", "\n") + "\n";
				if (line.length() > 1) {
					sb.append(line);
				}
			}
		}
		news.setContent(sb.toString());
		ArrayList<NewsPicture> picList = new ArrayList<NewsPicture>();
		for (Element e : doc.select(selector_picture)) {
			if (e.getElementsByTag("img").isEmpty()) {
				continue;
			}
			String picRelUrl = e.getElementsByTag("img").attr("src");
			NewsPicture pic = new NewsPicture();
			try {
				URL picUrl = new URL(picRelUrl);
				pic.setUrl(picUrl.toString());
			} catch (MalformedURLException e2) {
				String picAbsUrl;
				if (picRelUrl.startsWith("/")) {
					picAbsUrl = url.getProtocol() + "://" + url.getHost() + picRelUrl;
				} else {
					String[] urlSp = url.toString().split("/");
					String rootUrl = url.toString().replace(urlSp[urlSp.length - 1], "");
					while (picRelUrl.startsWith("../")) {
						urlSp = rootUrl.split("/");
						rootUrl = rootUrl.replace(urlSp[urlSp.length - 1] + "/", "");
						picRelUrl = picRelUrl.substring(3);
					}
					picAbsUrl = rootUrl + picRelUrl;
				}
				try {
					pic.setUrl(new URL(picAbsUrl).toString());
				} catch (MalformedURLException e1) {
					log.log(Level.INFO, "Newspaper: {0}, URL Create Error: {1}", new String[] { newspaperName, e1.getMessage() });
					continue;
				}
			}
			pic.setComment(e.text().trim().replaceAll("^ *", "").replaceAll(" *$", ""));
			if (pic.getComment().equals("")) {
				pic.setComment(e.getElementsByAttribute("data-title").attr("data-title"));
			}
			if (pic.getComment().equals("")) {
				pic.setComment(e.getElementsByAttribute("title").attr("title"));
			}
			log.log(Level.INFO, "Newspaper: {0}, Picture URL: {1}, Comment: {2}", new String[] { newspaperName, pic.getUrl(), pic.getComment() });
			picList.add(pic);
		}
		news.setPictures(picList.toArray(new NewsPicture[picList.size()]));
		return news;
	}

	public static void main(String[] args) throws Exception {
		DefaultParser p;
		Serializable news;
		try {
			p = new DefaultParser("云南日报", new Date());
			news = p.parse(new URL("http://yndaily.yunnan.cn/html/2015-05/14/content_964520.htm"));
			System.out.println(news);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
}