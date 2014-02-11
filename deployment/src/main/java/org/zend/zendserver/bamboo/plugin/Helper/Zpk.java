package org.zend.zendserver.bamboo.plugin.Helper;

import java.io.File;


import com.atlassian.bamboo.task.TaskContext;

public class Zpk {
	private TaskContext taskContext;
	private Build build;
	
	public Zpk(TaskContext taskContext, Build build) {
		this.taskContext = taskContext;
		this.build = build;
	}
	
	private File prepareDir() {
		File zpkDir = new File(taskContext.getWorkingDirectory().getAbsolutePath() + "/zpk");
		zpkDir.mkdirs();
		return zpkDir;
	}
	
	public String getFileName() {
		return build.getBuildNr() + "-" + build.getRevision() + ".zpk";
	}
	
	public String getPath() {
		File zpkDir = prepareDir();
        return zpkDir.getAbsolutePath() + "/" + getFileName();
	}
	
	public String getDir() {
		File zpkDir = prepareDir();
        return zpkDir.getAbsolutePath();
	}
	
	public String createVersion() {
		return build.getBuildNr() + "." + build.getRevision(); 
	}
}
