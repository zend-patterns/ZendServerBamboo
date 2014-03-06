package com.zend.zendserver.bamboo;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.build.test.TestCollectionResult;
import com.atlassian.bamboo.build.test.TestCollectionResultBuilder;
import com.atlassian.bamboo.build.test.TestReportCollector;
import com.atlassian.bamboo.results.tests.TestResults;
import com.atlassian.bamboo.resultsummary.tests.TestState;
import com.google.common.collect.Lists;
import com.zend.zendserver.bamboo.TaskResult.ResultParserDeploymentCheck;

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
        	
        	parser = new ResultParserDeploymentCheck(file.getAbsolutePath());
        	NodeList servers = parser.getNodeListServer();

        	buildLogger.addBuildLogEntry("Checking " + String.valueOf((servers.getLength() - 1)) + " servers for successful deployment");
			for (int i = 0; i < servers.getLength() - 1; i++) {
				
				Element serverInfo = (Element) servers.item(i);
				String id = parser.getValue(serverInfo, "id");
				String status = parser.getValue(serverInfo, "status");
				
				buildLogger.addBuildLogEntry("Server " + id + " has status " + status);
				TestResults testResults;
				
				if (status.equals("uploadError") ||
					status.equals("stageError") ||
					status.equals("activateError") ||
					status.equals("deactivateError") ||
					status.equals("unstageError") ||
					status.equals("partiallyDeployed") ||
					status.equals("notExists") ||
					status.equals("unknown")) 
				{
					testResults = new TestResults(id, getTestErrorDescription(serverInfo), "");
					testResults.setState(TestState.FAILED);
					failingTestResults.add(testResults);
				}
				else {
					if (status.equals("activating") ||
						status.equals("deactivating") ||
						status.equals("staging") ||
						status.equals("unstaging") ||
						status.equals("rollingBack")) 
					{
						task.isDeploying(true);
					}
					else {
						if (status.equals("OK") ||
							status.equals("deployed")) 
						{
							testResults = new TestResults(id, getTestSuccessDescription(serverInfo), "");
							testResults.setState(TestState.SUCCESS);
							successfulTestResults.add(testResults);
						}						
						else {
							testResults = new TestResults(id, getTestErrorDescription(serverInfo), "");
							testResults.setState(TestState.FAILED);
							failingTestResults.add(testResults);
						}
					}
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
