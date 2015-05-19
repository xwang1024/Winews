package cn.edu.nju.winews.crawler;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class CrawlerPool {
	private static final Logger logger = Logger.getLogger(DefaultHandler.class.getName());
	
	private static CrawlerPool instance;
	private int size;
	private List<Thread> general;
	private List<Thread> longRun;
	private static final int heartBeatMillis = 500;
	private int blockMillis;

	public static CrawlerPool getInstance() {
		if (instance == null) {
			instance = new CrawlerPool(5, 500);
		}
		return instance;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getBlockMillis() {
		return blockMillis;
	}

	public void setBlockMillis(int blockMillis) {
		this.blockMillis = blockMillis;
	}

	private CrawlerPool(int size, int blockMillis) {
		this.size = size;
		this.blockMillis = blockMillis;
		general = new ArrayList<Thread>(size);
		longRun = new ArrayList<Thread>(size);
	}

	public synchronized void addTask(Runnable task) {
		if (general.size() < size) {
			Thread t = new Thread(task);
			general.add(t);
			t.start();
			return;
		}
		while (true) {
			for (int i = 0; i < general.size(); i++) {
				try {
					Thread.sleep(heartBeatMillis);
				} catch (InterruptedException e) {
					System.err.println("This thread cannot be interrupted");
				}
				Thread t = general.get(i);
				if ((t == null) || (!t.isAlive()) || (t.isInterrupted())) {
					general.remove(i);
					t = new Thread(task);
					general.add(t);
					t.start();
					return;
				}
			}
			try {
				Thread.sleep(blockMillis);
			} catch (InterruptedException e) {
				System.err.println("This thread cannot be interrupted");
			}
		}
	}

	public void addLongTask(Runnable task) {
		if (longRun.size() < size) {
			Thread t = new Thread(task);
			longRun.add(t);
			t.start();
			return;
		}
		while (true) {
			for (int i = 0; i < longRun.size(); i++) {
				try {
					Thread.sleep(heartBeatMillis);
				} catch (InterruptedException e) {
					System.err.println("This thread cannot be interrupted");
				}
				Thread t = longRun.get(i);
				if ((t == null) || (!t.isAlive()) || (t.isInterrupted())) {
					longRun.remove(i);
					t = new Thread(task);
					longRun.add(t);
					t.start();
					return;
				}
			}
			try {
				Thread.sleep(blockMillis);
			} catch (InterruptedException e) {
				System.err.println("This thread cannot be interrupted");
			}
		}
		
	}
}
