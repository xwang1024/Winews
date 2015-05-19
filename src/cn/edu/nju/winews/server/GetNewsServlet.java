package cn.edu.nju.winews.server;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
import cn.edu.nju.winews.dao.NewsDao;
import cn.edu.nju.winews.dao.impl.NewsDaoImpl;
import cn.edu.nju.winews.model.News;
import cn.edu.nju.winews.util.Base64Util;

public class GetNewsServlet extends HttpServlet {

	private static final long serialVersionUID = 3855953739440921274L;
	private Map<String, Object> dataMap;
	private NewsDao nd;

	public GetNewsServlet() throws Exception {
		dataMap = new HashMap<String, Object>();
		nd = new NewsDaoImpl();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		dataMap.clear();
		String id = req.getParameter("id");
		try {
			if (id == null) {
				dataMap.put("status", "ERROR");
			} else {
				String url = URLDecoder.decode(new String(Base64Util.decode(id.getBytes())), "utf-8");
				System.out.println(url);
				News news;
				news = nd.get(url);
				if (news != null) {
					dataMap.put("news", news);
				}
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
