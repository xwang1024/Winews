package cn.edu.nju.winews.crawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class SourceHelper {
	private static final int timeoutMillis = 5000;

	public static Document get(String url, String charset) throws IOException {
		URL u = new URL(url);
		HttpURLConnection urlConnection = (HttpURLConnection) u.openConnection();
		urlConnection.setConnectTimeout(timeoutMillis);
		urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; rv:22.0) Gecko/20100101 Firefox/22.0");
		urlConnection.connect();
		BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), charset));
		String line;
		StringBuilder sb = new StringBuilder();
		while ((line = reader.readLine()) != null) {
			sb.append(line);
		}
		Document doc = Jsoup.parse(sb.toString());
		doc.setBaseUri(url);
		return doc;
	}

	public static Document get(String url) throws IOException {
		return Jsoup.connect(url.toString()).ignoreContentType(true).ignoreHttpErrors(true).timeout(timeoutMillis)
				.userAgent("Mozilla/5.0 (Windows NT 6.1; rv:22.0) Gecko/20100101 Firefox/22.0").get();
	}
}
