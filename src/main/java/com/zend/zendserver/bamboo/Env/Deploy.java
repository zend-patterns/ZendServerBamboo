package com.zend.zendserver.bamboo.Env;

import java.io.File;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.task.CommonTaskContext;
import com.atlassian.bamboo.task.TaskDefinition;

public class Deploy implements BuildEnv {
	public static final String ARTIFACT_DOWNLOADER_KEY = "com.atlassian.bamboo.plugins.bamboo-artifact-downloader-plugin:artifactdownloadertask";
	
	private CommonTaskContext taskContext;
	private String zpkPath;
	private BuildLogger logger;
	
	public Deploy(CommonTaskContext taskContext) {
		this.taskContext = taskContext;
		this.logger = taskContext.getBuildLogger();
	}
	
	public String getWorkingDir() {
		return taskContext.getWorkingDirectory().getAbsolutePath();
	}
	
	public String getBuildNr() {
        return taskContext.getConfigurationMap().get("buildnr");
	}
	
	public String getVersion() {
		return getBuildNr();
	}
	
	public String getZpkFileName() throws Exception {
		return getBuildNr() + ".zpk";
	}
	
	public String getZpkPath_old() throws Exception {
        return getWorkingDir() + "/" + getZpkFileName();
	}
	
	public String getZpkPath() throws Exception {
		if (StringUtils.isNotEmpty(zpkPath)) {
			return zpkPath;
		}
		File zpk;
		logger.addBuildLogEntry("Searching for ZPK file from custom definition");
		try {
			zpk = getZpkFromCustomDefinition();
			return getZpkAbsolutePath(zpk);
		}
		catch (Exception e) {
			logger.addBuildLogEntry(e.getMessage());
		}
		
		logger.addBuildLogEntry("Searching for ZPK file from Artifact Download");
		try {
			zpk = getZpkFromArtifactDownload();
			return getZpkAbsolutePath(zpk);
		}
		catch (Exception e) {
			logger.addBuildLogEntry(e.getMessage());
		}

		throw new Exception("Cannot find ZPK file. Please check your custom ZPK file settings or the configuration of the Artifact Download Task.");
	}
	
	public String getZpkDir() {
        return getWorkingDir();
	}
	
	private File getZpkFromCustomDefinition() throws Exception {
		String customZpkPath = taskContext.getConfigurationMap().get("customzpk");
		if (StringUtils.isEmpty(customZpkPath)) {
			throw new Exception("No custom ZPK file specified.");
		}
		
		File zpk = new File(customZpkPath); 
		
		if (zpk.isAbsolute()) {
			logger.addBuildLogEntry("Absolute path found");
			logger.addBuildLogEntry("Checking... ");
			return zpk;
		}
		
		return new File(getWorkingDir() + "/" + customZpkPath);
	}
	
	private File getZpkFromArtifactDownload() throws Exception {
		Iterator<TaskDefinition> taskDefinitionIterator = taskContext.getCommonContext().getTaskDefinitions().iterator();
		
		while (taskDefinitionIterator.hasNext()) {
			TaskDefinition taskDefinition = taskDefinitionIterator.next();
			if (!taskDefinition.getPluginKey().equals(ARTIFACT_DOWNLOADER_KEY)) continue;
			
			logger.addBuildLogEntry("Artifact Downloader Task found");
			String zpkDir = taskDefinition.getConfiguration().get("localPath_0").replace("${bamboo.buildNumber}", getBuildNr());
			logger.addBuildLogEntry("ZPK directory configuration found: " + zpkDir);
			
			String zpk = getWorkingDir()
					+ "/" + zpkDir
					+ "/" + getZpkFileName();
			
			return new File(zpk);
		}
		
		throw new Exception("No artifact downloader task found.");
	}
	
	private String getZpkAbsolutePath(File zpk) throws Exception{
		if (!zpk.exists()) {
			throw new Exception("Cannot find ZPK file according to detected path [" + zpk.getAbsolutePath() + "]");
		}
		
		logger.addBuildLogEntry("ZPK found: " + zpk.getAbsolutePath());
		zpkPath = zpk.getAbsolutePath();
		return zpk.getAbsolutePath();
	}
}
