package cn.edu.nju.winews.server.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.edu.nju.winews.dao.KeywordDao;
import cn.edu.nju.winews.dao.NewsDao;
import cn.edu.nju.winews.dao.impl.KeywordDaoImpl;
import cn.edu.nju.winews.dao.impl.NewsDaoImpl;
import cn.edu.nju.winews.dto.BriefNewsDTO;

import com.opensymphony.xwork2.ActionSupport;

public class SearchAction extends ActionSupport {
	private String keywords;
	private Map<String, Object> dataMap;
	
	private KeywordDao kwd;
	private NewsDao nd;

	public SearchAction() throws Exception {
		dataMap = new HashMap<String, Object>();
		kwd = new KeywordDaoImpl();
		nd = new NewsDaoImpl();
	}

	public String search() throws Exception {
		if(keywords == null) {
			dataMap.put("status", "ERROR");
			return SUCCESS;
		}
		String[] keys = keywords.split(",");
		String[] urls = kwd.search(keys);
		List<BriefNewsDTO> results = new ArrayList<BriefNewsDTO>();
		for(int i = 0; i < urls.length; i++) {
			BriefNewsDTO news = new BriefNewsDTO(nd.get(urls[i]));
			results.add(news);
		}
		dataMap.put("keywords", keys);
		dataMap.put("results", results);
		return SUCCESS;
	}

	public Map<String, Object> getDataMap() {
		return dataMap;
	}

	public void setDataMap(Map<String, Object> dataMap) {
		this.dataMap = dataMap;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
	
	
}
