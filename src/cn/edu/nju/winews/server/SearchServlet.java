package cn.edu.nju.winews.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
import cn.edu.nju.winews.dao.KeywordDao;
import cn.edu.nju.winews.dao.NewsDao;
import cn.edu.nju.winews.dao.impl.KeywordDaoImpl;
import cn.edu.nju.winews.dao.impl.NewsDaoImpl;
import cn.edu.nju.winews.dto.BriefNewsDTO;

public class SearchServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7972723407870315568L;

	private Map<String, Object> dataMap;

	private KeywordDao kwd;
	private NewsDao nd;

	public SearchServlet() throws Exception {
		dataMap = new HashMap<String, Object>();
		kwd = new KeywordDaoImpl();
		nd = new NewsDaoImpl();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		dataMap.clear();
		String keywords = req.getParameter("keywords");
		try {
			if (keywords == null) {
				dataMap.put("status", "ERROR");
			} else {
				String[] keys = keywords.split(",");
				String[] urls = kwd.search(keys);
				List<BriefNewsDTO> results = new ArrayList<BriefNewsDTO>();
				for (int i = 0; i < urls.length; i++) {
					BriefNewsDTO news = new BriefNewsDTO(nd.get(urls[i]));
					results.add(news);
				}
				dataMap.put("keywords", keys);
				dataMap.put("results", results);
			}
		} catch (Exception e) {
			e.printStackTrace();
			dataMap.put("status", "ERROR");
		}
		resp.setContentType("text/json");
		resp.setCharacterEncoding("utf-8");
		JSONObject json = JSONObject.fromObject(dataMap);
		System.out.println(json.toString());
		resp.getWriter().println(json.toString());
		resp.getWriter().close();
	}
}
