package com.zend.zendserver.bamboo;

import java.io.File;

import com.atlassian.bamboo.build.test.TestCollationService;
import com.atlassian.bamboo.task.CommonTaskContext;
import com.atlassian.bamboo.task.CommonTaskType;
import com.atlassian.bamboo.task.TaskContext;
import com.atlassian.bamboo.task.TaskException;
import com.atlassian.bamboo.task.TaskResult;
import com.atlassian.bamboo.task.TaskResultBuilder;
import com.atlassian.bamboo.task.TaskType;
import com.atlassian.bamboo.v2.build.agent.capability.CapabilityContext;
import com.zend.zendserver.bamboo.Env.Build;
import com.zend.zendserver.bamboo.Env.Deploy;
import com.zend.zendserver.bamboo.Process.ProcessHandler;

public class DeploymentTask extends BaseTask implements TaskType, CommonTaskType {
	
	public DeploymentTask(
    		final TestCollationService testCollationService, 
    		final com.atlassian.bamboo.process.ProcessService processService, 
    		final CapabilityContext capabilityContext)
    {
    	super(testCollationService, processService, capabilityContext);
    }

	public TaskResult execute(CommonTaskContext commonTaskContext) throws TaskException {
		Deploy deploy = new Deploy(commonTaskContext);
		init(commonTaskContext, deploy);
		
		TaskResultBuilder builder = TaskResultBuilder.newBuilder(commonTaskContext);
		buildLogger.addBuildLogEntry("Deployment (in Bamboo-Deploy context) has started...");
		
		return doExecute(builder);
	}

	public TaskResult execute(TaskContext taskContext) throws TaskException {
		Build build = new Build(taskContext);
		init(taskContext, build);
		
		TaskResultBuilder builder = TaskResultBuilder.newBuilder(taskContext);
		buildLogger.addBuildLogEntry("Deployment (in Bamboo-Build context) has started...");
		
		return doExecute(builder);
	}
	
	private TaskResult doExecute(TaskResultBuilder builder) {
		try {
			Thread.sleep(5000);
		}
		catch (Exception e) {}

		DeploymentReportCollector deploymentReportCollector = new DeploymentReportCollector(this, buildLogger);
		
		errorCollatorListener.setBuilder(builder);
		errorCollatorListener.setTestReportCollector(deploymentReportCollector);
		
		ProcessHandler deployment = processHandlerService.deployment();
		deployment.execute();
		
		try {
			File resultFileDeployment = new File(deployment.getOutputFilename());

			errorCollatorListener.setResultFile(resultFileDeployment);
			
			tests.collate();
			
		} catch (Exception e) { 
			buildLogger.addErrorLogEntry("Exception: " + e.getMessage());
			builder.failed();
		}
		
		return builder.checkReturnCode(deployment.getExternalProcess()).build();
	}
}
