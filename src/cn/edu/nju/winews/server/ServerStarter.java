package cn.edu.nju.winews.server;

import cn.edu.nju.winews.NewsWatcherManager;

public class ServerStarter {

	public static void main(String[] args) throws Exception {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					NewsWatcherManager manager = new NewsWatcherManager();
					manager.start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();

		WinewsServer server = new WinewsServer("jetty/etc/jetty.xml", "/winews");
		server.startServer();
	}
}
