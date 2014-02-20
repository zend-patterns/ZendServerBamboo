package org.zend.zendserver.bamboo.plugin.TaskResult;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ResultParserDeploymentCheck extends ResultParser {
	public ResultParserDeploymentCheck(String file)
			throws ParserConfigurationException, SAXException, IOException {
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
}
