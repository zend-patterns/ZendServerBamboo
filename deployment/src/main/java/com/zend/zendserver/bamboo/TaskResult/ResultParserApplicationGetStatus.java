package com.zend.zendserver.bamboo.TaskResult;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.atlassian.bamboo.build.logger.BuildLogger;

public class ResultParserApplicationGetStatus extends ResultParser {

	private BuildLogger buildLogger;
	public ResultParserApplicationGetStatus(String file, BuildLogger buildLogger)
			throws ParserConfigurationException, SAXException, IOException {
		super(file);
		
		this.buildLogger = buildLogger;
	}
	
	public String getApplicationId(String applicationName) {
		String id = null;
		buildLogger.addErrorLogEntry("+++*** y  " + " " + applicationName);
		try {
			Element responseData = getNodeResponseData();
			Element applicationsList = getNode(responseData, "applicationsList");
			int deployedApps = applicationsList.getChildNodes().getLength();
			
			//Element applicationInfo; // = (Element) applicationsList.getElementsByTagName("applicationInfo").item(0);

			
			
			//buildLogger.addErrorLogEntry("+++*** y " + i + " " + applicationInfoNode.getNodeName() + " " + node2.getChildNodes().item(0).getNodeValue());
			
			NodeList applicationInfoList = applicationsList.getElementsByTagName("applicationInfo");
			for (int i = 0; i < applicationInfoList.getLength(); i++) {
		        //Node node = applicationInfoList.item(i);
		        Element applicationInfo = (Element) applicationInfoList.item(i);
		        //String appId = getValue(applicationInfo, "id");
		        buildLogger.addErrorLogEntry("+++*** y  " + i + " " + getValue(applicationInfo, "userAppName"));
		        
		        if (getValue(applicationInfo, "userAppName").equals(applicationName)) {
					id = getValue(applicationInfo, "id");
					buildLogger.addErrorLogEntry("+++*** y id  " + i + " " + id);
					break;
				}
		        //if (node.getNodeType() == Node.ELEMENT_NODE) {
		            // do something with the current element
		        /*
		        	buildLogger.addErrorLogEntry("+++*** x " + i + " " + node.getNodeName());
		        	NodeList nodeList2 = applicationsList.getElementsByTagName("*");
		        	for (int j = 0; j < nodeList2.getLength();j++) {
				        Node node2 = nodeList2.item(j);
				        if (node2.getNodeType() == Node.ELEMENT_NODE) {
				            // do something with the current element
				        	buildLogger.addErrorLogEntry("+++*** y " + i + " " + node2.getNodeName() + " " + node2.getChildNodes().item(0).getNodeValue());
				        }
				    }
		        //}*/
		    }
		    
		/*
			for (int i = 0; i < deployedApps; i++) {
				//Element applicationInfo2 = (Element) applicationsList.getChildNodes().item(i);
				applicationInfo = (Element) applicationsList.getChildNodes().item(i).getChildNodes().item(0);
				if (getValue(applicationInfo, "appName").equals(applicationName)) {
					id = getValue(applicationInfo, "id");
					break;
				}
			}*/
			if (id == null) throw new Exception("id not found for application " + applicationName);
		}
		catch (Exception e) {
			buildLogger.addErrorLogEntry("Exception: " + e.getMessage());
		}
		return id;
	}
}
