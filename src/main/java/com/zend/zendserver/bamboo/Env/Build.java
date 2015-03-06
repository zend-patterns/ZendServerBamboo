package com.zend.zendserver.bamboo.Env;

import java.io.File;

import org.apache.commons.lang.StringUtils;

import com.atlassian.bamboo.task.TaskContext;
import com.atlassian.core.util.FileUtils;

public class Build implements BuildEnv {
	private TaskContext taskContext;
	
	public Build(TaskContext taskContext) {
		this.taskContext = taskContext;
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
		return getVersion() + ".zpk";
	}
	
	public String getZpkPath() throws Exception {
		String customZpk = taskContext.getConfigurationMap().get("customzpk");
		if (!StringUtils.isEmpty(customZpk)) {
			File zpk = new File(customZpk);
			if (!zpk.exists()) {
				throw new Exception("Cannot find a ZPK file under the given path [" + customZpk + "]. Please check your task confiiguration.");
			}
			return customZpk;
		}
		File zpkDir = prepareDir();
        return zpkDir.getAbsolutePath() + "/" + getZpkFileName();
	}
	
	public String getZpkDir() {
		File zpkDir = prepareDir();
        return zpkDir.getAbsolutePath();
	}
}
