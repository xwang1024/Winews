package cn.edu.nju.winews.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class WinewsServer {
	private static WinewsServer instance;

	private Server server;
	private ServletContextHandler context;
	private boolean isRunning = false;

	private WinewsServer() {
		server = new Server(8080);
		context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");
		server.setHandler(context);
		context.addServlet(new ServletHolder(new SearchServlet()), "/search");

	}

	public static WinewsServer getInstance() {
		if (instance == null) {
			instance = new WinewsServer();
		}
		return instance;
	}

	public void startServer() throws Exception {
		if (!isRunning) {
			server.start();
			server.join();
			isRunning = true;
		}
	}

	public void stopServer() throws Exception {
		if (isRunning) {
			server.stop();
			isRunning = false;
		}
	}
}
