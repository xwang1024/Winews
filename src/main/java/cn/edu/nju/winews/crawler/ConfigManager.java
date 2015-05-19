package cn.edu.nju.winews.crawler;

import java.io.File;

import org.apache.commons.vfs2.FileChangeEvent;
import org.apache.commons.vfs2.FileListener;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.impl.DefaultFileMonitor;

public class ConfigManager {
	private static ConfigManager instance;
	public static final String DEFAULT_CONFIG_PATH = "winews.xml";
	private String configPath = DEFAULT_CONFIG_PATH;

	private ConfigManager() {
		// TODO Auto-generated constructor stub
	}

	public static ConfigManager getInstance() {
		if (instance == null) {
			instance = new ConfigManager();
		}
		return instance;
	}

	public static void setInstance(ConfigManager instance) {
		ConfigManager.instance = instance;
	}

	public void init() {
		// TODO Auto-generated method stub

	}

	public void startWatchThread() throws FileSystemException {
		FileSystemManager fsm = VFS.getManager();
		FileObject fileObj = fsm.resolveFile(new File("./"), configPath);
		DefaultFileMonitor dfm = new DefaultFileMonitor(new FileListener() {
			@Override
			public void fileDeleted(FileChangeEvent event) throws Exception {
				// TODO Auto-generated method stub

			}

			@Override
			public void fileCreated(FileChangeEvent event) throws Exception {
				// TODO Auto-generated method stub

			}

			@Override
			public void fileChanged(FileChangeEvent event) throws Exception {
				// TODO Auto-generated method stub

			}
		});
		dfm.setRecursive(true);
		dfm.addFile(fileObj);
		dfm.setDelay(1000);
		dfm.start();
	}

	public Config getConfig() {
		// TODO Auto-generated method stub
		return null;
	}

	public static void main(String[] args) throws FileSystemException, InterruptedException {
		ConfigManager cm = new ConfigManager();
		cm.startWatchThread();
		Thread.sleep(20000);
	}
}
