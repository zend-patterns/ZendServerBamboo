package com.zend.zendserver.bamboo;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.configuration.ConfigurationMap;
import com.atlassian.bamboo.process.ProcessService;
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
import com.zend.zendserver.bamboo.Process.ApplicationGetDetailsProcess;
import com.zend.zendserver.bamboo.Process.ApplicationGetStatusProcess;
import com.zend.zendserver.bamboo.Process.DeploymentProcess;
import com.zend.zendserver.bamboo.Process.ExecutableHelper;
import com.zend.zendserver.bamboo.Process.ProcessHandler;
import com.zend.zendserver.bamboo.Process.RollbackProcess;
import com.zend.zendserver.bamboo.TaskResult.ResultParserApplicationGetStatus;
import com.zend.zendserver.bamboo.TaskResult.ResultParserDeploymentCheck;
import com.zend.zendserver.bamboo.TaskResult.ResultParserInstallApp;

public class RollbackTask implements CommonTaskType, TaskType {
	
	private CapabilityContext capabilityContext;
    
    private final ProcessService processService;
    private BuildLogger buildLogger;

    public RollbackTask(final ProcessService processService, final CapabilityContext capabilityContext)
    {
        this.processService = processService;
        this.capabilityContext = capabilityContext;
    }
    
    public TaskResult execute(CommonTaskContext commonTaskContext)
			throws TaskException {
		
		TaskResultBuilder builder = TaskResultBuilder.newBuilder(commonTaskContext);
		buildLogger = commonTaskContext.getBuildLogger();
		buildLogger.addBuildLogEntry("Preparing Rollback (in Bamboo-Deploy context).");
		
		Deploy deploy = new Deploy(commonTaskContext);
		
		return doExecute(commonTaskContext, deploy, builder,commonTaskContext.getConfigurationMap()); 
	}

	public TaskResult execute(TaskContext taskContext) throws TaskException {
		TaskResultBuilder builder = TaskResultBuilder.newBuilder(taskContext);
		buildLogger = taskContext.getBuildLogger();
		buildLogger.addBuildLogEntry("Preparing Rollback (in Bamboo-Build context).");
		
		Build build = new Build(taskContext);
		
		return doExecute((CommonTaskContext) taskContext, build, builder, taskContext.getConfigurationMap());
	}
	
	public TaskResult doExecute(CommonTaskContext taskContext, BuildEnv buildEnv, TaskResultBuilder builder, ConfigurationMap configMap) {

		try {
			Thread.sleep(5000);
		}
		catch (Exception e) {}
		buildLogger.addErrorLogEntry("+++ 1");
		builder.success();

		buildLogger.addErrorLogEntry("+++ 2");
		ExecutableHelper eh = new ExecutableHelper(capabilityContext);
		buildLogger.addErrorLogEntry("+++ 3");
		ApplicationGetStatusProcess applicationGetStatusProcess = new ApplicationGetStatusProcess(configMap, eh);
		ApplicationGetDetailsProcess applicationGetDetailsProcess = new ApplicationGetDetailsProcess(configMap, eh);
		buildLogger.addErrorLogEntry("+++ 4");
		ProcessHandler applicationGetStatusProcessHandler = new ProcessHandler(applicationGetStatusProcess, buildLogger);
		applicationGetStatusProcessHandler.setBuildEnv(buildEnv);
		buildLogger.addErrorLogEntry("+++ 4.1");
		applicationGetStatusProcessHandler.execute();
		buildLogger.addErrorLogEntry("+++ 4.2");
		ProcessHandler applicationGetDetailsProcessHandler;
		
		buildLogger.addErrorLogEntry("+++ 5");
		
		DeploymentProcess deployProcess = new DeploymentProcess(configMap);
		buildLogger.addErrorLogEntry("+++ 6");
		deployProcess.setBuildEnv(buildEnv);
		buildLogger.addErrorLogEntry("+++ 7");
		ProcessHandler deployProcessHandler = new ProcessHandler(deployProcess, buildLogger);
		buildLogger.addErrorLogEntry("+++ 8");
		deployProcessHandler.setBuildEnv(buildEnv);
		buildLogger.addErrorLogEntry("+++ 9");
		
		ResultParserApplicationGetStatus resultParserApplicationGetStatus;
		buildLogger.addErrorLogEntry("+++ 10");
		try {
			String filename = applicationGetStatusProcessHandler.getOutputFilename();
			buildLogger.addErrorLogEntry("+++ 10.2");
			resultParserApplicationGetStatus = new ResultParserApplicationGetStatus(filename, buildLogger);
			buildLogger.addErrorLogEntry("+++ 11");
			String applicationId = resultParserApplicationGetStatus.getApplicationId(configMap.get("app_name"));
			buildLogger.addErrorLogEntry("+++ 12");
			
			int retry = Integer.parseInt(configMap.get("retry"));
			buildLogger.addErrorLogEntry("+++ 14");
			int waittime = Integer.parseInt(configMap.get("waittime"));
			buildLogger.addErrorLogEntry("+++ 15");

			RollbackProcess rollbackProcess = new RollbackProcess(configMap, eh);
			buildLogger.addErrorLogEntry("+++ 16");
			rollbackProcess.setApplicationId(applicationId);
			buildLogger.addErrorLogEntry("+++ 17");
			ProcessHandler rollbackProcessHandler = new ProcessHandler(rollbackProcess, buildLogger);
			buildLogger.addErrorLogEntry("+++ 18");
			rollbackProcessHandler.setBuildEnv(buildEnv);
			
			buildLogger.addErrorLogEntry("+++ 19");
			buildLogger.addErrorLogEntry("Rolling back Application " + applicationId);
			buildLogger.addErrorLogEntry("+++ 20");
			rollbackProcessHandler.execute();
			buildLogger.addErrorLogEntry("+++ 21");
			
			int itRollback = 0;
			Boolean isRollingback = true;
			do {
				buildLogger.addErrorLogEntry("+++ 22");
				itRollback++;
				buildLogger.addBuildLogEntry("Waiting for successful rollback (Iteration " + itRollback + " of " + retry + ")");
				if (itRollback > 1) {
					Thread.sleep(waittime * 1000);
				}
				applicationGetDetailsProcess.setApplicationId(applicationId);
				applicationGetDetailsProcessHandler = new ProcessHandler(applicationGetDetailsProcess, buildLogger);
				applicationGetDetailsProcessHandler.setBuildEnv(buildEnv);
				applicationGetDetailsProcess.incTestIteration();
				applicationGetDetailsProcessHandler.execute();
				
				ResultParserDeploymentCheck parser = new ResultParserDeploymentCheck(applicationGetDetailsProcessHandler.getOutputFilename());
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
