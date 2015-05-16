package cn.edu.nju.winews.server;

public class ServerStarter {
	
	public static void main(String[] args) {
		WinewsServer server = new WinewsServer("jetty/etc/jetty.xml", "/winews");
		server.startServer();

	}
}
