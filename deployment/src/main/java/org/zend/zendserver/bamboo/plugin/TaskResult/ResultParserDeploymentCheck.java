package org.zend.zendserver.bamboo.plugin.TaskResult;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.atlassian.bamboo.build.logger.BuildLogger;

public class ResultParserDeploymentCheck extends ResultParser {

	private BuildLogger bl;
	public ResultParserDeploymentCheck(String file, BuildLogger bl)
			throws ParserConfigurationException, SAXException, IOException {
		super(file);
		
		this.bl = bl;
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
