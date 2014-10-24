package com.zend.zendserver.bamboo.TaskResult;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ResultParserError extends ResultParser {
	public ResultParserError(String file)
			throws Exception {
		super(file);
	}
	
	public NodeList getNodeListServer() {
		Element applicationInfo = getNodeApplicationInfo();
		return applicationInfo.getElementsByTagName("servers").item(0).getChildNodes();
	}
	
	public Element getNodeApplicationInfo() {
		Element responseData = (Element) doc.getElementsByTagName("responseData").item(0);
		Element applicationDetails = getNode(responseData, "applicationDetails");
		return getNode(applicationDetails, "applicationInfo");
	}
	
	public Boolean foundError() {
		return (doc.getElementsByTagName("errorData").getLength() > 0);
	}
	
	public Element getNodeErrorData() {
		return (Element) doc.getElementsByTagName("errorData").item(0);
	}
}
