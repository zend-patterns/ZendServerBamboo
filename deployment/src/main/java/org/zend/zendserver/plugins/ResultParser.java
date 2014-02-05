package org.zend.zendserver.plugins;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public abstract class ResultParser {
	protected Document doc;
	
	public ResultParser (String file) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(new File(file));
		doc.getDocumentElement().normalize();
		
		this.doc = doc;
	}
	
	protected Element getNodeResponseData() {
		return (Element) doc.getElementsByTagName("responseData").item(0);
	}
	
	protected String getValue(Element element, String tag) {
		NodeList nodes = element.getElementsByTagName(tag).item(0).getChildNodes();
		Node node = (Node) nodes.item(0);
		return node.getNodeValue();
	}
	
	protected Element getNode(Element element, String tag) {
		Element node = (Element) element.getElementsByTagName(tag).item(0);
		return node;
	}
}
