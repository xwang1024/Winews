package cn.edu.nju.winews.crawler.config;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.vfs2.FileChangeEvent;
import org.apache.commons.vfs2.FileListener;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.impl.DefaultFileMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ConfigManager {
	public static final String CONFIG_NAME = "etc/winews.xml";

	private static final Logger logger = LoggerFactory.getLogger(ConfigManager.class);

	private static ConfigManager instance;

	private Config config = new Config();

	private ConfigManager() {
		// TODO Auto-generated constructor stub
	}

	public static ConfigManager getInstance() {
		if (instance == null) {
			instance = new ConfigManager();
		}
		return instance;
	}

	public static void setInstance(ConfigManager instance) {
		ConfigManager.instance = instance;
	}

	public void init() throws ParserConfigurationException, SAXException, IOException {
		logger.info("Reading Configure: winews.xml");

		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();
		parser.parse(new File(CONFIG_NAME), new WinewsConfigHandler());
	}

	public void startWatchThread() throws FileSystemException {
		FileSystemManager fsm = VFS.getManager();
		FileObject fileObj = fsm.resolveFile(new File("./"), CONFIG_NAME);
		DefaultFileMonitor dfm = new DefaultFileMonitor(new FileListener() {
			@Override
			public void fileDeleted(FileChangeEvent event) throws Exception {
				logger.warn("winews.xml has been deleted!");
			}

			@Override
			public void fileCreated(FileChangeEvent event) throws Exception {
				init();
			}

			@Override
			public void fileChanged(FileChangeEvent event) throws Exception {
				init();
			}
		});
		dfm.setRecursive(true);
		dfm.addFile(fileObj);
		dfm.setDelay(1000);
		dfm.start();
	}

	public Config getConfig() {
		return config;
	}

	public static void main(String[] args) throws InterruptedException, ParserConfigurationException, SAXException, IOException {
		ConfigManager cm = new ConfigManager();
		cm.init();
		cm.getConfig().print();
		System.out.println(cm.getConfig().get("winews.dataSource.newspaper.云南日报.selector.layout@2050-01-01"));
	}

	private class WinewsConfigHandler extends DefaultHandler {
		private ConfigNode current;

		@Override
		public void startDocument() throws SAXException {
			ConfigNode root = new ConfigNode();
			config = new Config();
			config.setRoot(root);
			current = config.getRoot();
		}

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			if (!qName.equals("conf")) {
				return;
			}
			String name = attributes.getValue("name");
			String value = attributes.getValue("value");
			String start = attributes.getValue("start");
			String end = attributes.getValue("end");
			if (name == null) {
				logger.debug("Conf name is null");
				return;
			}
			if (start == null) {
				start = current.getStart();
			}
			if (end == null) {
				end = current.getEnd();
			}
			ConfigNode node = new ConfigNode().setName(name).setValue(value).setStart(start).setEnd(end).setFather(current);
			ConfigNode child = null;
			if ((child = current.getChild(name)) != null) {
				child.getSiblings().add(node);
			} else {
				current.addChild(node);
			}
			current = node;

		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			current = current.getFather();
		}

	}
}
