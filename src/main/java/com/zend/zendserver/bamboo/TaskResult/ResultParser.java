package com.zend.zendserver.bamboo.TaskResult;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.zend.zendserver.bamboo.ZendServerWebApiException;

public abstract class ResultParser {
	protected Document doc;
	
	public ResultParser (String file) throws Exception {
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(new File(file));
			doc.getDocumentElement().normalize();
			
			this.doc = doc;
		}
		catch (Exception e) {
			Path path = Paths.get(file);
			String error = new String(Files.readAllBytes(path));
			
			throw new ZendServerWebApiException(error);
		}
	}
	
	protected Element getNodeResponseData() {
		return (Element) doc.getElementsByTagName("responseData").item(0);
	}
	
	public String getValue(Element element, String tag) {
		NodeList nodes = element.getElementsByTagName(tag).item(0).getChildNodes();
		Node node = (Node) nodes.item(0);
		return node.getNodeValue();
	}
	
	public Element getNode(Element element, String tag) {
		Element node = (Element) element.getElementsByTagName(tag).item(0);
		return node;
	}
}
