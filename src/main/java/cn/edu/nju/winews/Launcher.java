package cn.edu.nju.winews;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import cn.edu.nju.winews.crawler.CrawlerMain;

/**
 * Hello world!
 *
 */
public class Launcher {
	private static Logger logger = LoggerFactory.getLogger(Launcher.class);
	
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
		CrawlerMain crawler = new CrawlerMain();
		crawler.start();
	}
}
