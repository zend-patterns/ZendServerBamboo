package org.zend.zendserver.bamboo.plugin.Process;

import java.util.Arrays;
import java.util.List;

import org.zend.zendserver.bamboo.plugin.Helper.Build;
import org.zend.zendserver.bamboo.plugin.Helper.Zpk;

import com.atlassian.bamboo.task.TaskContext;

public class PackagingProcess implements Process {
	
	public static final String OUTPUT_FILE_PREFIX = "zwsa/pack-";
	public static final String OUTPUT_FILE_SUFFIX = ".log";
	
	private final TaskContext taskContext;
	private final ExecutableHelper executableHelper;
	
	public PackagingProcess(TaskContext taskContext, ExecutableHelper executableHelper)
    {
		this.taskContext = taskContext;
		this.executableHelper = executableHelper;
    }
	
	public List<String> getCommandList() throws Exception {
		String workingDir = taskContext.getWorkingDirectory().getAbsolutePath();
		Build build = new Build(taskContext);
		Zpk zpk = new Zpk(taskContext, build);
		
		List<String> commandList = Arrays.asList(
				executableHelper.getExecutable(),
				"packZpk",
				"--folder=" + workingDir,
				"--destination=" + zpk.getDir(),
				"--name=" + zpk.getFileName(),
				"--version=" + zpk.createVersion());
		
		return commandList;
	}

	public String getOutputFilePrefix() {
		return OUTPUT_FILE_PREFIX;
	}

	public String getOutputFileSuffix() {
		return OUTPUT_FILE_SUFFIX;
	}
}
