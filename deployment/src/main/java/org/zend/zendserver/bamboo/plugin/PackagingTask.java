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

public class PackagingTask implements TaskType {

	@Override
	public TaskResult execute(final TaskContext taskContext)
			throws TaskException {
		
		TaskResultBuilder builder = TaskResultBuilder.create(taskContext);
		BuildLogger buildLogger = taskContext.getBuildLogger();
 
		Command cmd = new Command(taskContext.getConfigurationMap());
		Call call = new Call(buildLogger);
		Build build = new Build(taskContext);
		Zpk zpk = new Zpk(taskContext, build);
		ResultFile resultFile = new ResultFile(taskContext, build);
		
		String packCmd = cmd.getPackZpk(
				taskContext.getWorkingDirectory().getAbsolutePath(), 
				zpk.getDir(), 
				zpk.getFileName(),
				zpk.createVersion(),
				resultFile.getPathPackZpk());
		call.execute(packCmd);
		
		if (call.isFailed()) {
			builder.failed();
		}
		else {
			builder.success();
		}
        return builder.build();
	}

}
