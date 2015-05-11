package cn.edu.nju.winews.tool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Prop2xml {
	public static void main(String[] args) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.newDocument();
		File[] files = new File("conf").listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File arg0, String arg1) {
				if (arg1.endsWith("properties"))
					return true;
				return false;
			}
		});
		Element newspaperListElement = doc.createElement("newspaperList");
		for (int i = 0; i < files.length; i++) {
			System.out.println(files[i].getName());
			
			Properties prop = new Properties();
			prop.load(new FileReader(files[i]));
			Element newspaperElement = doc.createElement("newspaper");
			Element element;
			element = doc.createElement("name");
			element.setTextContent(files[i].getName().split("\\.")[0]);
			newspaperElement.appendChild(element);
			element = doc.createElement("domain");
			element.setTextContent("???");
			newspaperElement.appendChild(element);
			element = doc.createElement("province");
			element.setTextContent("???");
			newspaperElement.appendChild(element);
			element = doc.createElement("handler");
			element.setTextContent("cn.edu.nju.winews.handler.impl.DefaultHandler");
			newspaperElement.appendChild(element);
			element = doc.createElement("parser");
			element.setTextContent("cn.edu.nju.winews.parser.impl.DefaultParser");
			newspaperElement.appendChild(element);
			
			element = doc.createElement("url");
			element.setAttribute("start", prop.getProperty("end_date"));
			if(!prop.getProperty("start_date").equals("{{NOW}}")) {
				element.setAttribute("end", prop.getProperty("start_date"));
			}
			Element element2 = doc.createElement("pattern");
			Element element3 = doc.createElement("node");
			element3.setTextContent(prop.getProperty("node_url_pattern"));
			element2.appendChild(element3);
			element3 = doc.createElement("content");
			element3.setTextContent(prop.getProperty("content_url_pattern"));
			element2.appendChild(element3);
			element3 = doc.createElement("date");
			element3.setTextContent(prop.getProperty("date_pattern"));
			element2.appendChild(element3);
			element.appendChild(element2);
			
			element2 = doc.createElement("format");
			element3 = doc.createElement("node");
			element3.setTextContent(prop.getProperty("root_url_format"));
			element2.appendChild(element3);
			element3 = doc.createElement("date");
			element3.setTextContent(prop.getProperty("date_format"));
			element2.appendChild(element3);
			element.appendChild(element2);
			newspaperElement.appendChild(element);
			
			element = doc.createElement("selector");
			element2 = doc.createElement("preTitle");
			element2.setTextContent(prop.getProperty("subtitle_selector"));
			element.appendChild(element2);
			element2 = doc.createElement("title");
			element2.setTextContent(prop.getProperty("title_selector"));
			element.appendChild(element2);
			element2 = doc.createElement("subTitle");
			element2.setTextContent(prop.getProperty("subtitle_selector"));
			element.appendChild(element2);
			element2 = doc.createElement("author");
			element2.setTextContent(prop.getProperty("subtitle_selector"));
			element.appendChild(element2);
			element2 = doc.createElement("layout");
			element2.setTextContent(prop.getProperty("layout_selector"));
			element.appendChild(element2);
			element2 = doc.createElement("content");
			element2.setTextContent(prop.getProperty("content_selector"));
			element.appendChild(element2);
			element2 = doc.createElement("picture");
			element2.setTextContent(prop.getProperty("picture_selector"));
			element.appendChild(element2);
			newspaperElement.appendChild(element);
			newspaperListElement.appendChild(newspaperElement);
		}
		doc.appendChild(newspaperListElement);
		TransformerFactory transFactory = TransformerFactory.newInstance();
		Transformer trans = transFactory.newTransformer();
		trans.transform(new DOMSource(doc), new StreamResult(new FileOutputStream("output.xml")));
	}
}
