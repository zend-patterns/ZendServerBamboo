package com.zend.zendserver.bamboo;


import com.atlassian.bamboo.process.ProcessService;
import com.atlassian.bamboo.task.TaskContext;
import com.atlassian.bamboo.task.TaskException;
import com.atlassian.bamboo.task.TaskResult;
import com.atlassian.bamboo.task.TaskResultBuilder;
import com.atlassian.bamboo.task.TaskType;
import com.atlassian.bamboo.v2.build.agent.capability.CapabilityContext;
import com.zend.zendserver.bamboo.Env.Build;
import com.zend.zendserver.bamboo.Process.ExecutableHelper;
import com.zend.zendserver.bamboo.Process.PackagingProcess;
import com.zend.zendserver.bamboo.Process.ProcessHandler;

public class PackagingTask implements TaskType {

    private CapabilityContext capabilityContext;
    
    private final ProcessService processService;

    public PackagingTask(final ProcessService processService, final CapabilityContext capabilityContext)
    {
        this.processService = processService;
        this.capabilityContext = capabilityContext;
    }
     
    public TaskResult execute(final TaskContext taskContext)
			throws TaskException {
		
		TaskResultBuilder builder = TaskResultBuilder.create(taskContext);
		
		ExecutableHelper eh = new ExecutableHelper(capabilityContext); 
		PackagingProcess packProcess = new PackagingProcess(taskContext.getConfigurationMap(), eh);
		Build build = new Build(taskContext);
		packProcess.setBuildEnv(build);
		
		ProcessHandler processHandler = new ProcessHandler(packProcess, taskContext.getBuildLogger());
		processHandler.setBuildEnv(build);
		
		processHandler.execute();

		return builder.checkReturnCode(processHandler.getExternalProcess()).build();
	}

}
