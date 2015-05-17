package cn.edu.nju.winews.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

public class News implements Serializable {
	private static final long serialVersionUID = 8366725589441064039L;
	private String newspaper;
	private String domain;
	private String province;
	private String url;
	private String preTitle;
	private String title;
	private String subTitle;
	private String layout;
	private Date date;
	private String author;
	private String content;
	private NewsPicture[] pictures;

	public String getNewspaper() {
		return newspaper;
	}

	public void setNewspaper(String newspaper) {
		this.newspaper = newspaper;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getPreTitle() {
		return preTitle;
	}

	public void setPreTitle(String preTitle) {
		this.preTitle = preTitle;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubTitle() {
		return subTitle;
	}

	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}

	public String getLayout() {
		return layout;
	}

	public void setLayout(String layout) {
		this.layout = layout;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public NewsPicture[] getPictures() {
		return pictures;
	}

	public void setPictures(NewsPicture[] pictures) {
		this.pictures = pictures;
	}

	@Override
	public String toString() {
		return "NewsPO [content=" + content + ", picture=" + Arrays.toString(pictures) + ", newspaper=" + newspaper + ", domain=" + domain
				+ ", province=" + province + ", url=" + url + ", preTitle=" + preTitle + ", title=" + title + ", subTitle=" + subTitle + ", layout="
				+ layout + ", date=" + date + ", author=" + author + "]";
	}

}
