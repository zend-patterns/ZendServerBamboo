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

////import org.zend.zendserver.plugins.*;

public class DeploymentCheckReportCollector implements TestReportCollector {
	private BuildLogger buildLogger;
	private DeploymentCheckTask task;
	private ResultParserDeploymentCheck parser;
	
	public DeploymentCheckReportCollector(DeploymentCheckTask task, BuildLogger buildLogger) {
		this.buildLogger = buildLogger;
		this.task = task;
	}
	
	public TestCollectionResult collect(File file) throws Exception {
		buildLogger.addBuildLogEntry("...started.");
		TestCollectionResultBuilder builder = new TestCollectionResultBuilder();
		
		Collection<TestResults> successfulTestResults = Lists.newArrayList();
        Collection<TestResults> failingTestResults = Lists.newArrayList();
		
        try {
        	
        	parser = new ResultParserDeploymentCheck(file.getAbsolutePath(), buildLogger);
        	NodeList servers = parser.getNodeListServer();

        	buildLogger.addBuildLogEntry("Checking " + String.valueOf((servers.getLength() - 1)) + " servers for correct deployment");
			for (int i = 0; i < servers.getLength() - 1; i++) {
				
				Element serverInfo = (Element) servers.item(i);
				String id = parser.getValue(serverInfo, "id");
				String status = parser.getValue(serverInfo, "status");
				
				buildLogger.addBuildLogEntry("Server " + id + " has status " + status);
				TestResults testResults;
				switch (status) {
					case "uploadError":
					case "stageError":
					case "activateError":
					case "deactivateError":
					case "unstageError":
					case "partiallyDeployed":
					case "notExists":
					case "unknown":
						testResults = new TestResults(id, getTestErrorDescription(serverInfo), "");
						testResults.setState(TestState.FAILED);
						failingTestResults.add(testResults);
						break;
						
					case "activating":
					case "deactivating":
					case "staging":
					case "unstaging":
					case "rollingBack":
						task.isDeploying(true);
						break;
						
					case "OK":
					case "deployed":
						testResults = new TestResults(id, getTestSuccessDescription(serverInfo), "");
						testResults.setState(TestState.SUCCESS);
						successfulTestResults.add(testResults);
						break;
						
					default:
						testResults = new TestResults(id, getTestErrorDescription(serverInfo), "");
						testResults.setState(TestState.FAILED);
						failingTestResults.add(testResults);
						break;
				}
			}

		} catch (Exception ex4) {
			buildLogger.addErrorLogEntry("Exception: " + ex4.getMessage());
		}
        
        return builder
                .addSuccessfulTestResults(successfulTestResults)
                .addFailedTestResults(failingTestResults)
                .build();
	}

	public Set<String> getSupportedFileExtensions()
    {
		Set<String> set = new HashSet<String>();
        set.add("xml");
        
        return set;
    }
	
	public String getTestErrorDescription(Element serverInfo) {
		Element applicationInfo = parser.getNodeApplicationInfo();
		Element messageList = parser.getNode(applicationInfo, "messageList");
		String error;
		try {
			error = parser.getValue(messageList, "error");
		}
		catch (Exception e) {
			error = "Unknown error";
		}
		
		String deployedVersion = parser.getValue(applicationInfo, "deployedVersion");
		
		String status = parser.getValue(serverInfo, "status");
		String serverId = parser.getValue(serverInfo, "id");
		
		String placeholder = "Server %s - Status: %s; Error: %s; Deployed Version: %s";
        String description = String.format(placeholder,
        		serverId,
        		status,
        		error,
        		deployedVersion);
		return  description;
	}
	
	public String getTestSuccessDescription(Element serverInfo) {
		String deployedVersion = parser.getValue(serverInfo, "deployedVersion");
		
		String status = parser.getValue(serverInfo, "status");
		String serverId = parser.getValue(serverInfo, "id");
		
		String placeholder = "Server %s - Status: %s; Deployed Version: %s";
        String description = String.format(placeholder,
        		serverId,
        		status,
        		deployedVersion);
		return  description;
	}
}
