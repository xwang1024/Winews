package cn.edu.nju.winews.model;

import java.io.Serializable;

public class NewsPicture implements Serializable {

	private static final long serialVersionUID = -8144739215145426267L;

	private String url;
	private String comment;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

}
