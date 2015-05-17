package cn.edu.nju.winews.nlpir;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class NlpirMain {
	public static void main(String[] args) throws Exception {
		// 初始化NLPIR分词工具
		NLPIR nlpir = NLPIRConfig.initNLPIR();
		// 读取词库
		readDictFile(nlpir);
		String keywordLine = nlpir.NLPIR_ParagraphProcess("据中国地震台网测定，北京时间2015年5月17日10时11分 在安徽省滁州市天长市（北纬32.6度，东经119.1度）发生3.0级地震，震源深度5公里。(原标题：5月17日10时11分安徽省滁州市天长市发生3.0级地震)", 1);
		System.out.println(keywordLine);
	}

	public static void readDictFile(NLPIR nlpir) throws Exception {
		BufferedReader bfr = new BufferedReader(new InputStreamReader(new FileInputStream("wi_dict.txt")));
		String line = null;
		while ((line = bfr.readLine()) != null) {
			nlpir.NLPIR_AddUserWord(line);
		}
		bfr.close();
	}
}
