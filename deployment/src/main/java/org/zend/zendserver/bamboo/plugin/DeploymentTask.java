package org.zend.zendserver.bamboo.plugin;

import org.zend.zendserver.bamboo.plugin.Helper.Build;
import org.zend.zendserver.bamboo.plugin.Helper.Zpk;
import org.zend.zendserver.bamboo.plugin.TaskResult.ResultFile;
import org.zend.zendserver.bamboo.plugin.ZendServerSDK.Call;
import org.zend.zendserver.bamboo.plugin.ZendServerSDK.Command;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.task.TaskContext;
import com.atlassian.bamboo.task.TaskException;
import com.atlassian.bamboo.task.TaskResult;
import com.atlassian.bamboo.task.TaskResultBuilder;
import com.atlassian.bamboo.task.TaskType;

public class DeploymentTask implements TaskType {
	
	@Override
	public TaskResult execute(final TaskContext taskContext)
			throws TaskException {
		
		TaskResultBuilder builder = TaskResultBuilder.create(taskContext);
		builder.failed();
		BuildLogger buildLogger = taskContext.getBuildLogger();
		
		try {
			Thread.sleep(5000);
		}
		catch (Exception e) {}
		
		Command cmd = new Command(taskContext.getConfigurationMap());
		Call call = new Call(buildLogger);
		Build build = new Build(taskContext);
		Zpk zpk = new Zpk(taskContext, build);
		ResultFile resultFile = new ResultFile(taskContext, build);
				
		String installCmd = cmd.getInstallApp(
				zpk.getPath(),
				resultFile.getPathInstallApp()
				);
		
		buildLogger.addBuildLogEntry("Deployment has started...");
		call.execute(installCmd);
		if (!call.isFailed()) {
			builder.success();
		}

        return builder.build();
	}

}
