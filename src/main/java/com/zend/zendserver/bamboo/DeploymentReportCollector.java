package com.zend.zendserver.bamboo;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.build.test.TestCollectionResult;
import com.atlassian.bamboo.build.test.TestCollectionResultBuilder;
import com.atlassian.bamboo.build.test.TestReportCollector;
import com.atlassian.bamboo.results.tests.TestResults;
import com.atlassian.bamboo.resultsummary.tests.TestState;
import com.google.common.collect.Lists;
import com.zend.zendserver.bamboo.TaskResult.ResultParserError;

public class DeploymentReportCollector implements TestReportCollector {
	private BuildLogger buildLogger;
	private ResultParserError parser;
	
	public DeploymentReportCollector(DeploymentTask task, BuildLogger buildLogger) {
		this.buildLogger = buildLogger;
	}
	
	public TestCollectionResult collect(File file) throws Exception {
		buildLogger.addBuildLogEntry("Checking Zend Server output for errors");
		TestCollectionResultBuilder builder = new TestCollectionResultBuilder();
		
		Collection<TestResults> successfulTestResults = Lists.newArrayList();
        Collection<TestResults> failingTestResults = Lists.newArrayList();
		
        try {
        	
        	parser = new ResultParserError(file.getAbsolutePath());
        	
        	if (parser.foundError()) {
        	
        		String errorMessage = parser.getValue(parser.getNodeErrorData(), "errorMessage");
				
				TestResults testResults = new TestResults("ErrorMessage", errorMessage, "");
				testResults.setState(TestState.FAILED);
				failingTestResults.add(testResults);
        	}
        	else {
        		TestResults	testResults = new TestResults("Deployment", "Initialization successful", "");
							testResults.setState(TestState.SUCCESS);
							successfulTestResults.add(testResults);
        	}

		} catch (Exception ex) {
			buildLogger.addErrorLogEntry("Exception: " + ex.getMessage());
			TestResults testResultsEx = new TestResults("Exception", ex.getMessage(), "");
			testResultsEx.setState(TestState.FAILED);
			failingTestResults.add(testResultsEx);
			builder.addFailedTestResults(failingTestResults);
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
}
