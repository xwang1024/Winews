package cn.edu.nju.winews.dto;

import java.io.Serializable;
import java.net.URLEncoder;
import java.util.Date;

import cn.edu.nju.winews.model.News;
import cn.edu.nju.winews.util.Base64Util;

public class BriefNewsDTO implements Serializable {
	private static final long serialVersionUID = 745781180569643330L;
	private String id;
	private String title;
	private Date date;

	public BriefNewsDTO() {
	}

	public BriefNewsDTO(News news) throws Exception {
		this.id = URLEncoder.encode(new String(Base64Util.encode(news.getUrl().getBytes())),"utf-8");
		this.title = news.getTitle();
		if (title == null || title.length() == 0) {
			this.title = news.getPreTitle();
		}
		if (title == null || title.length() == 0) {
			this.title = news.getSubTitle();
		}
		this.date = news.getDate();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

}
