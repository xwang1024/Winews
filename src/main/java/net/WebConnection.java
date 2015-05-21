package net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.edu.nju.winews.crawler.config.ConfigManager;

public class WebConnection {
	private static final Logger logger = LoggerFactory.getLogger(WebConnection.class);
	private static final ConfigManager cm = ConfigManager.getInstance();

	private URL url;
	private String charset;
	private int ioTimeout = 5000;

	public WebConnection(String url) throws MalformedURLException {
		this.url = new URL(url);
		init();
	}

	public WebConnection(String url, String charset) throws MalformedURLException {
		this.url = new URL(url);
		this.charset = charset;
		init();
	}

	public WebConnection(URL url) {
		this.url = url;
		init();
	}

	public WebConnection(URL url, String charset) {
		this.url = url;
		this.charset = charset;
		init();
	}

	private void init() {
		String conf = cm.getConfig().get("winews", "global", "ioTimeout");
		if (conf == null) {
			try {
				ioTimeout = Integer.parseInt(conf);
			} catch (NumberFormatException e) {
			}
		}
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public int getAccessMillis() {
		Date start = new Date();
		try {
			Jsoup.connect(url.toString()).ignoreContentType(true).ignoreHttpErrors(true)
					.userAgent("Mozilla/5.0 (Windows NT 6.1; rv:22.0) Gecko/20100101 Firefox/22.0").get();
		} catch (IOException e) {
			try {
				Jsoup.connect(url.toString()).ignoreContentType(true).ignoreHttpErrors(true)
						.userAgent("Mozilla/5.0 (Windows NT 6.1; rv:22.0) Gecko/20100101 Firefox/22.0").get();
			} catch (IOException e1) {
				return Integer.MAX_VALUE;
			}
		}
		Date end = new Date();
		return (int) (end.getTime() - start.getTime());
	}

	private Document getWithCharset() throws IOException {
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		urlConnection.setConnectTimeout(ioTimeout);
		urlConnection.setReadTimeout(ioTimeout);
		urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; rv:22.0) Gecko/20100101 Firefox/22.0");
		urlConnection.connect();
		BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), charset));
		String line;
		StringBuilder sb = new StringBuilder();
		while ((line = reader.readLine()) != null) {
			sb.append(line);
		}
		Document doc = Jsoup.parse(sb.toString());
		doc.setBaseUri(url.toString());
		return doc;
	}

	public Document get() throws IOException {
		if (charset == null) {
			return Jsoup.connect(url.toString()).ignoreContentType(true).ignoreHttpErrors(true).timeout(ioTimeout)
					.userAgent("Mozilla/5.0 (Windows NT 6.1; rv:22.0) Gecko/20100101 Firefox/22.0").get();
		} else {
			return getWithCharset();
		}

	}
}
