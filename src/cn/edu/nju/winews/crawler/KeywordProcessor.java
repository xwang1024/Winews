package cn.edu.nju.winews.crawler;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ConcurrentLinkedQueue;

import cn.edu.nju.winews.dao.KeywordDao;
import cn.edu.nju.winews.dao.impl.KeywordDaoImpl;
import cn.edu.nju.winews.model.News;
import cn.edu.nju.winews.nlpir.NLPIR;
import cn.edu.nju.winews.nlpir.NLPIRConfig;

public class KeywordProcessor {
	private static KeywordProcessor instance;
	private ConcurrentLinkedQueue<News> queue = new ConcurrentLinkedQueue<News>();
	private Thread t;

	private NLPIR nlpir;
	private KeywordDao kwd;

	public static KeywordProcessor getInstance() throws Exception {
		if (instance == null) {
			instance = new KeywordProcessor();
			instance.process();
		}
		return instance;
	}

	private KeywordProcessor() throws Exception {
		kwd = new KeywordDaoImpl();

		nlpir = NLPIRConfig.initNLPIR();
		BufferedReader bfr = new BufferedReader(new InputStreamReader(new FileInputStream("wi_dict.txt")));
		String line = null;
		while ((line = bfr.readLine()) != null) {
			nlpir.NLPIR_AddUserWord(line);
		}
		bfr.close();
	}

	public void addNewsTask(News news) {
		queue.offer(news);
	}

	public void process() {
		t = new Thread(new Runnable() {
			public void run() {
				while (true) {
					synchronized (queue) {
						if (!queue.isEmpty()) {
							News news = queue.poll();
							StringBuilder totalContent = new StringBuilder();
							totalContent.append(news.getPreTitle());
							totalContent.append("\n");
							totalContent.append(news.getTitle());
							totalContent.append("\n");
							totalContent.append(news.getSubTitle());
							totalContent.append("\n");
							totalContent.append(news.getContent());
							String kwLine = nlpir.NLPIR_GetKeyWords(totalContent.toString(), 5, false);
							String[] sp = kwLine.split("#");
							for (int i = 0; i < sp.length; i++) {
								try {
									kwd.addSearchIndex(sp[i], news.getUrl());
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						} else {
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		});
		t.start();
	}
}
