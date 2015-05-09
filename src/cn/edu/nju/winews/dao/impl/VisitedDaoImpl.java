package cn.edu.nju.winews.dao.impl;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import cn.edu.nju.winews.dao.VisitedDao;
import cn.edu.nju.winews.util.MD5Util;
import cn.nju.edu.winews.crawler.handler.exception.ConfigException;

public class VisitedDaoImpl implements VisitedDao {
	private static final Logger log = Logger.getLogger(VisitedDaoImpl.class.getName());

	private static final String CONFIG_PATH = "mysql.properties";
	private String url;
	private String username;
	private String password;
	private Connection conn;

	public VisitedDaoImpl() throws Exception {
		initialize();
	}

	private void initialize() throws Exception {
		initConfig();
		initConnection();
	}

	private void initConfig() throws IOException {
		Properties prop = new Properties();
		prop.load(this.getClass().getResourceAsStream(CONFIG_PATH));
		url = prop.getProperty("url", "");
		username = prop.getProperty("username", "");
		password = prop.getProperty("password", "");
		log.log(Level.INFO, "初始化Mysql连接，url={0}，username={1}，password=******", new String[] { url, username });
		if (url.equals("") || username.equals("") || password.equals("")) {
			throw new ConfigException("配置文件不完整！");
		}
	}

	private void initConnection() throws ClassNotFoundException, SQLException {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(url, username, password);
		} catch (ClassNotFoundException e) {
			log.log(Level.SEVERE, e.getMessage());
			throw e;
		}

	}

	@Override
	public boolean isVisited(String url) throws Exception {
		log.log(Level.FINE, "crawler_visited表包含{0}", url);
		PreparedStatement ps = conn.prepareStatement("select 1 from crawler_visited where url_code=? limit 1");
		ps.setString(1, MD5Util.gen32bitMD5(url));
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			return true;
		}
		ps.close();
		return false;
	}

	@Override
	public void add(String url, String newspaper) throws Exception {
		log.log(Level.FINE, "增加记录到crawler_visited: url={0},newspaper={1}", new String[] { url, newspaper });
		PreparedStatement ps = conn.prepareStatement("insert into crawler_visited (url_code,url,newspaper,timestamp) values (?,?,?,?)");
		ps.setString(1, MD5Util.gen32bitMD5(url));
		ps.setString(2, url);
		ps.setString(3, newspaper);
		ps.setTimestamp(4, new Timestamp(new Date().getTime()));
		ps.execute();
		ps.close();
	}

	@Override
	public void clear() throws Exception {
		log.log(Level.FINE, "清空表crawler_visited");
		PreparedStatement ps = conn.prepareStatement("truncate table crawler_visited;");
		ps.execute();
		ps.close();
	}

}
