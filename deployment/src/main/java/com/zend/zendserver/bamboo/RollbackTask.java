package com.zend.zendserver.bamboo;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

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
import com.zend.zendserver.bamboo.Process.DeploymentProcess;
import com.zend.zendserver.bamboo.Process.ProcessHandler;
import com.zend.zendserver.bamboo.TaskResult.ResultParserApplicationGetStatus;
import com.zend.zendserver.bamboo.TaskResult.ResultParserDeploymentCheck;

public class RollbackTask extends BaseTask implements CommonTaskType, TaskType {
	
    public RollbackTask(
    		com.atlassian.bamboo.process.ProcessService processService,
			CapabilityContext capabilityContext) {
		super(processService, capabilityContext);
	}
    
    public TaskResult execute(CommonTaskContext commonTaskContext)
			throws TaskException {
		
		TaskResultBuilder builder = TaskResultBuilder.newBuilder(commonTaskContext);
		buildLogger = commonTaskContext.getBuildLogger();
		buildLogger.addBuildLogEntry("Preparing Rollback (in Bamboo-Deploy context).");
		
		Deploy deploy = new Deploy(commonTaskContext);
		init(commonTaskContext, deploy);
		
		return doExecute(commonTaskContext, deploy, builder,commonTaskContext.getConfigurationMap()); 
	}

	public TaskResult execute(TaskContext taskContext) throws TaskException {
		TaskResultBuilder builder = TaskResultBuilder.newBuilder(taskContext);
		buildLogger = taskContext.getBuildLogger();
		buildLogger.addBuildLogEntry("Preparing Rollback (in Bamboo-Build context).");
		
		Build build = new Build(taskContext);
		init(taskContext, build);
		
		return doExecute((CommonTaskContext) taskContext, build, builder, taskContext.getConfigurationMap());
	}
	
	public TaskResult doExecute(CommonTaskContext taskContext, BuildEnv buildEnv, TaskResultBuilder builder, ConfigurationMap configMap) {

		try {
			Thread.sleep(5000);
		}
		catch (Exception e) {}
		builder.success();

		ProcessHandler applicationGetStatus = processHandlerService.applicationGetStatus();
		applicationGetStatus.execute();
		
		DeploymentProcess deployProcess = new DeploymentProcess(configMap);
		deployProcess.setBuildEnv(buildEnv);
		ProcessHandler deployProcessHandler = new ProcessHandler(deployProcess, buildLogger);
		deployProcessHandler.setBuildEnv(buildEnv);
		
		ResultParserApplicationGetStatus resultParserApplicationGetStatus;
		try {
			String filename = applicationGetStatus.getOutputFilename();
			resultParserApplicationGetStatus = new ResultParserApplicationGetStatus(filename, buildLogger);
			String applicationId = resultParserApplicationGetStatus.getApplicationId(configMap.get("app_name"));
			
			int retry = Integer.parseInt(configMap.get("retry"));
			int waittime = Integer.parseInt(configMap.get("waittime"));

			ProcessHandler rollback = processHandlerService.rollback(applicationId);			
			buildLogger.addErrorLogEntry("Rolling back Application " + applicationId);
			rollback.execute();
			
			int itRollback = 0;
			Boolean isRollingback = true;
			do {
				itRollback++;
				buildLogger.addBuildLogEntry("Waiting for successful rollback (Iteration " + itRollback + " of " + retry + ")");
				if (itRollback > 1) {
					Thread.sleep(waittime * 1000);
				}
				ProcessHandler applicationGetDetails = processHandlerService.applicationGetDetails(applicationId);
				applicationGetDetails.execute();
				
				ResultParserDeploymentCheck parser = new ResultParserDeploymentCheck(applicationGetDetails.getOutputFilename());
				NodeList servers = parser.getNodeListServer();
				for (int i = 0; i < servers.getLength() - 1; i++) {
					Element serverInfo = (Element) servers.item(i);
					String id = parser.getValue(serverInfo, "id");
					String status = parser.getValue(serverInfo, "status");
					if (status.equals("deployed")) { 
						String deployedVersion = parser.getValue(serverInfo, "deployedVersion");
						buildLogger.addErrorLogEntry("Server " + id + " - version of current App installed: " + deployedVersion);
						isRollingback = false;
					}
				}
			} while(isRollingback && itRollback <= retry);
			
			if (isRollingback && itRollback >= retry) {
				buildLogger.addErrorLogEntry("Rollback FAILED! Please check application status in Zend Server UI!");
				builder.failed();
			}
			else {
				buildLogger.addErrorLogEntry("Rollback succeeded!");
			}
		}
		catch (Exception e) {
			buildLogger.addErrorLogEntry("Exception: "+e.getMessage());
		}
		return builder.build();
	}
}
