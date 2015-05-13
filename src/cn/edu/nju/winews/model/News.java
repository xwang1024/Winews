package cn.edu.nju.winews.model;

import java.io.Serializable;
import java.util.Arrays;

public class News extends BriefNews implements Serializable {
	private static final long serialVersionUID = 8366725589441064039L;

	private String content;
	private NewsPicture[] picture;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public NewsPicture[] getPicture() {
		return picture;
	}

	public void setPicture(NewsPicture[] picture) {
		this.picture = picture;
	}

	@Override
	public String toString() {
		return "NewsPO [content=" + content + ", picture=" + Arrays.toString(picture) + ", newspaper=" + newspaper + ", domain=" + domain
				+ ", province=" + province + ", url=" + url + ", preTitle=" + preTitle + ", title=" + title + ", subTitle=" + subTitle + ", layout="
				+ layout + ", date=" + date + ", author=" + author + "]";
	}

}
