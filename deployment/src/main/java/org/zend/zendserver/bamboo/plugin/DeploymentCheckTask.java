package org.zend.zendserver.bamboo.plugin;

import org.zend.zendserver.bamboo.plugin.Helper.Build;
import org.zend.zendserver.bamboo.plugin.TaskResult.ResultFile;
import org.zend.zendserver.bamboo.plugin.TaskResult.ResultParserInstallApp;
import org.zend.zendserver.bamboo.plugin.ZendServerSDK.Call;
import org.zend.zendserver.bamboo.plugin.ZendServerSDK.Command;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.build.test.TestCollationService;
import com.atlassian.bamboo.task.TaskContext;
import com.atlassian.bamboo.task.TaskException;
import com.atlassian.bamboo.task.TaskResult;
import com.atlassian.bamboo.task.TaskResultBuilder;
import com.atlassian.bamboo.task.TaskType;

public class DeploymentCheckTask implements TaskType {
	private TestCollationService testCollationService;
	private Boolean isDeploying; 
	
	public DeploymentCheckTask(TestCollationService testCollationService)
    {
        this.testCollationService = testCollationService;
    }

	@Override
	public TaskResult execute(final TaskContext taskContext)
			throws TaskException {

		final BuildLogger buildLogger = taskContext.getBuildLogger();

		try {
			Thread.sleep(5000);
		}
		catch (Exception e) {}

		TaskResultBuilder builder = TaskResultBuilder.create(taskContext);

		Command cmd = new Command(taskContext.getConfigurationMap());
		Call call = new Call(buildLogger);
		Build build = new Build(taskContext);
		ResultFile resultFile = new ResultFile(taskContext, build);

		buildLogger.addErrorLogEntry("Preparing test runs.");
		ResultParserInstallApp resultParserInstallApp;
		try {
			resultParserInstallApp = new ResultParserInstallApp(resultFile.getPathInstallApp(), buildLogger);
			String applicationId = resultParserInstallApp.getApplicationId();

			int repeat = 5;
			int wait = 5;
			int it = 0;
			do {
				isDeploying = false; 
				try {
					it++;
					buildLogger.addBuildLogEntry("Test iteration " + it + " of " + repeat + "...");
					if (it > 1) {
						Thread.sleep(wait * 1000);
					}
					call.execute(cmd.getApplicationGetDetails(applicationId, resultFile.getPathApplicationGetDetails()));

				} catch (Exception e) {
					buildLogger.addErrorLogEntry(e.getMessage());
				}

				testCollationService.collateTestResults(
						taskContext, 
						ResultFile.APPLICATION_GET_DETAILS, 
						new DeploymentCheckReportCollector(this, buildLogger));
			} while(isDeploying && it <= repeat);

			if (isDeploying && it >= repeat) {
				buildLogger.addErrorLogEntry("Stop testing; deployment is still running. Aborting. ");
				builder.failed();
			}

			if (builder.checkTestFailures().getTaskState().toString() == "Failed") {
				try {
					buildLogger.addErrorLogEntry("Deployment FAILED! Initializing ROLLBACK.");

					call.execute(cmd.getApplicationRollback(applicationId, resultFile.getPathApplicationRollback()));

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
