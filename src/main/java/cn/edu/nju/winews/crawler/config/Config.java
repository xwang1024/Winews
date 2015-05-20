package cn.edu.nju.winews.crawler.config;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Config {
	private static final Logger logger = LoggerFactory.getLogger(ConfigManager.class);

	private static final String NODE_SP = "\\.";
	private static final String DATE_SP = "@";
	private static final Date MIN_DATE = new GregorianCalendar(1900, 0, 1).getTime();
	private static final Date MAX_DATE = new GregorianCalendar(2100, 0, 1).getTime();
	private static final SimpleDateFormat DEFAULT_SDF = new SimpleDateFormat("yyyy-MM-dd");

	private ConfigNode root;

	private ConfigNode searchNode(final String configPath) {
		String path = new String(configPath);
		Date date = null;
		String[] sp = null;
		if (path.contains(DATE_SP)) {
			sp = path.split(DATE_SP);
			path = sp[0];
			String dateStr = sp[1];
			try {
				date = DEFAULT_SDF.parse(dateStr);
			} catch (ParseException e) {
				logger.warn("Query date format does not match yyyy-MM-dd: {}", dateStr);
				e.printStackTrace();
			}
		}
		sp = path.split(NODE_SP);
		ConfigNode searchNode = root;
		for (int i = 0; i < sp.length; i++) {
			String name = sp[i];

			searchNode = searchNode.getChild(name);
			if (searchNode == null) {
				return null;
			}

			if (date != null) {
				String dateStr = null;
				try {
					Set<ConfigNode> nodeSet = new HashSet<ConfigNode>(searchNode.getSiblings());
					nodeSet.add(searchNode);
					Date start;
					Date end;
					for (ConfigNode cn : nodeSet) {
						if ((dateStr = cn.getStart()) == null) {
							start = MIN_DATE;
						} else {
							start = DEFAULT_SDF.parse(dateStr);
						}
						if ((dateStr = cn.getEnd()) == null) {
							end = MAX_DATE;
						} else {
							end = DEFAULT_SDF.parse(dateStr);
						}
						if (!(date.before(start) || date.after(end))) {
							searchNode = cn;
							break;
						} else {
							searchNode = null;
						}
					}
					if (searchNode == null) {
						return null;
					}
				} catch (ParseException e) {
					logger.warn("Config date format does not match yyyy-MM-dd: {}", dateStr);
				}

			}
		}
		return searchNode;
	}

	public String get(final String configPath) {
		ConfigNode searchNode = searchNode(configPath);
		if (searchNode == null) {
			return null;
		}
		return searchNode.getValue();
	}

	public String[] getNameList(final String configPath) {
		ConfigNode searchNode = searchNode(configPath);
		return searchNode.getChildrenNameList();
	}

	ConfigNode getRoot() {
		return root;
	}

	void setRoot(ConfigNode root) {
		this.root = root;
	}

	void print() {
		print(root, "");
	}

	private void print(ConfigNode current, String prepend) {
		System.out.println(prepend + current.getName() + "=" + current.getValue() + " (" + current.getStart() + "~" + current.getEnd());
		String[] nameList = current.getChildrenNameList();
		for (int i = 0; i < nameList.length; i++) {
			print(current.getChild(nameList[i]), prepend + "  ");
		}
		List<ConfigNode> sib = current.getSiblings();
		for (int i = 0; i < sib.size(); i++) {
			print(sib.get(i), prepend + "-");
		}
	}
}
