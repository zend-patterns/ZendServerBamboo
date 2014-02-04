package org.zend.zendserver.plugins;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.build.test.TestCollectionResult;
import com.atlassian.bamboo.build.test.TestCollectionResultBuilder;
import com.atlassian.bamboo.build.test.TestReportCollector;
import com.atlassian.bamboo.results.tests.TestResults;
import com.atlassian.bamboo.resultsummary.tests.TestState;
import com.atlassian.bamboo.task.TaskResultBuilder;
import com.google.common.collect.Lists;

public class DeploymentCheckReportCollector implements TestReportCollector {
	private BuildLogger buildLogger;
	private DeploymentCheckTask task;
	
	public DeploymentCheckReportCollector(DeploymentCheckTask task, BuildLogger buildLogger) {
		this.buildLogger = buildLogger;
		this.task = task;
	}
	
	public TestCollectionResult collect(File file) throws Exception {
		TestCollectionResultBuilder builder = new TestCollectionResultBuilder();
		
		Collection<TestResults> successfulTestResults = Lists.newArrayList();
        Collection<TestResults> failingTestResults = Lists.newArrayList();
		
        String deploymentLogFile = "/home/jan/workspaces/sandboxx/deployment/target/bamboo/home/xml-data/build-dir/TES-TEST-TEST/result.xml";
		
        try {

			File stocks = new File(deploymentLogFile);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(stocks);
			doc.getDocumentElement().normalize();

			Element responseData = (Element) doc.getElementsByTagName("responseData").item(0);
			buildLogger.addBuildLogEntry("+++ 001: " + responseData.getNodeName()); 
			//Element responseDataElement = (Element) responseData;
			Element applicationDetails = (Element) responseData.getElementsByTagName("applicationDetails").item(0);
			buildLogger.addBuildLogEntry("+++ 002: " + applicationDetails.getNodeName());
			Element applicationInfo = (Element) applicationDetails.getElementsByTagName("applicationInfo").item(0);
			buildLogger.addBuildLogEntry("+++ 003: " + applicationInfo.getNodeName());

			//Element responseDataElement = (Element) responseData;
			//String status = getValue("status", responseDataElement);
			//String appName = getValue("appName", responseDataElement);
			//buildLogger.addBuildLogEntry("+++ appName: " + getValue("appName", responseDataElement));
			//buildLogger.addBuildLogEntry("+++ Status: " + getValue("status", responseDataElement)); 

			
			
			NodeList servers = applicationInfo.getElementsByTagName("servers").item(0).getChildNodes();
			buildLogger.addBuildLogEntry("+++ server-length: " + servers.getLength()); 
			
			for (int i = 0; i < servers.getLength() - 1; i++) {
				
				Element node = (Element) servers.item(i);
				buildLogger.addBuildLogEntry("01: " + node.getNodeName());

				buildLogger.addErrorLogEntry("&&& " + getValue(node, "deployedVersion"));
				buildLogger.addErrorLogEntry("&&& " + getElement(node, "deployedVersion").getNodeName());
				buildLogger.addErrorLogEntry("&&& " + getTestSuccessDescription(node));
				buildLogger.addErrorLogEntry("&&& " + getTestErrorDescription(applicationInfo, node));
				
				//Element applicationServer = getElement("applicationServer", node);
				String id = node.getElementsByTagName("id").item(0).getChildNodes().item(0).getNodeValue();
				buildLogger.addBuildLogEntry("02: " + id);

				String deployedVersion = node.getElementsByTagName("deployedVersion").item(0).getChildNodes().item(0).getNodeValue();
				//String deployedVersion = getValue("deployedVersion", applicationServer);
				buildLogger.addBuildLogEntry("04: " + deployedVersion);
				//String status = getValue("status", applicationServer);
				String status = node.getElementsByTagName("status").item(0).getChildNodes().item(0).getNodeValue();
				buildLogger.addBuildLogEntry("05: " + status);
				
				buildLogger.addBuildLogEntry("+++ id: " + id);
				buildLogger.addBuildLogEntry("+++ deployedVersion: " + deployedVersion);
				buildLogger.addBuildLogEntry("+++ status: " + status);
				
				TestResults testResults = new TestResults(id, getTestErrorDescription(applicationInfo, node), "");
				switch (status) {
					case "uploadError":
					case "stageError":
					case "activateError":
					case "deactivateError":
					case "unstageError":
					case "partiallyDeployed":
					case "notExists":
					case "unknown":
						//testResults = new TestResults(id, getTestErrorDescription(applicationInfo, node), "");
						testResults.setState(TestState.FAILED);
						failingTestResults.add(testResults);
						buildLogger.addBuildLogEntry("0001: failed");
						//successfulTestResults.add(testResults);
						break;
						
					case "activating":
					case "deactivating":
					case "staging":
					case "unstaging":
					case "rollingBack":
						buildLogger.addBuildLogEntry("0002: skipped");
						task.isDeploying(true);
						break;
						
					case "OK":
					case "deployed":
						testResults.setState(TestState.SUCCESS);
						successfulTestResults.add(testResults);
						break;
						
					default:
						testResults.setState(TestState.FAILED);
						failingTestResults.add(testResults);
						break;
				}
			}

		} catch (Exception ex) {
			buildLogger.addBuildLogEntry("+++ Exception " + ex.getMessage());
		}
        
        return builder
                .addSuccessfulTestResults(successfulTestResults)
                .addFailedTestResults(failingTestResults)
                .build();
	}
	
	private String getValue(Element element, String tag) {
		NodeList nodes = element.getElementsByTagName(tag).item(0).getChildNodes();
		Node node = (Node) nodes.item(0);
		return node.getNodeValue();
	}
	
	private Element getElement(Element element, String tag) {
		Element node = (Element) element.getElementsByTagName(tag).item(0);
		return node;
	}

	public Set<String> getSupportedFileExtensions()
    {
		Set<String> set = new HashSet<String>();
        set.add("xml");
        
        return set;
    }
	
	public String getTestErrorDescription(Element applicationInfo, Element serverInfo) {
		Element messageList = getElement(applicationInfo, "messageList");
		String error;
		try {
			error = getValue(messageList, "error");
		}
		catch (Exception e) {
			error = "Unknown error";
		}
		
		String deployedVersion = getValue(applicationInfo, "deployedVersion");
		
		String status = getValue(serverInfo, "status");
		String serverId = getValue(serverInfo, "id");
		
		String placeholder = "Server %s - Status: %s; Error: %s; Deployed Version: %s";
        String description = String.format(placeholder,
        		serverId,
        		status,
        		error,
        		deployedVersion);
		return  description;
	}
	
	public String getTestSuccessDescription(Element serverInfo) {
		String deployedVersion = getValue(serverInfo, "deployedVersion");
		
		String status = getValue(serverInfo, "status");
		String serverId = getValue(serverInfo, "id");
		
		String placeholder = "Server %s - Status: %s; Deployed Version: %s";
        String description = String.format(placeholder,
        		serverId,
        		status,
        		deployedVersion);
		return  description;
	}
	
}
