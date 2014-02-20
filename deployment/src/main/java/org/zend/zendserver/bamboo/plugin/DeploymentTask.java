package org.zend.zendserver.bamboo.plugin;

import org.zend.zendserver.bamboo.plugin.Process.DeploymentProcess;
import org.zend.zendserver.bamboo.plugin.Process.ExecutableHelper;
import org.zend.zendserver.bamboo.plugin.Process.ProcessHandler;

import com.atlassian.bamboo.process.ProcessService;
import com.atlassian.bamboo.task.TaskContext;
import com.atlassian.bamboo.task.TaskException;
import com.atlassian.bamboo.task.TaskResult;
import com.atlassian.bamboo.task.TaskResultBuilder;
import com.atlassian.bamboo.task.TaskType;
import com.atlassian.bamboo.v2.build.agent.capability.CapabilityContext;

public class DeploymentTask implements TaskType {
	private CapabilityContext capabilityContext;
    
    private final ProcessService processService;

    public DeploymentTask(final ProcessService processService, final CapabilityContext capabilityContext)
    {
        this.processService = processService;
        this.capabilityContext = capabilityContext;
    }
    
	public TaskResult execute(final TaskContext taskContext)
			throws TaskException {
		
		TaskResultBuilder builder = TaskResultBuilder.create(taskContext);
		
		try {
			Thread.sleep(5000);
		}
		catch (Exception e) {}
		
		taskContext.getBuildLogger().addBuildLogEntry("Deployment has started...");
		
		ExecutableHelper eh = new ExecutableHelper(capabilityContext); 
		DeploymentProcess deployProcess = new DeploymentProcess(taskContext, eh);
		ProcessHandler processHandler = new ProcessHandler(deployProcess, taskContext);
		
		processHandler.execute();

		return builder.checkReturnCode(processHandler.getExternalProcess()).build();
	}

}
