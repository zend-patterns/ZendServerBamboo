package com.zend.zendserver.bamboo.Process;

import java.io.File;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.exception.StartupException;
import com.atlassian.utils.process.ExternalProcess;
import com.atlassian.utils.process.ExternalProcessBuilder;
import com.atlassian.utils.process.PluggableProcessHandler;
import com.zend.zendserver.bamboo.Env.BuildEnv;

public class ProcessHandler {
	public Process process;
	private ExternalProcess externalProcess;
	private PluggableProcessHandler processHandler;
	private BuildLogger buildLogger;
	
	private Boolean failed = false;
	private BuildEnv buildEnv;
	
	public ProcessHandler(Process process, BuildLogger buildLogger)
    {
		this.process = process;
		this.buildLogger = buildLogger;
		
		this.processHandler = new PluggableProcessHandler();
    }
	
	public ExternalProcess getExternalProcess() {
		return externalProcess;
	}
	
	protected ExternalProcessBuilder getProcessBuilder() throws Exception {
		ExternalProcessBuilder epb = new ExternalProcessBuilder()
			.command(process.getCommandList())
			.env("RAW_ZS_OUTPUT", "1")
			.env("HOMEPATH", "\\")
			.env("HOMEDRIVE", "c:")
			.handler(processHandler);
		return epb;
	}
	
	public void execute() {
		try {
			OutputHandler outputHandler = new OutputHandler(processHandler, getOutputFilename(), buildLogger);
			process.getCommandList();
			externalProcess = getProcessBuilder().build();
			externalProcess.execute();
			buildLogger.addBuildLogEntry("Process command line: " + externalProcess.getCommandLine());
	        
	        outputHandler.write();
		}
		catch (StartupException e) {
			buildLogger.addErrorLogEntry("StartupException in ProcessHandler.execute function");
			buildLogger.addErrorLogEntry(e.getMessage());
			buildLogger.addErrorLogEntry("Please check your 'Server capabilities' settings in the admin area. A valid 'Zend Server Web API' capability has to be defined in order to execute this task.");
			failed = true;
		}
		catch (Exception e) {
			buildLogger.addErrorLogEntry("Exception in ProcessHandler.execute function");
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
	
	public void setBuildEnv(BuildEnv buildEnv) {
		this.buildEnv = buildEnv;
	}
	
	public BuildEnv getBuildEnv() {
		return buildEnv;
	}
	
	public String getOutputFilename() throws Exception {
		StringBuilder filename = new StringBuilder();
		filename.append(buildEnv.getWorkingDir());
		filename.append("/");
		filename.append(process.getOutputFilePrefix());
		filename.append(buildEnv.getBuildNr());

		filename.append(process.getOutputFileSuffix());
		File dir = new File(new File(filename.toString()).getParent());
		dir.mkdirs();
		return filename.toString();
	}
}
