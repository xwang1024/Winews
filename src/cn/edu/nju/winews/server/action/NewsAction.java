package cn.edu.nju.winews.server.action;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import cn.edu.nju.winews.dao.NewsDao;
import cn.edu.nju.winews.dao.impl.NewsDaoImpl;
import cn.edu.nju.winews.model.News;
import cn.edu.nju.winews.util.Base64Util;

import com.opensymphony.xwork2.ActionSupport;

public class NewsAction extends ActionSupport {

	private String id;

	private Map<String, Object> dataMap;
	private NewsDao nd;

	public NewsAction() throws Exception {
		dataMap = new HashMap<String, Object>();
		nd = new NewsDaoImpl();
	}

	public String get() throws Exception {
		if (id == null) {
			dataMap.put("status", "ERROR");
			return SUCCESS;
		}
		String url = URLDecoder.decode(new String(Base64Util.decode(id.getBytes())), "utf-8");
		System.out.println(url);
		News news = nd.get(url);
		if (news != null) {
			dataMap.put("news", news);
		}
		return SUCCESS;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Map<String, Object> getDataMap() {
		return dataMap;
	}

	public void setDataMap(Map<String, Object> dataMap) {
		this.dataMap = dataMap;
	}

}
