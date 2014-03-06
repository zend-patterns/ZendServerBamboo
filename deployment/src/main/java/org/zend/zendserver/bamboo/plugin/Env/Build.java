package org.zend.zendserver.bamboo.plugin.Env;

import java.io.File;

import com.atlassian.bamboo.task.TaskContext;

public class Build implements BuildEnv {
	private TaskContext taskContext;
	
	public Build(TaskContext taskContext) {
		this.taskContext = taskContext;
	}
	
	public String getRevision() {
		Object[] repositoryIds = taskContext.getBuildContext().getRelevantRepositoryIds().toArray();
        Long repositoryId = Long.valueOf(String.valueOf(repositoryIds[0]));
        
        String revision = taskContext.getBuildContext().getBuildChanges().getVcsRevisionKey(repositoryId);
        revision = revision.substring(0, 6);
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
		File zpkDir = prepareDir();
        return zpkDir.getAbsolutePath() + "/" + getZpkFileName();
	}
	
	public String getZpkDir() {
		File zpkDir = prepareDir();
        return zpkDir.getAbsolutePath();
	}
}
