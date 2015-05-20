package cn.edu.nju.winews.crawler.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

class ConfigNode {
	private String name;
	private String value;
	private String start;
	private String end;
	private Map<String, ConfigNode> children = new HashMap<String, ConfigNode>();
	private List<ConfigNode> siblings = new ArrayList<ConfigNode>();
	private ConfigNode father;

	String getName() {
		return name;
	}

	ConfigNode setName(String name) {
		this.name = name;
		return this;
	}

	String getValue() {
		return value;
	}

	ConfigNode setValue(String value) {
		this.value = value;
		return this;
	}

	String getStart() {
		return start;
	}

	ConfigNode setStart(String start) {
		this.start = start;
		return this;
	}

	String getEnd() {
		return end;
	}

	ConfigNode setEnd(String end) {
		this.end = end;
		return this;
	}

	List<ConfigNode> getSiblings() {
		return siblings;
	}

	ConfigNode setSiblings(List<ConfigNode> siblings) {
		this.siblings = siblings;
		return this;
	}

	ConfigNode getFather() {
		return father;
	}

	ConfigNode setFather(ConfigNode father) {
		this.father = father;
		return this;
	}

	ConfigNode getChild(String name) {
		return children.get(name);
	}

	ConfigNode addChild(ConfigNode child) {
		if (child.getName() == null) {
			throw new NullPointerException("Attr name is null");
		}
		children.put(child.getName(), child);
		return this;
	}

	String[] getChildrenNameList() {
		Set<String> set = children.keySet();
		return set.toArray(new String[set.size()]);
	}
}
