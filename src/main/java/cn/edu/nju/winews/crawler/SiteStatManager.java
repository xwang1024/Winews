package cn.edu.nju.winews.crawler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class SiteStatManager extends HashMap<String, StatInfo> {
	private static final long serialVersionUID = 3667998936942699640L;

	private static SiteStatManager instance = new SiteStatManager();

	private SiteStatManager() {
		super();
	}

	public static SiteStatManager getInstance() {
		return instance;
	}

	public void print() {
		for (String key : keySet()) {
			System.out.print("Site stat: ");
			System.out.print(key);
			System.out.print(": ");
			System.out.println(get(key).getMillis());
		}
	}

	public String[] getSpeedKeyList() {
		ArrayList<StatInfo> l = new ArrayList<StatInfo>(values());
		Collections.sort(l);
		int len = l.size();
		String[] arr = new String[l.size()];
		for (int i = 0; i < len; i++) {
			arr[i] = l.get(len - i - 1).getName();
		}
		return arr;
	}
}
