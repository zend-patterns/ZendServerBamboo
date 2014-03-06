package com.zend.zendserver.bamboo;


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
import com.atlassian.bamboo.variable.CustomVariableContext;
import com.zend.zendserver.bamboo.Env.Build;
import com.zend.zendserver.bamboo.Env.BuildEnv;
import com.zend.zendserver.bamboo.Env.Deploy;
import com.zend.zendserver.bamboo.Process.DeploymentProcess;
import com.zend.zendserver.bamboo.Process.ExecutableHelper;
import com.zend.zendserver.bamboo.Process.ProcessHandler;

public class DeploymentTask implements TaskType, CommonTaskType {
	private CapabilityContext capabilityContext;
    
    private final ProcessService processService;
    private BuildLogger buildLogger;

    public DeploymentTask(final ProcessService processService, final CapabilityContext capabilityContext)
    {
        this.processService = processService;
        this.capabilityContext = capabilityContext;
    }
    
    private CustomVariableContext customVariableContext;
    
    public CustomVariableContext getCustomVariableContext() {
        return customVariableContext;
    }
     
    public void setCustomVariableContext(CustomVariableContext customVariableContext) {
        this.customVariableContext = customVariableContext;
    }
    
	public TaskResult execute(CommonTaskContext commonTaskContext)
			throws TaskException {
		
		TaskResultBuilder builder = TaskResultBuilder.newBuilder(commonTaskContext);
		buildLogger = commonTaskContext.getBuildLogger();
		buildLogger.addBuildLogEntry("Deployment (in Bamboo-Deploy context) has started...");
		
		Deploy deploy = new Deploy(commonTaskContext);
		
		return doExecute(deploy, builder,commonTaskContext.getConfigurationMap()); 
	}

	public TaskResult execute(TaskContext taskContext) throws TaskException {
		TaskResultBuilder builder = TaskResultBuilder.newBuilder(taskContext);
		buildLogger = taskContext.getBuildLogger();
		buildLogger.addBuildLogEntry("Deployment (in Bamboo-Build context) has started...");
		
		Build build = new Build(taskContext);
		
		return doExecute(build, builder, taskContext.getConfigurationMap());
	}
	
	private TaskResult doExecute(BuildEnv buildEnv, TaskResultBuilder builder, ConfigurationMap configMap) {
		try {
			Thread.sleep(5000);
		}
		catch (Exception e) {}
		ExecutableHelper eh = new ExecutableHelper(capabilityContext); 
		DeploymentProcess deployProcess = new DeploymentProcess(configMap, eh);
		deployProcess.setBuildEnv(buildEnv);
		
		ProcessHandler processHandler = new ProcessHandler(deployProcess, buildLogger);
		processHandler.setBuildEnv(buildEnv);
		
		processHandler.execute();
		
		return builder.checkReturnCode(processHandler.getExternalProcess()).build();
	}

}
