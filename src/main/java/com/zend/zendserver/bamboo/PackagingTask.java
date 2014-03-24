package com.zend.zendserver.bamboo;

import com.atlassian.bamboo.task.TaskContext;
import com.atlassian.bamboo.task.TaskException;
import com.atlassian.bamboo.task.TaskResult;
import com.atlassian.bamboo.task.TaskResultBuilder;
import com.atlassian.bamboo.task.TaskType;
import com.atlassian.bamboo.v2.build.agent.capability.CapabilityContext;
import com.zend.zendserver.bamboo.Env.Build;
import com.zend.zendserver.bamboo.Process.ProcessHandler;

public class PackagingTask extends BaseTask implements TaskType {
    public PackagingTask(
    		com.atlassian.bamboo.process.ProcessService processService,
			CapabilityContext capabilityContext) {
		super(processService, capabilityContext);
	}

	public TaskResult execute(final TaskContext taskContext)
			throws TaskException {
		
		Build build = new Build(taskContext);
		init(taskContext, build);
		buildLogger = taskContext.getBuildLogger();
		
		TaskResultBuilder builder = TaskResultBuilder.newBuilder(taskContext);
		
		ProcessHandler packaging = processHandlerService.packaging();
		packaging.execute();
		
		return builder.checkReturnCode(packaging.getExternalProcess()).build();
	}

}
