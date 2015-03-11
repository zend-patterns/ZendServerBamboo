package com.zend.zendserver.bamboo.Env;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.plan.artifact.ArtifactSubscriptionContext;
import com.atlassian.bamboo.task.TaskContext;

public class Build implements BuildEnv {
	private TaskContext taskContext;
	private BuildLogger logger;
	private String zpkPath;
	
	public Build(TaskContext taskContext) {
		this.taskContext = taskContext;
		this.logger = taskContext.getBuildLogger();
	}
	
	public String getRevision() {
		String revision = "norev";
		Object[] repositoryIds = taskContext.getBuildContext().getRelevantRepositoryIds().toArray();
		if (repositoryIds.length > 0) {
			Long repositoryId = Long.valueOf(String.valueOf(repositoryIds[0]));
			revision = taskContext.getBuildContext().getBuildChanges().getVcsRevisionKey(repositoryId);
			revision = revision.substring(0, 6);
		}
			
		return revision;
	}
	
	public String getBuildNr() {
        return String.valueOf(taskContext.getBuildContext().getBuildNumber());
	}
	
	public String getVersion() {
		return getBuildNr() + "-" + getRevision();
	}
	
	public String getWorkingDir() {
		return taskContext.getWorkingDirectory().getAbsolutePath();
	}
	
	private File prepareDir() {
		File zpkDir = new File(getWorkingDir() + "/zpk");
		zpkDir.mkdirs();
		return zpkDir;
	}
	
	public String getZpkFileName() throws Exception {
		return getBuildNr() + ".zpk";
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
		
		logger.addBuildLogEntry("Searching for ZPK file from Artifact Dependency");
		try {
			zpk = getZpkFromArtifactDependency();
			return getZpkAbsolutePath(zpk);
		}
		catch (Exception e) {
			logger.addBuildLogEntry(e.getMessage());
		}
		
		logger.addBuildLogEntry("Searching for ZPK file in working directory");
		try {
			zpk = getZpkFromTask();
			return getZpkAbsolutePath(zpk);
		}
		catch (Exception e) {
			logger.addBuildLogEntry(e.getMessage());
		}
		
		throw new Exception("Cannot find ZPK file. If you're executing the ZPK Packaging task and the Deployment task in two different plans, you have to check your custom ZPK file setting in the task definition or specify an Artifact Dependency");
	}
	
	public String getZpkDir() {
		File zpk;
		String customZpk = taskContext.getConfigurationMap().get("customzpk");
		if (!StringUtils.isEmpty(customZpk)) {
			zpk = new File(customZpk);
			return zpk.getParent();
		}
		else {
			zpk = prepareDir();
			return zpk.getAbsolutePath();
		}
	}
	
	private File getZpkFromArtifactDependency() throws Exception {
		if (!taskContext.getBuildContext().getArtifactContext().getSubscriptionContexts().iterator().hasNext()) {
			throw new Exception("No artifact dependency found.");
		}
		
		logger.addErrorLogEntry(taskContext.getBuildContext().getArtifactContext().getSubscriptionContexts().iterator().next().getDestinationPath());
		logger.addErrorLogEntry(getZpkFileName());
		String zpk = getWorkingDir()
				+ "/" + taskContext.getBuildContext().getArtifactContext().getSubscriptionContexts().iterator().next().getDestinationPath()
				+ "/" + getZpkFileName();
		return new File(zpk);
	}
	
	private File getZpkFromCustomDefinition() throws Exception {
		String customZpkPath = taskContext.getConfigurationMap().get("customzpk");
		if (StringUtils.isEmpty(customZpkPath)) {
			throw new Exception("No custom ZPK file specified.");
		}
		
		Boolean absolutePath = (String.valueOf(customZpkPath.charAt(0)).equals("/")) ? true : false;
		
		if (absolutePath) {
			return new File(customZpkPath);
		}
		
		return new File(getWorkingDir() + "/" + customZpkPath);
	}
	
	private File getZpkFromTask() throws Exception {
		File zpkDir = prepareDir();
		File zpk = new File(zpkDir.getAbsolutePath() + "/" + getZpkFileName());
		return zpk;
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
