package cn.edu.nju.winews.model;

public class UrlRecord {
	private String url;
	private String sourceName;
	private long timestamp;

	public UrlRecord() {
		// TODO Auto-generated constructor stub
	}

	public UrlRecord(String url, String sourceName, long timestamp) {
		super();
		this.url = url;
		this.sourceName = sourceName;
		this.timestamp = timestamp;
	}

	public String getUrl() {
		return url;
	}

	public UrlRecord setUrl(String url) {
		this.url = url;
		return this;
	}

	public String getSourceName() {
		return sourceName;
	}

	public UrlRecord setSourceName(String sourceName) {
		this.sourceName = sourceName;
		return this;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public UrlRecord setTimestamp(long timestamp) {
		this.timestamp = timestamp;
		return this;
	}
}
