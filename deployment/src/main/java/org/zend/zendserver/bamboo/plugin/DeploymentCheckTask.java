package org.zend.zendserver.bamboo.plugin;

import java.io.File;

import org.zend.zendserver.bamboo.plugin.Process.ApplicationGetDetailsProcess;
import org.zend.zendserver.bamboo.plugin.Process.DeploymentProcess;
import org.zend.zendserver.bamboo.plugin.Process.ExecutableHelper;
import org.zend.zendserver.bamboo.plugin.Process.ProcessHandler;
import org.zend.zendserver.bamboo.plugin.Process.RollbackProcess;
import org.zend.zendserver.bamboo.plugin.TaskResult.ResultParserInstallApp;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.build.test.TestCollationService;
import com.atlassian.bamboo.process.ProcessService;
import com.atlassian.bamboo.task.TaskContext;
import com.atlassian.bamboo.task.TaskException;
import com.atlassian.bamboo.task.TaskResult;
import com.atlassian.bamboo.task.TaskResultBuilder;
import com.atlassian.bamboo.task.TaskType;
import com.atlassian.bamboo.v2.build.agent.capability.CapabilityContext;

public class DeploymentCheckTask implements TaskType {
	private TestCollationService testCollationService;
	private Boolean isDeploying; 
	
private CapabilityContext capabilityContext;
    
    private final ProcessService processService;

    public DeploymentCheckTask(final TestCollationService testCollationService, final ProcessService processService, final CapabilityContext capabilityContext)
    {
    	this.testCollationService = testCollationService;
        this.processService = processService;
        this.capabilityContext = capabilityContext;
    }
    
	public TaskResult execute(final TaskContext taskContext)
			throws TaskException {

		final BuildLogger buildLogger = taskContext.getBuildLogger();

		try {
			Thread.sleep(5000);
		}
		catch (Exception e) {}

		TaskResultBuilder builder = TaskResultBuilder.create(taskContext);

		ExecutableHelper eh = new ExecutableHelper(capabilityContext); 
		ApplicationGetDetailsProcess applicationGetDetailsProcess = new ApplicationGetDetailsProcess(taskContext, eh);
		ProcessHandler applicationGetDetailsProcessHandler;
		
		DeploymentProcess deployProcess = new DeploymentProcess(taskContext);
		ProcessHandler deployProcessHandler = new ProcessHandler(deployProcess, taskContext);
		
		buildLogger.addBuildLogEntry("Preparing test runs.");
		ResultParserInstallApp resultParserInstallApp;
		try {
			resultParserInstallApp = new ResultParserInstallApp(deployProcessHandler.getOutputFilename(), buildLogger);
			String applicationId = resultParserInstallApp.getApplicationId();
			applicationGetDetailsProcess.setApplicationId(applicationId);
			
			int retry = Integer.parseInt(taskContext.getConfigurationMap().get("retry"));
			int wait = Integer.parseInt(taskContext.getConfigurationMap().get("wait"));
			int it = 0;
			do {
				isDeploying = false; 
				try {
					it++;
					buildLogger.addBuildLogEntry("Test iteration " + it + " of " + retry + "...");
					if (it > 1) {
						Thread.sleep(wait * 1000);
					}
					applicationGetDetailsProcessHandler = new ProcessHandler(applicationGetDetailsProcess, taskContext);
					applicationGetDetailsProcess.incTestIteration();
					applicationGetDetailsProcessHandler.execute();

					File resultFileAbsolute = new File(applicationGetDetailsProcessHandler.getOutputFilename());
					testCollationService.collateTestResults(
							taskContext, 
							resultFileAbsolute.getName(), 
							new DeploymentCheckReportCollector(this, buildLogger));
					
				} catch (Exception e) {
					buildLogger.addErrorLogEntry(e.getMessage());
				}
			} while(isDeploying && it <= retry);

			if (isDeploying && it >= retry) {
				buildLogger.addErrorLogEntry("Stop testing; deployment is still running. Aborting. ");
				builder.failed();
			}

			if (builder.checkTestFailures().getTaskState().toString() == "Failed") {
				try {
					buildLogger.addErrorLogEntry("Deployment FAILED! Initializing ROLLBACK.");

					RollbackProcess rollbackProcess = new RollbackProcess(taskContext, eh);
					rollbackProcess.setApplicationId(applicationId);
					ProcessHandler rollbackProcessHandler = new ProcessHandler(rollbackProcess, taskContext);
					
					rollbackProcessHandler.execute();
				}
				catch (Exception e) {
					buildLogger.addErrorLogEntry("Exception: "+e.getMessage());
				}
			}
		}
		catch (Exception e1) {
			builder.failed();
			buildLogger.addErrorLogEntry(e1.getMessage());
		}
		return builder.build();
	}

	public void isDeploying(Boolean isDeploying) {
		this.isDeploying = isDeploying;
	}
}
