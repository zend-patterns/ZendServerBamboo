package com.zend.zendserver.bamboo;

import java.io.File;
import java.util.Iterator;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.build.test.TestCollationService;
import com.atlassian.bamboo.build.test.TestCollectionResult;
import com.atlassian.bamboo.configuration.ConfigurationMap;
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
import com.zend.zendserver.bamboo.Env.Build;
import com.zend.zendserver.bamboo.Env.BuildEnv;
import com.zend.zendserver.bamboo.Env.Deploy;
import com.zend.zendserver.bamboo.Process.ProcessHandler;
import com.zend.zendserver.bamboo.TaskResult.ResultParserInstallApp;

public class DeploymentCheckTask extends BaseTask implements CommonTaskType, TaskType {
	private Boolean isDeploying; 
	private DeploymentCheckReportCollector check = null;
	
    private BuildLogger buildLogger;
    
    private TaskContext taskContext = null;

    public DeploymentCheckTask(
    		final TestCollationService testCollationService, 
    		final com.atlassian.bamboo.process.ProcessService processService, 
    		final CapabilityContext capabilityContext)
    {
    	super(testCollationService, processService, capabilityContext);
    }
    
    public TaskResult execute(CommonTaskContext commonTaskContext)
			throws TaskException {
		
		TaskResultBuilder builder = TaskResultBuilder.newBuilder(commonTaskContext);
		buildLogger = commonTaskContext.getBuildLogger();
		buildLogger.addBuildLogEntry("Preparing test runs (in Bamboo-Deploy context).");
		
		Deploy deploy = new Deploy(commonTaskContext);
		init(commonTaskContext, deploy);
		
		check = new DeploymentCheckReportCollector(this, buildLogger);
		
		return doExecute(commonTaskContext, deploy, builder, commonTaskContext.getConfigurationMap()); 
	}

	public TaskResult execute(TaskContext taskContext) throws TaskException {
		this.taskContext = taskContext;
		TaskResultBuilder builder = TaskResultBuilder.newBuilder(taskContext);
		buildLogger = taskContext.getBuildLogger();
		buildLogger.addBuildLogEntry("Preparing test runs (in Bamboo-Build context).");
		
		Build build = new Build(taskContext);
		init(taskContext, build);
		
		return doExecute((CommonTaskContext) taskContext, build, builder, taskContext.getConfigurationMap());
	}
	
	public TaskResult doExecute(CommonTaskContext commonTaskContext, BuildEnv buildEnv, TaskResultBuilder builder, ConfigurationMap configMap) {

		try {
			Thread.sleep(5000);
		}
		catch (Exception e) {}
		
		builder.success();
		
		ResultParserInstallApp resultParserInstallApp;
		try {
			resultParserInstallApp = new ResultParserInstallApp(processHandlerService.deployment().getOutputFilename(), buildLogger);
			String applicationId = resultParserInstallApp.getApplicationId();
			
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
					
					ProcessHandler applicationGetDetails = processHandlerService.applicationGetDetails(applicationId);
					applicationGetDetails.execute();
					

					buildLogger.addErrorLogEntry("+++ out " + applicationGetDetails.getOutputFilename());
					File resultFileAbsolute = new File(applicationGetDetails.getOutputFilename());
					if (check == null) {
						testCollationService.collateTestResults(
								(TaskContext) commonTaskContext, 
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
				if (configMap.getAsBoolean("rollback")) {
					try {
						buildLogger.addErrorLogEntry("Deployment FAILED! Initializing ROLLBACK.");
	
						RollbackTask rollbackTask = new RollbackTask(bambooProcessService, capabilityContext);
						if (taskContext != null) {
							rollbackTask.execute(taskContext);
						}
						else {
							rollbackTask.execute(commonTaskContext);
						}
					}
					catch (Exception e) {
						buildLogger.addErrorLogEntry("Exception: "+e.getMessage());
					}
				}
				else {
					buildLogger.addErrorLogEntry("Deployment FAILED! No automatic rollback.");
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
