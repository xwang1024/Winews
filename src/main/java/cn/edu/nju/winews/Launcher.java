package cn.edu.nju.winews;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hello world!
 *
 */
public class Launcher {
	private static Logger logger = LoggerFactory.getLogger(Launcher.class);
	
	public static void main(String[] args) {
		logger.info("Hello World! {} {} {}","占位符1","占位符2","占位符3");
	}
}
