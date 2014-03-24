package com.zend.zendserver.bamboo.TaskResult;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.atlassian.bamboo.build.logger.BuildLogger;

public class ResultParserInstallApp extends ResultParser {

	private BuildLogger buildLogger;
	public ResultParserInstallApp(String file, BuildLogger buildLogger)
			throws ParserConfigurationException, SAXException, IOException {
		super(file);
		
		this.buildLogger = buildLogger;
	}
	
	public String getApplicationId() {
		String id = null;
		try {
			Element responseData = (Element) doc.getElementsByTagName("responseData").item(0);
			Element applicationInfo = getNode(responseData, "applicationInfo");
			
			id = getValue(applicationInfo, "id");
		}
		catch (Exception e) {
			buildLogger.addErrorLogEntry("Exception: " + e.getMessage());
		}
		return id;
	}
}
