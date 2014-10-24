package com.zend.zendserver.bamboo.TaskResult;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ResultParserMonitorGetIssuesListPredefinedFilter extends ResultParser {
	public ResultParserMonitorGetIssuesListPredefinedFilter(String file)
			throws Exception {
		super(file);
	}
	
	public NodeList getNodeListIssues() {
		Element issues = getNodeIssues();
		return issues.getElementsByTagName("issue");
	}
	
	public Element getNodeIssues() {
		Element responseData = (Element) doc.getElementsByTagName("responseData").item(0);
		return getNode(responseData, "issues");
	}
}
