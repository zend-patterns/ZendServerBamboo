package com.zend.zendserver.bamboo;


import com.atlassian.bamboo.configuration.ConfigurationMap;
import com.atlassian.bamboo.task.CommonTaskContext;
import com.atlassian.bamboo.task.CommonTaskType;
import com.atlassian.bamboo.task.TaskContext;
import com.atlassian.bamboo.task.TaskException;
import com.atlassian.bamboo.task.TaskResult;
import com.atlassian.bamboo.task.TaskResultBuilder;
import com.atlassian.bamboo.task.TaskType;
import com.atlassian.bamboo.v2.build.agent.capability.CapabilityContext;
import com.zend.zendserver.bamboo.Env.Build;
import com.zend.zendserver.bamboo.Env.BuildEnv;
import com.zend.zendserver.bamboo.Env.Deploy;
import com.zend.zendserver.bamboo.Process.ProcessHandler;

public class DeploymentTask extends BaseTask implements TaskType, CommonTaskType {
	
	public DeploymentTask(
			com.atlassian.bamboo.process.ProcessService processService,
			CapabilityContext capabilityContext) {
		super(processService, capabilityContext);
	}

	public TaskResult execute(CommonTaskContext commonTaskContext) throws TaskException {
		Deploy deploy = new Deploy(commonTaskContext);
		init(commonTaskContext, deploy);
		
		TaskResultBuilder builder = TaskResultBuilder.newBuilder(commonTaskContext);
		buildLogger.addBuildLogEntry("Deployment (in Bamboo-Deploy context) has started...");
		
		return doExecute(deploy, builder, commonTaskContext.getConfigurationMap()); 
	}

	public TaskResult execute(TaskContext taskContext) throws TaskException {
		Build build = new Build(taskContext);
		init(taskContext, build);
		
		TaskResultBuilder builder = TaskResultBuilder.newBuilder(taskContext);
		buildLogger.addBuildLogEntry("Deployment (in Bamboo-Build context) has started...");
		
		return doExecute(build, builder, taskContext.getConfigurationMap());
	}
	
	private TaskResult doExecute(BuildEnv buildEnv, TaskResultBuilder builder, ConfigurationMap configMap) {
		try {
			Thread.sleep(5000);
		}
		catch (Exception e) {}

		ProcessHandler deployment = processHandlerService.deployment();
		deployment.execute();
		
		return builder.checkReturnCode(deployment.getExternalProcess()).build();
	}

}
