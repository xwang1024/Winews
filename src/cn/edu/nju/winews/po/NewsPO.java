package cn.edu.nju.winews.po;

import java.io.Serializable;

public class NewsPO extends BriefNewsPO implements Serializable {
	private static final long serialVersionUID = 8366725589441064039L;

	private String content;
	private String[] picture;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String[] getPicture() {
		return picture;
	}

	public void setPicture(String[] picture) {
		this.picture = picture;
	}

}
