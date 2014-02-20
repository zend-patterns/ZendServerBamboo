package org.zend.zendserver.bamboo.plugin.Process;

import java.io.File;

import org.zend.zendserver.bamboo.plugin.Helper.Build;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.task.TaskContext;
import com.atlassian.utils.process.ExternalProcess;
import com.atlassian.utils.process.ExternalProcessBuilder;
import com.atlassian.utils.process.PluggableProcessHandler;

public class ProcessHandler {
	private final TaskContext taskContext;
	private Process process;
	private ExternalProcess externalProcess;
	private PluggableProcessHandler processHandler;
	private BuildLogger buildLogger;
	
	private Boolean failed = false;
	
	public ProcessHandler(Process process, TaskContext taskContext)
    {
		this.process = process;
		this.taskContext = taskContext;
		this.buildLogger = taskContext.getBuildLogger();
		
		this.processHandler = new PluggableProcessHandler();
    }
	
	public ExternalProcess getExternalProcess() {
		return externalProcess;
	}
	
	protected ExternalProcessBuilder getProcessBuilder() throws Exception {
		ExternalProcessBuilder epb = new ExternalProcessBuilder()
			.command(process.getCommandList())
			.handler(processHandler);
		
		return epb;
	}
	
	public void execute() {
		try {
			
			OutputHandler outputHandler = new OutputHandler(processHandler, getOutputFilename(), buildLogger);
			
			ExternalProcessBuilder pb = getProcessBuilder();
			
			externalProcess = pb.build();
			externalProcess.execute();
			
			buildLogger.addBuildLogEntry("Process command line: " + externalProcess.getCommandLine());
	        
	        outputHandler.write();
		}
		catch (Exception e) {
			buildLogger.addErrorLogEntry(e.getMessage());
			failed = true;
		}
	}
	
	public Boolean hasFailed() {
		if (!failed) {
			failed = !processHandler.succeeded();
		}
		
		return failed;
	}
	
	public String getOutputFilename() {
		Build build = new Build(taskContext);
		StringBuilder filename = new StringBuilder();
		filename.append(taskContext.getWorkingDirectory().getAbsolutePath());
		filename.append("/");
		filename.append(process.getOutputFilePrefix());
		filename.append(build.getBuildNr());
		filename.append("-");
		filename.append(build.getRevision());
		filename.append(process.getOutputFileSuffix());
		
		File dir = new File(new File(filename.toString()).getParent());
		dir.mkdirs();
		
		return filename.toString();
	}
}
