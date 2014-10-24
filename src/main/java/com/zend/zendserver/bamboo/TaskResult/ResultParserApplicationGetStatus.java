package com.zend.zendserver.bamboo.TaskResult;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.atlassian.bamboo.build.logger.BuildLogger;

public class ResultParserApplicationGetStatus extends ResultParser {

	private BuildLogger buildLogger;
	private String applicationId = null;
	private String deploymentTime = null;
	
	public ResultParserApplicationGetStatus(String file, BuildLogger buildLogger)
			throws Exception {
		super(file);
		
		this.buildLogger = buildLogger;
	}
	
	public ResultParserApplicationGetStatus(String file)
			throws Exception {
		super(file);
	}
	
	private void fetchApplicationData(String applicationName) throws Exception {
		String id = null;
		Element responseData = getNodeResponseData();
		Element applicationsList = getNode(responseData, "applicationsList");
		
		NodeList applicationInfoList = applicationsList.getElementsByTagName("applicationInfo");
		for (int i = 0; i < applicationInfoList.getLength(); i++) {
	        Element applicationInfo = (Element) applicationInfoList.item(i);
	        if (getValue(applicationInfo, "userAppName").equals(applicationName)) {
				id = getValue(applicationInfo, "id");
				applicationId = id;
				deploymentTime = getValue(applicationInfo, "creationTime");
				break;
			}
	    }

		if (id == null) throw new Exception("id not found for application " + applicationName);
	}
	
	public String getApplicationId(String applicationName) throws Exception {
		if (applicationId == null) {
			fetchApplicationData(applicationName);
		}
		return applicationId;
	}

	public String getDeploymentTime(String applicationName) throws Exception {
		if (deploymentTime == null) {
			fetchApplicationData(applicationName);
		}
		return deploymentTime;
	}
}
