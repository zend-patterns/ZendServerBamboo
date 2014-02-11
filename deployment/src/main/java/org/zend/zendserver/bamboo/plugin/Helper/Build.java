package org.zend.zendserver.bamboo.plugin.Helper;

import com.atlassian.bamboo.task.TaskContext;

public class Build {
	private TaskContext taskContext;
	
	public Build(TaskContext taskContext) {
		this.taskContext = taskContext;
	}
	
	public String getRevision() {
		Object[] repositoryIds = taskContext.getBuildContext().getRelevantRepositoryIds().toArray();
        Long repositoryId = Long.valueOf(String.valueOf(repositoryIds[0]));
        
        return taskContext.getBuildContext().getBuildChanges().getVcsRevisionKey(repositoryId);
	}
	
	public String getBuildNr() {
        return String.valueOf(taskContext.getBuildContext().getBuildNumber());
	}
}
