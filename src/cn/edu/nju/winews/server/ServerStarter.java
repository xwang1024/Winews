package cn.edu.nju.winews.server;

import cn.edu.nju.winews.NewsWatcherManager;

public class ServerStarter {
	
	public static void main(String[] args) throws Exception {
		NewsWatcherManager manager = new NewsWatcherManager();
		manager.start();
		
		WinewsServer server = new WinewsServer("jetty/etc/jetty.xml", "/winews");
		server.startServer();

	}
}
