package cn.edu.nju.winews.model;

import java.io.Serializable;
import java.util.Date;

public class VisitedRecord implements Serializable {
	private static final long serialVersionUID = -8220451993914186089L;

	private String url;
	private String newspaper;
	private Date timestamp;

	public VisitedRecord() {
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getNewspaper() {
		return newspaper;
	}

	public void setNewspaper(String newspaper) {
		this.newspaper = newspaper;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		return "VisitedRecord [url=" + url + ", newspaper=" + newspaper + ", timestamp=" + timestamp + "]";
	}

}
