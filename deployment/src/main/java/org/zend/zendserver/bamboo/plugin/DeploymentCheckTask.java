package org.zend.zendserver.bamboo.plugin;

import java.io.File;
import java.util.Iterator;

import org.zend.zendserver.bamboo.plugin.Env.Build;
import org.zend.zendserver.bamboo.plugin.Env.BuildEnv;
import org.zend.zendserver.bamboo.plugin.Env.Deploy;
import org.zend.zendserver.bamboo.plugin.Process.ApplicationGetDetailsProcess;
import org.zend.zendserver.bamboo.plugin.Process.DeploymentProcess;
import org.zend.zendserver.bamboo.plugin.Process.ExecutableHelper;
import org.zend.zendserver.bamboo.plugin.Process.ProcessHandler;
import org.zend.zendserver.bamboo.plugin.Process.RollbackProcess;
import org.zend.zendserver.bamboo.plugin.TaskResult.ResultParserInstallApp;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.build.test.TestCollationService;
import com.atlassian.bamboo.build.test.TestCollectionResult;
import com.atlassian.bamboo.configuration.ConfigurationMap;
import com.atlassian.bamboo.process.ProcessService;
import com.atlassian.bamboo.results.tests.TestResults;
import com.atlassian.bamboo.task.CommonTaskContext;
import com.atlassian.bamboo.task.CommonTaskType;
import com.atlassian.bamboo.task.TaskContext;
import com.atlassian.bamboo.task.TaskException;
import com.atlassian.bamboo.task.TaskResult;
import com.atlassian.bamboo.task.TaskResultBuilder;
import com.atlassian.bamboo.task.TaskState;
import com.atlassian.bamboo.task.TaskType;
import com.atlassian.bamboo.v2.build.agent.capability.CapabilityContext;

public class DeploymentCheckTask implements CommonTaskType, TaskType {
	private TestCollationService testCollationService;
	private Boolean isDeploying; 
	private DeploymentCheckReportCollector check = null;
	
	private CapabilityContext capabilityContext;
    
    private final ProcessService processService;
    private BuildLogger buildLogger;

    public DeploymentCheckTask(final TestCollationService testCollationService, final ProcessService processService, final CapabilityContext capabilityContext)
    {
    	this.testCollationService = testCollationService;
        this.processService = processService;
        this.capabilityContext = capabilityContext;
    }
    
    public TaskResult execute(CommonTaskContext commonTaskContext)
			throws TaskException {
		
		TaskResultBuilder builder = TaskResultBuilder.newBuilder(commonTaskContext);
		buildLogger = commonTaskContext.getBuildLogger();
		buildLogger.addBuildLogEntry("Preparing test runs (in Bamboo-Deploy context).");
		
		Deploy deploy = new Deploy(commonTaskContext);
		
		check = new DeploymentCheckReportCollector(this, buildLogger);
		
		return doExecute(commonTaskContext, deploy, builder,commonTaskContext.getConfigurationMap()); 
	}

	public TaskResult execute(TaskContext taskContext) throws TaskException {
		TaskResultBuilder builder = TaskResultBuilder.newBuilder(taskContext);
		buildLogger = taskContext.getBuildLogger();
		buildLogger.addBuildLogEntry("Preparing test runs (in Bamboo-Build context).");
		
		Build build = new Build(taskContext);
		
		return doExecute((CommonTaskContext) taskContext, build, builder, taskContext.getConfigurationMap());
	}
	
	public TaskResult doExecute(CommonTaskContext taskContext, BuildEnv buildEnv, TaskResultBuilder builder, ConfigurationMap configMap) {

		try {
			Thread.sleep(5000);
		}
		catch (Exception e) {}
		
		builder.success();

		ExecutableHelper eh = new ExecutableHelper(capabilityContext); 
		ApplicationGetDetailsProcess applicationGetDetailsProcess = new ApplicationGetDetailsProcess(configMap, eh);
		ProcessHandler applicationGetDetailsProcessHandler;
		
		DeploymentProcess deployProcess = new DeploymentProcess(configMap);
		deployProcess.setBuildEnv(buildEnv);
		ProcessHandler deployProcessHandler = new ProcessHandler(deployProcess, buildLogger);
		deployProcessHandler.setBuildEnv(buildEnv);
		
		ResultParserInstallApp resultParserInstallApp;
		try {
			resultParserInstallApp = new ResultParserInstallApp(deployProcessHandler.getOutputFilename(), buildLogger);
			String applicationId = resultParserInstallApp.getApplicationId();
			applicationGetDetailsProcess.setApplicationId(applicationId);
			
			int retry = Integer.parseInt(configMap.get("retry"));
			int waittime = Integer.parseInt(configMap.get("waittime"));
			int it = 0;
			do {
				isDeploying = false; 
				try {
					it++;
					buildLogger.addBuildLogEntry("Test iteration " + it + " of " + retry + "...");
					if (it > 1) {
						Thread.sleep(waittime * 1000);
					}
					applicationGetDetailsProcessHandler = new ProcessHandler(applicationGetDetailsProcess, buildLogger);
					applicationGetDetailsProcessHandler.setBuildEnv(buildEnv);
					applicationGetDetailsProcess.incTestIteration();
					applicationGetDetailsProcessHandler.execute();

					File resultFileAbsolute = new File(applicationGetDetailsProcessHandler.getOutputFilename());
					if (check == null) {
						testCollationService.collateTestResults(
								(TaskContext) taskContext, 
								resultFileAbsolute.getName(), 
								new DeploymentCheckReportCollector(this, buildLogger));
					}
					else {
						TestCollectionResult result = check.collect(resultFileAbsolute);
						Iterator<TestResults> failIterator = result.getFailedTestResults().iterator();
						if (failIterator.hasNext()) {
							buildLogger.addErrorLogEntry("Test error messages:");
							while (failIterator.hasNext()) {
								builder.failed();
								buildLogger.addErrorLogEntry(failIterator.next().getActualMethodName());
							}
						}
						
						Iterator<TestResults> successIterator = result.getSuccessfulTestResults().iterator();
						if (failIterator.hasNext()) {
							buildLogger.addBuildLogEntry("Test success messages:");
							while (successIterator.hasNext()) {
								buildLogger.addBuildLogEntry(successIterator.next().toString());
							}
						}
					}
					
				} catch (Exception e) {
					buildLogger.addErrorLogEntry(e.getMessage());
				}
			} while(isDeploying && it <= retry);

			if (isDeploying && it >= retry) {
				buildLogger.addErrorLogEntry("Stop testing; deployment is still running. Aborting. ");
				builder.failed();
			}
			
			if (check == null) {
				builder.checkTestFailures();
			}

			if (builder.getTaskState() == TaskState.FAILED || builder.getTaskState() == TaskState.ERROR) {
				try {
					buildLogger.addErrorLogEntry("Deployment FAILED! Initializing ROLLBACK.");

					RollbackProcess rollbackProcess = new RollbackProcess(configMap, eh);
					rollbackProcess.setApplicationId(applicationId);
					ProcessHandler rollbackProcessHandler = new ProcessHandler(rollbackProcess, buildLogger);
					rollbackProcessHandler.setBuildEnv(buildEnv);
					
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
