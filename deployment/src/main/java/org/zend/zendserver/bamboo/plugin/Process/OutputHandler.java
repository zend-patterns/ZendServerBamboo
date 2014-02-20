package org.zend.zendserver.bamboo.plugin.Process;

import java.io.PrintWriter;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.utils.process.PluggableProcessHandler;
import com.atlassian.utils.process.StringOutputHandler;

public class OutputHandler {
	private StringOutputHandler processOutputHandler;
	private String filename;
	private BuildLogger buildLogger;
	
	public OutputHandler (PluggableProcessHandler processHandler, String filename, BuildLogger buildLogger) {
		this.filename = filename;
		this.buildLogger = buildLogger;
		
		this.processOutputHandler = new StringOutputHandler();
		
		processHandler.setOutputHandler(processOutputHandler);
        processHandler.setErrorHandler(processOutputHandler);
	}
	
	public void write() {
		String output = processOutputHandler.getOutput();
		
		PrintWriter out;
		try {
			out = new PrintWriter(filename);
			out.println(output);
			out.close();
			
			buildLogger.addBuildLogEntry("Output written to: " + filename);
		} catch (Exception e) {
			buildLogger.addErrorLogEntry(e.getMessage());
		}
	}
}
