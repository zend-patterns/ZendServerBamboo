package com.zend.zendserver.bamboo;

import java.awt.Event;
import java.io.File;

import com.atlassian.bamboo.build.test.TestReportCollector;
import com.atlassian.bamboo.task.CommonTaskContext;
import com.atlassian.bamboo.task.TaskContext;
import com.atlassian.bamboo.task.TaskResultBuilder;

abstract class EventListener {
	protected TaskContext taskContext;
	protected CommonTaskContext commonTaskContext;
	protected TaskResultBuilder builder;
	protected TestReportCollector testReportCollector;
	protected File resultFile;
	
	public CommonTaskContext getCommonTaskContext() {
		return commonTaskContext;
	}

	public void setCommonTaskContext(CommonTaskContext commonTaskContext) {
		this.commonTaskContext = commonTaskContext;
	}

	public TaskContext getTaskContext() {
		return taskContext;
	}

	public void setTaskContext(TaskContext taskContext) {
		this.taskContext = taskContext;
	}

	public TaskResultBuilder getBuilder() {
		return builder;
	}

	public void setBuilder(TaskResultBuilder builder) {
		this.builder = builder;
	}

	public TestReportCollector getDeploymentReportCollector() {
		return testReportCollector;
	}

	public void setTestReportCollector(TestReportCollector testReportCollector) {
		this.testReportCollector = testReportCollector;
	}

	public File getResultFile() {
		return resultFile;
	}

	public void setResultFile(File resultFile) {
		this.resultFile = resultFile;
	}

	abstract void fireEvent(Event e) throws Exception;
}
