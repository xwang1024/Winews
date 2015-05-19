package cn.edu.nju.winews.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class WinewsServer extends Server {
	private Server server;

	public WinewsServer() throws Exception {
		server = new Server(8088);

		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/winews");
		server.setHandler(context);

		context.addServlet(new ServletHolder(new SearchServlet()), "/search");
		context.addServlet(new ServletHolder(new GetNewsServlet()), "/get");
	}

	public void startServer() throws Exception {
		server.start();
		server.join();
	}

}
