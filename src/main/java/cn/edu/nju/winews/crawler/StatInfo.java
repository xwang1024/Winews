package cn.edu.nju.winews.crawler;

public class StatInfo implements Comparable<StatInfo> {
	private String name;
	private int millis;

	public StatInfo() {
	}

	public StatInfo(String name, int millis) {
		super();
		this.name = name;
		this.millis = millis;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getMillis() {
		return millis;
	}

	public void setMillis(int millis) {
		this.millis = millis;
	}

	@Override
	public int compareTo(StatInfo o) {
		return millis - o.getMillis();
	}

}
