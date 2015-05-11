package cn.edu.nju.winews.po;

import java.util.Arrays;

public class NewsPO extends BriefNewsPO implements IPO {
	private static final long serialVersionUID = 8366725589441064039L;

	private String content;
	private NewsPicturePO[] picture;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public NewsPicturePO[] getPicture() {
		return picture;
	}

	public void setPicture(NewsPicturePO[] picture) {
		this.picture = picture;
	}

	@Override
	public String toString() {
		return "NewsPO [content=" + content + ", picture="
				+ Arrays.toString(picture) + ", newspaper=" + newspaper
				+ ", domain=" + domain + ", province=" + province + ", url="
				+ url + ", preTitle=" + preTitle + ", title=" + title
				+ ", subTitle=" + subTitle + ", layout=" + layout + ", date="
				+ date + ", author=" + author + "]";
	}
	

}
