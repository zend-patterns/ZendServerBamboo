package org.zend.zendserver.plugins;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;

import com.atlassian.bamboo.bandana.PlanAwareBandanaContext;
import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.process.ExternalProcessBuilder;
import com.atlassian.bamboo.process.ProcessService;
import com.atlassian.bamboo.task.TaskContext;
import com.atlassian.bamboo.task.TaskException;
import com.atlassian.bamboo.task.TaskResult;
import com.atlassian.bamboo.task.TaskResultBuilder;
import com.atlassian.bamboo.task.TaskType;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.utils.process.ExternalProcess;

public class DeploymentTask implements TaskType {
	
	private BandanaManager bandanaManager;
	
	@Override
	public TaskResult execute(final TaskContext taskContext)
			throws TaskException {
		
		TaskResultBuilder builder = TaskResultBuilder.create(taskContext);
		builder.failed();
		
		try {
			Thread.sleep(5000);
		}
		catch (Exception e) {}
		
		final BuildLogger buildLogger = taskContext.getBuildLogger();
		final String zs_client_location = taskContext.getConfigurationMap().get("zs_client_location");
		final String api_key = taskContext.getConfigurationMap().get("api_key");
		final String api_secret = taskContext.getConfigurationMap().get("api_secret");
		final String url = taskContext.getConfigurationMap().get("url");
		final String zpk = taskContext.getConfigurationMap().get("package");
		final String base_url = taskContext.getConfigurationMap().get("base_url");
		final String app_name = taskContext.getConfigurationMap().get("app_name");
		final String zsversion = taskContext.getConfigurationMap().get("zsversion");
		final String params = taskContext.getConfigurationMap().get("params");
		
		Object[] repositoryIds = taskContext.getBuildContext().getRelevantRepositoryIds().toArray();
        Long repositoryId = Long.valueOf(String.valueOf(repositoryIds[0]));
        
        String revision = taskContext.getBuildContext().getBuildChanges().getVcsRevisionKey(repositoryId);
        String buildNr = String.valueOf(taskContext.getBuildContext().getBuildNumber());
        
        buildLogger.addBuildLogEntry("*** buildNr: " + buildNr + " - revision: " + revision);
        String zpkName = buildNr + "-" + revision + ".zpk";
        String zpkPath = taskContext.getWorkingDirectory().getAbsolutePath() + "/zpk/" + zpkName;
        
        buildLogger.addBuildLogEntry("*** zs_client_location "+zs_client_location);
        buildLogger.addBuildLogEntry("*** api_key "+api_key);
        buildLogger.addBuildLogEntry("*** api_secret "+api_secret);
        buildLogger.addBuildLogEntry("*** url "+url);
        buildLogger.addBuildLogEntry("*** zpk "+zpk);
        buildLogger.addBuildLogEntry("*** base_url "+base_url);
        buildLogger.addBuildLogEntry("*** app_name "+app_name);
        buildLogger.addBuildLogEntry("*** zsversion "+zsversion);
        buildLogger.addBuildLogEntry("*** params "+params);
        buildLogger.addBuildLogEntry("*** zs_client_location "+zs_client_location);
        buildLogger.addBuildLogEntry("*** zpkName "+zpkName);
        
		File zsclientLogDir = new File(taskContext.getWorkingDirectory().getAbsolutePath() + "/zsclient-log");
		zsclientLogDir.mkdirs();
		
		String deploymentLogFile = zsclientLogDir.getAbsolutePath() + "/deploy-" + buildNr + "-" + revision + ".log";
		
        //String placeholder = "%s getSystemInfo --zsurl=%s --zskey=%s --zssecret=%s --zsversion=%s";
        String placeholder = "%s installApp --zpk %s --baseUri=%s --userAppName=%s --zsurl=%s --zskey=%s --zssecret=%s --zsversion=%s ";
        String cmd = String.format(placeholder,
        		zs_client_location,
        		zpkPath,
        		base_url,
        		app_name,
        		url,
        		api_key,
        		api_secret,
        		zsversion);
        
        buildLogger.addBuildLogEntry("*** cmd: "+cmd);
        
        ProcessBuilder pb = new ProcessBuilder("bash", "-c", cmd);
        Process process;
		try {
			process = pb.start();
			process.waitFor();
			BufferedReader bri = new BufferedReader(new InputStreamReader(process.getInputStream()));
			
			String line;
	        PrintWriter writer = new PrintWriter(deploymentLogFile, "UTF-8");
			while ((line = bri.readLine()) != null) {
				writer.println(line);
	        }
			writer.close();
			
			if (process.exitValue() == 0) {
				builder.success();
			}
			
		} catch (IOException e) {
			buildLogger.addBuildLogEntry(e.getMessage());
		} catch (InterruptedException e) {
			buildLogger.addBuildLogEntry(e.getMessage());
		}  
   
		/**
        ExternalProcess process = processService.createProcess(taskContext,
        		new ExternalProcessBuilder()
        		.commandFromString(cmd).
        		.workingDirectory(taskContext.getWorkingDirectory()));
        
        process.execute();
 		*/
        return builder.build();
	}

}
