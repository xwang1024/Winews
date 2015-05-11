package cn.edu.nju.winews.config;

import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class NewspaperConfigManager {
	private static final String PATH = "conf/newspaper.xml";
	private static final String URL_CONFIG_PREFIX = "url_";
	private static final String SELECTOR_CONFIG_PREFIX = "selector_";
	
	private static NewspaperConfigManager instance;

	private String[] newspaperNameArray;
	private Map<String, NewsPaperConfig> confMap;

	private XPath xpath;
	private Document doc;

	private NewspaperConfigManager() throws Exception {
		reload();
	}
	
	public static NewspaperConfigManager getInstance() throws Exception {
		if(instance == null) {
			instance = new NewspaperConfigManager();
		}
		return instance;
	}

	public void reload() throws Exception {
		XPathFactory xpFactory = XPathFactory.newInstance();
		xpath = xpFactory.newXPath();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		doc = builder.parse(PATH);

		// Read newspaper list
		NodeList list = (NodeList) xpath.evaluate("/newspaperList/newspaper/name", doc, XPathConstants.NODESET);
		int len = list.getLength();
		newspaperNameArray = new String[len];
		confMap = new HashMap<String, NewspaperConfigManager.NewsPaperConfig>();
		for (int i = 0; i < len; i++) {
			newspaperNameArray[i] = list.item(i).getTextContent().trim();

			// Read config detail
			NewsPaperConfig detail = new NewsPaperConfig(newspaperNameArray[i]);
			NodeList nodelist = (NodeList) xpath.evaluate("/newspaperList/newspaper[name=\"" + detail.getName() + "\"]/*", doc, XPathConstants.NODESET);
			for(int j = 0; j < nodelist.getLength(); j++) {
				fillConfig(detail, nodelist.item(j), "");
			}
			confMap.put(detail.getName(), detail);
		}
	}

	private void fillConfig(NewsPaperConfig detail, Node node, String baseName) {
		if(node.getNodeName().startsWith("#") || node.getNodeName().startsWith("@") ) {
			return;
		}
		String newBase = baseName;
		if (newBase.length() != 0) {
			newBase = newBase + "_" + node.getNodeName().trim();
		} else {
			newBase = node.getNodeName().trim();
		}

		NodeList nodelist = node.getChildNodes();
		if (nodelist.getLength() <= 1) {
			detail.setConfig(newBase, node.getTextContent().trim());
		} else {
			for (int i = 0; i < nodelist.getLength(); i++) {
				fillConfig(detail, nodelist.item(i), newBase);
			}
		}
	}

	public String[] getNewspaperList() {
		return newspaperNameArray;
	}
	
	public String getNewspaperName(String domain) {
		
		return null;
	}

	public String getCommonConfig(String paperName, CommonConfig configName) {
		NewsPaperConfig conf = confMap.get(paperName);
		if (conf == null)
			return null;
		else
			return conf.getConfig(configName.toString());
	}

	public String getUrlConfig(String paperName, UrlConfig configName) {
		NewsPaperConfig conf = confMap.get(paperName);
		if (conf == null)
			return null;
		else
			return conf.getConfig(URL_CONFIG_PREFIX + configName.toString());
	}

	public String getSelector(String paperName, Selector selectorName) {
		NewsPaperConfig conf = confMap.get(paperName);
		if (conf == null)
			return null;
		else
			return conf.getConfig(SELECTOR_CONFIG_PREFIX + selectorName.toString());
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < newspaperNameArray.length; i++) {
			sb.append(confMap.get(newspaperNameArray[i])).append("\r\n");
		}
		return sb.toString();
	}

	public String toString(String newspaper) {
		StringBuilder sb = new StringBuilder();
		sb.append(confMap.get(newspaper)).append("\r\n");
		return sb.toString();
	}

	public static enum CommonConfig {
		name, domain, province, handler, parser
	}

	public static enum UrlConfig {
		pattern_node, pattern_content, pattern_date, format_node, format_date
	}

	public static enum Selector {
		preTitle, title, subTitle, author, layout, content, picture
	}

	private static class NewsPaperConfig {
		private String name;
		private Map<String, String> map;

		public NewsPaperConfig(String name) {
			this.name = name;
			map = new HashMap<String, String>();
		}

		public String getName() {
			return name;
		}

		public String getConfig(String key) {
			return map.get(key);
		}

		public void setConfig(String key, String value) {
			map.put(key.trim(), value.trim());
		}

		public String toString() {
			StringBuilder sb = new StringBuilder();
			for (String key : map.keySet()) {
				sb.append(name).append(":").append(key).append("=").append(map.get(key)).append("\r\n");
			}
			return sb.toString();
		}
	}
}
