package com.zend.zendserver.bamboo.Env;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.plan.artifact.ArtifactDefinitionContext;
import com.atlassian.bamboo.plan.artifact.ArtifactSubscriptionContext;
import com.atlassian.bamboo.task.TaskContext;
import com.atlassian.bamboo.task.TaskDefinition;
import com.atlassian.bamboo.variable.VariableDefinitionContext;

public class Build implements BuildEnv {
	public static final String DEFAULT_ZPK_DIR = "/zpk";
	public static final String DEFAULT_ZPK_FILE_EXT = ".zpk";
	
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
	
	public String getZpkFileName() throws Exception {
		return getBuildNr() + DEFAULT_ZPK_FILE_EXT;
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
		
		logger.addBuildLogEntry("Searching for ZPK file from Artifact Definition");
		try {
			zpk = getZpkFromArtifactDefinition();
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
	
	public String getZpkDir() throws Exception {
		File zpk;
		logger.addBuildLogEntry("Searching for ZPK dir from custom definition");
		try {
			zpk = getZpkFromCustomDefinition();
			return getZpkAbsolutePath(zpk.getParentFile());
		}
		catch (Exception e) {
			logger.addBuildLogEntry(e.getMessage());
		}
		
		logger.addBuildLogEntry("Searching for ZPK dir from Artifact Definition");
		try {
			zpk = getZpkFromArtifactDefinition();
			return getZpkAbsolutePath(zpk.getParentFile());
		}
		catch (Exception e) {
			logger.addBuildLogEntry(e.getMessage());
		}
		
		zpk = new File(getWorkingDir() + "/" + DEFAULT_ZPK_DIR);
		zpk.mkdirs();
		logger.addBuildLogEntry("Using default zpk dir [" + DEFAULT_ZPK_DIR + "]");
		return getZpkAbsolutePath(zpk);
	}
	
	private File getZpkFromArtifactDefinition() throws Exception {
		Iterator<ArtifactDefinitionContext> artifactIterator = taskContext.getBuildContext().getArtifactContext().getDefinitionContexts().iterator();
		if (!artifactIterator.hasNext()) {
			throw new Exception("No artifact definition found.");
		}
		
		ArtifactDefinitionContext artifact = artifactIterator.next();
		String pattern = artifact.getCopyPattern().replace("${bamboo.buildNumber}", getBuildNr());
		String location = artifact.getLocation().replace("${bamboo.buildNumber}", getBuildNr());
		String zpkDir = getWorkingDir() + "/" + location;
		String zpk = getWorkingDir() + "/" + location + "/" + pattern;

		new File(zpkDir).mkdirs();
		
		return new File(zpk);
	}
	
	private File getZpkFromArtifactDependency() throws Exception {
		 Iterator<ArtifactSubscriptionContext> artifactIterator = taskContext.getBuildContext().getArtifactContext().getSubscriptionContexts().iterator();
		
		if (!artifactIterator.hasNext()) {
			throw new Exception("No artifact dependency found.");
		}
		
		ArtifactSubscriptionContext artifact = artifactIterator.next();
		String destinationPath = artifact.getDestinationPath().replace("${bamboo.buildNumber}", getBuildNr());
		
		String zpk = getWorkingDir()
				+ "/" + destinationPath
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
		File zpkDir = new File(getWorkingDir() + "/" + DEFAULT_ZPK_DIR);
		zpkDir.mkdirs();
		logger.addBuildLogEntry("Using default zpk dir [" + DEFAULT_ZPK_DIR + "]");
		File zpk = new File(zpkDir.getAbsolutePath() + "/" + getZpkFileName());
		return zpk;
	}
	
	private String getZpkAbsolutePath(File zpk) throws Exception{
		if (!zpk.exists()) {
			throw new Exception("Cannot find ZPK file according to detected path [" + zpk.getAbsolutePath() + "]");
		}
		
		logger.addBuildLogEntry("File found: " + zpk.getAbsolutePath());
		return zpk.getAbsolutePath();
	}
	
	public long getProcessTimeout() {
		long processTimeout;
		try {
			VariableDefinitionContext processTimeoutContext = taskContext.getBuildContext().getVariableContext().getDefinitions().get("processTimeout");
			processTimeout = Long.parseLong(processTimeoutContext.getValue()) * 1000;
		}
		catch (Exception e) {
			processTimeout = 60 * 1000;
		}
		return processTimeout;
		
		
	}
}
