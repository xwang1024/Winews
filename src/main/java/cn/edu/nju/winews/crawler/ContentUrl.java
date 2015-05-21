package cn.edu.nju.winews.crawler;

import java.net.URL;
import java.util.Date;

import org.jsoup.nodes.Document;

public class ContentUrl {
	private URL url;
	private String sourceName;
	private Document fatherDoc;
	private Date date;
	private String confDate;

	public ContentUrl() {
		// TODO Auto-generated constructor stub
	}

	public ContentUrl(URL url, String sourceName, Document fatherDoc, Date date, String confDate) {
		super();
		this.url = url;
		this.sourceName = sourceName;
		this.fatherDoc = fatherDoc;
		this.date = date;
		this.confDate = confDate;
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	public String getSourceName() {
		return sourceName;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	public Document getFatherDoc() {
		return fatherDoc;
	}

	public void setFatherDoc(Document fatherDoc) {
		this.fatherDoc = fatherDoc;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getConfDate() {
		return confDate;
	}

	public void setConfDate(String confDate) {
		this.confDate = confDate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ContentUrl other = (ContentUrl) obj;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		return true;
	}

}
