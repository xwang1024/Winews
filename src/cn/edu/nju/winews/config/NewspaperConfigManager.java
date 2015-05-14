package cn.edu.nju.winews.config;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class NewspaperConfigManager {
	private static final Logger logger = Logger.getLogger(NewspaperConfigManager.class.getName());
	private static final String DEFAULT_NULL = "NULL";
	private static final String PATH = "conf/newspaper.xml";
	private static final String URL_CONFIG_PREFIX = "url_";
	private static final String SELECTOR_CONFIG_PREFIX = "selector_";
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

	private static NewspaperConfigManager instance;

	private String[] newspaperNameArray;
	private Map<String, NewsPaperConfig> paper_conf_map;

	private XPath xpath;
	private Document doc;

	private NewspaperConfigManager() throws Exception {
		reload();
	}

	public static NewspaperConfigManager getInstance() throws Exception {
		if (instance == null) {
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
		paper_conf_map = new HashMap<String, NewspaperConfigManager.NewsPaperConfig>();
		for (int i = 0; i < len; i++) {
			newspaperNameArray[i] = list.item(i).getTextContent().trim();

			// Read config detail
			NewsPaperConfig detail = new NewsPaperConfig(newspaperNameArray[i]);
			NodeList nodelist = (NodeList) xpath.evaluate("/newspaperList/newspaper[name=\"" + detail.getName() + "\"]/*", doc,
					XPathConstants.NODESET);
			for (int j = 0; j < nodelist.getLength(); j++) {
				fillConfig(detail, nodelist.item(j), "", null, null);
			}
			paper_conf_map.put(detail.getName(), detail);
		}
	}

	private void fillConfig(NewsPaperConfig detail, Node node, String baseName, String start, String end) throws ParseException {
		if (node.getNodeName().startsWith("#") || node.getNodeName().startsWith("@")) {
			return;
		}
		String newBase = baseName;
		if (node.hasAttributes()) {
			NamedNodeMap attrs = node.getAttributes();
			for (int i = 0; i < attrs.getLength(); i++) {
				if (attrs.item(i).getNodeName().equals("start")) {
					start = attrs.item(i).getNodeValue();
				} else if (attrs.item(i).getNodeName().equals("end")) {
					end = attrs.item(i).getNodeValue();
				} else {
					logger.log(Level.WARNING, "Unknown attribute");
				}
			}
		}
		if (newBase.length() != 0) {
			newBase = newBase + "_" + node.getNodeName().trim();
		} else {
			newBase = node.getNodeName().trim();
		}

		NodeList nodelist = node.getChildNodes();
		if (nodelist.getLength() <= 1) {
			String value = node.getTextContent().trim();
			if (value.length() == 0) {
				value = DEFAULT_NULL;
			}
			detail.setConfig(newBase, value, start, end);
		} else {
			for (int i = 0; i < nodelist.getLength(); i++) {
				fillConfig(detail, nodelist.item(i), newBase, start, end);
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
		NewsPaperConfig conf = paper_conf_map.get(paperName);
		if (conf == null)
			return null;
		else {
			String result = conf.getConfig(configName.toString()).getValue();
			if (result == null) {
				result = DEFAULT_NULL;
			}
			return result;
		}
	}

	public String getUrlConfig(String paperName, Date date, UrlConfig configName) {
		NewsPaperConfig conf = paper_conf_map.get(paperName);
		if (conf == null)
			return null;
		else {
			String result = conf.getConfig(URL_CONFIG_PREFIX + configName.toString()).getValue(date);
			if (result == null) {
				result = DEFAULT_NULL;
			}
			return result;
		}
	}

	public String getSelector(String paperName, Date date, Selector selectorName) {
		NewsPaperConfig conf = paper_conf_map.get(paperName);
		if (conf == null)
			return null;
		else {
			String result = conf.getConfig(SELECTOR_CONFIG_PREFIX + selectorName.toString()).getValue(date);
			if (result == null) {
				result = DEFAULT_NULL;
			}
			return result;
		}
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < newspaperNameArray.length; i++) {
			sb.append(paper_conf_map.get(newspaperNameArray[i])).append("\r\n");
		}
		return sb.toString();
	}

	public String toString(String newspaper) {
		StringBuilder sb = new StringBuilder();
		sb.append(paper_conf_map.get(newspaper)).append("\r\n");
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
		private Map<String, Config> map;

		public NewsPaperConfig(String name) {
			this.name = name;
			map = new HashMap<String, Config>();
		}

		public String getName() {
			return name;
		}

		public Config getConfig(String key) {
			return map.get(key);
		}

		public void setConfig(String key, String value, String startDate, String endDate) throws ParseException {
			Config conf;
			if (map.containsKey(key)) {
				conf = map.get(key);
			} else {
				if (startDate == null && endDate == null) {
					conf = new Config(1, false);
				} else {
					conf = new Config(5, true);
				}
			}
			conf.addValue(value, startDate, endDate);
			map.put(key.trim(), conf);
		}

		public String toString() {
			StringBuilder sb = new StringBuilder();
			for (String key : map.keySet()) {
				sb.append(name).append(":").append(key).append("=").append(map.get(key)).append("\r\n");
			}
			return sb.toString();
		}
	}

	private static class Config {
		private int index = 0;
		private int length = 0;
		private String[] value;
		private Date[] startTime;
		private Date[] endTime;
		private boolean isTimeLimited = false;

		public Config(int len, boolean isTimeLimited) {
			length = len;
			value = new String[len];
			startTime = new Date[len];
			endTime = new Date[len];
			this.isTimeLimited = isTimeLimited;
		}

		public String getValue() {
			return value[0];
		}

		public String getValue(Date date) {
			for (int i = 0; i < index; i++) {
				if(startTime[i] == null && endTime[i] == null) {
					return getValue();
				}
				if(endTime[i] != null) {
					if (date.compareTo(startTime[i]) >= 0 && date.compareTo(endTime[i]) <= 0) {
						return value[i];
					}
				} else {
					if (date.compareTo(startTime[i]) >= 0) {
						return value[i];
					}
				}
			}
			return null;
		}

		public void addValue(String value, String startDate, String endDate) throws ParseException {
			if (isTimeLimited) {
				Date stdate = DATE_FORMAT.parse(startDate);
				Date eddate;
				if (endDate == null) {
					eddate = new Date();
				} else {
					eddate = DATE_FORMAT.parse(endDate);
				}
				if (eddate.compareTo(stdate) < 0) {
					throw new DateTimeException("Start date cannot be after End date");
				}
				for (int i = 0; i < index; i++) {
					Date stt = startTime[i];
					Date edt = endTime[i];
					if (edt == null) {
						edt = new Date();
					}

					if ((stdate.compareTo(stt) >= 0 && stdate.compareTo(edt) <= 0) || (eddate.compareTo(stt) >= 0 && eddate.compareTo(edt) <= 0)) {
						throw new DateTimeException("Date ranges are overlap");
					}
				}

				this.value[index] = value;
				this.startTime[index] = stdate;
				if (endDate == null) {
					this.endTime[index] = null;
				} else {
					this.endTime[index] = eddate;
				}
				index++;
			} else {
				this.value[0] = value;
				index++;
			}
		}
	}
	
	// public static void main(String[] args) throws Exception {
	// SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	// System.out.println(NewspaperConfigManager.getInstance().getSelector("天津日报",new
	// Date(), NewspaperConfigManager.Selector.author));
	//
	// }
}
