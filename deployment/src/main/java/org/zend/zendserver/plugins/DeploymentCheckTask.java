package org.zend.zendserver.plugins;

import java.io.File;
import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.build.test.TestCollationService;
import com.atlassian.bamboo.task.TaskContext;
import com.atlassian.bamboo.task.TaskException;
import com.atlassian.bamboo.task.TaskResult;
import com.atlassian.bamboo.task.TaskResultBuilder;
import com.atlassian.bamboo.task.TaskType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

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

		buildLogger.addErrorLogEntry("Preparing test runs.");
		String deployResultFilename = getDeployResultFilename(taskContext, buildLogger);
		String applicationId = getApplicationIdByDeployResult(deployResultFilename, buildLogger);
		
		ZendServerSDKCall call = new ZendServerSDKCall(buildLogger);
        
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
        		call.execute(
        				call.getApplicationGetDetailsCmd(
        						taskContext.getConfigurationMap(), 
        						applicationId, 
        						getApplicationGetDetailsFilePath(taskContext)));
        		
        	} catch (Exception e) {
        		buildLogger.addErrorLogEntry(e.getMessage());
        	}

        	testCollationService.collateTestResults(
        			taskContext, 
        			getApplicationGetDetailsFile(), 
        			new DeploymentCheckReportCollector(this, buildLogger));
        } while(isDeploying && it <= repeat);
        
        if (isDeploying && it >= repeat) {
        	buildLogger.addErrorLogEntry("Stop testing; deployment is still running. Aborting. ");
        	builder.failed();
        }
        
        if (builder.checkTestFailures().getTaskState().toString() == "Failed") {
        	try {
        		buildLogger.addErrorLogEntry("Deployment FAILED! Initializing ROLLBACK.");
        		
        		String applicationRollbackFile = "result-info.xml";
        		String applicationRollbackFilePath = taskContext.getWorkingDirectory().getAbsolutePath() + "/" + applicationRollbackFile;
        		
        		call.execute(
        				call.getApplicationRollbackCmd(
        						taskContext.getConfigurationMap(), 
        						applicationId, 
        						applicationRollbackFilePath));
        	}
        	catch (Exception e) {
        		buildLogger.addBuildLogEntry("Exception: "+e.getMessage());
        	}
        }

		return builder.build();
	}

	public void isDeploying(Boolean isDeploying) {
		this.isDeploying = isDeploying;
	}
	
	private String getDeployResultFilename(TaskContext tc, BuildLogger bl) {
		Object[] repositoryIds = tc.getBuildContext().getRelevantRepositoryIds().toArray();
        Long repositoryId = Long.valueOf(String.valueOf(repositoryIds[0]));
        
		String revision = tc.getBuildContext().getBuildChanges().getVcsRevisionKey(repositoryId);
        String buildNr = String.valueOf(tc.getBuildContext().getBuildNumber());
        
        File zsclientLogDir = new File(tc.getWorkingDirectory().getAbsolutePath() + "/zsclient-log");
		zsclientLogDir.mkdirs();
		
		String deploymentLogFile = zsclientLogDir.getAbsolutePath() + "/deploy-" + buildNr + "-" + revision + ".log";
		
		return deploymentLogFile;
	}
	
	public String getApplicationIdByDeployResult(String deployResultFilename, BuildLogger buildLogger) {
		String id = null;
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(new File(deployResultFilename));
			doc.getDocumentElement().normalize();

			Element responseData = (Element) doc.getElementsByTagName("responseData").item(0);
			
			Element applicationInfo = (Element) responseData.getElementsByTagName("applicationInfo").item(0);
			id = applicationInfo.getElementsByTagName("id").item(0).getChildNodes().item(0).getNodeValue();
		}
		catch (Exception e) {
			buildLogger.addErrorLogEntry("Exception: " + e.getMessage());
		}
		return id;
	}
	
	public String getApplicationGetDetailsFile() {
		return "applicationGetDetails.xml";
	}
	
	public String getApplicationGetDetailsFilePath(TaskContext taskContext) {
		return taskContext.getWorkingDirectory().getAbsolutePath() + "/" + getApplicationGetDetailsFile();
	}
}
