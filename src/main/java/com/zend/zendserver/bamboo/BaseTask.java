package com.zend.zendserver.bamboo;

import java.awt.Event;
import java.util.Iterator;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.build.test.TestCollationService;
import com.atlassian.bamboo.build.test.TestCollectionResult;
import com.atlassian.bamboo.results.tests.TestResults;
import com.atlassian.bamboo.task.CommonTaskContext;
import com.atlassian.bamboo.task.TaskContext;
import com.atlassian.bamboo.v2.build.agent.capability.CapabilityContext;
import com.atlassian.bamboo.variable.CustomVariableContext;
import com.zend.zendserver.bamboo.Env.BuildEnv;
import com.zend.zendserver.bamboo.Process.ProcessHandlerService;
import com.zend.zendserver.bamboo.Process.ProcessService;

abstract public class BaseTask {    
	
	protected com.atlassian.bamboo.process.ProcessService bambooProcessService;
	protected CapabilityContext capabilityContext;
	protected CustomVariableContext customVariableContext;
	protected TestCollationService testCollationService;
	protected BuildLogger buildLogger;
	protected ProcessService processService;
	protected ProcessHandlerService processHandlerService;
	protected EventListener errorCollatorListener;
	protected Tests tests;

	public BaseTask(
		final com.atlassian.bamboo.process.ProcessService bambooProcessService, 
		final CapabilityContext capabilityContext)
	{
	    this.bambooProcessService = bambooProcessService;
	    this.capabilityContext = capabilityContext;
	}
	
	public BaseTask(
    		final TestCollationService testCollationService, 
    		final com.atlassian.bamboo.process.ProcessService bambooProcessService, 
    		final CapabilityContext capabilityContext)
    {
    	this.testCollationService = testCollationService;
        this.bambooProcessService = bambooProcessService;
        this.capabilityContext = capabilityContext;
    }
    
    public CustomVariableContext getCustomVariableContext() {
        return customVariableContext;
    }
     
    public void setCustomVariableContext(CustomVariableContext customVariableContext) {
        this.customVariableContext = customVariableContext;
    }
	
	public void init(CommonTaskContext commonTaskContext, BuildEnv buildEnv) {
		buildLogger = commonTaskContext.getBuildLogger();
		
		processService = new ProcessService();
		processService.setCapabilityContext(capabilityContext);
		processService.setConfigMap(commonTaskContext.getConfigurationMap());
		processService.setBuildEnv(buildEnv);
		
		processHandlerService = new ProcessHandlerService();
		processHandlerService.setProcessService(processService);
		processHandlerService.setBuildEnv(buildEnv);
		processHandlerService.setBuildLogger(buildLogger);
		
		errorCollatorListener = getDeployErrorCollator();
		errorCollatorListener.setCommonTaskContext(commonTaskContext);
		
		tests = new Tests();
		tests.setListener(errorCollatorListener);
	}
	
	public void init(TaskContext taskContext, BuildEnv buildEnv) {
		buildLogger = taskContext.getBuildLogger();
		
		processService = new ProcessService();
		processService.setCapabilityContext(capabilityContext);
		processService.setConfigMap(taskContext.getConfigurationMap());
		processService.setBuildEnv(buildEnv);
		
		processHandlerService = new ProcessHandlerService();
		processHandlerService.setProcessService(processService);
		processHandlerService.setBuildEnv(buildEnv);
		processHandlerService.setBuildLogger(buildLogger);
		
		errorCollatorListener = getBuildErrorCollator();
		errorCollatorListener.setTaskContext(taskContext);
		
		tests = new Tests();
		tests.setListener(errorCollatorListener);
	}
	
	protected EventListener getBuildErrorCollator() {
		return new EventListener() {
			@Override
			public void fireEvent(Event e) {
				testCollationService.collateTestResults(
						taskContext, 
						resultFile.getName(), 
						testReportCollector);
				getBuilder().checkTestFailures();
			}
		};
	}
	
	protected EventListener getDeployErrorCollator() {
		return new EventListener() {
			@Override
			public void fireEvent(Event e) throws Exception {
				TestCollectionResult result = testReportCollector.collect(resultFile);
				Iterator<TestResults> failIterator = result.getFailedTestResults().iterator();
				if (failIterator.hasNext()) {
					buildLogger.addErrorLogEntry("Test error messages:");
					while (failIterator.hasNext()) {
						builder.failed();
						buildLogger.addErrorLogEntry(failIterator.next().getActualMethodName());
					}
				}
				
				Iterator<TestResults> successIterator = result.getSuccessfulTestResults().iterator();
				if (failIterator.hasNext()) {
					buildLogger.addBuildLogEntry("Test success messages:");
					while (successIterator.hasNext()) {
						buildLogger.addBuildLogEntry(successIterator.next().toString());
					}
				}
			}
		};
	}
}
