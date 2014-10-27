package com.zend.zendserver.bamboo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.atlassian.bamboo.configuration.ConfigurationMap;
import com.atlassian.bamboo.task.CommonTaskContext;
import com.atlassian.bamboo.task.CommonTaskType;
import com.atlassian.bamboo.task.TaskContext;
import com.atlassian.bamboo.task.TaskException;
import com.atlassian.bamboo.task.TaskResult;
import com.atlassian.bamboo.task.TaskResultBuilder;
import com.atlassian.bamboo.task.TaskType;
import com.atlassian.bamboo.v2.build.agent.capability.CapabilityContext;
import com.atlassian.bamboo.variable.CustomVariableContext;
import com.zend.zendserver.bamboo.Env.Build;
import com.zend.zendserver.bamboo.Env.BuildEnv;
import com.zend.zendserver.bamboo.Env.Deploy;
import com.zend.zendserver.bamboo.Process.DeploymentProcess;
import com.zend.zendserver.bamboo.Process.MonitorGetIssuesListPredefinedFilterProcess;
import com.zend.zendserver.bamboo.Process.ProcessHandler;
import com.zend.zendserver.bamboo.TaskResult.ResultParserApplicationGetStatus;
import com.zend.zendserver.bamboo.TaskResult.ResultParserDeploymentCheck;
import com.zend.zendserver.bamboo.TaskResult.ResultParserInstallApp;
import com.zend.zendserver.bamboo.TaskResult.ResultParserMonitorGetIssuesListPredefinedFilter;

public class StatisticsTask extends BaseTask implements CommonTaskType, TaskType {
	
	public static final String KEY = "org.zend.zendserver.bamboo.plugin.zendserver:ZendServerStatisticsTask";
	
	public static final String OUTPUT_FILE_KEY_ISSUES_LIST_BEFORE_DEPLOY = "task.report.issues_list_before_deploy";
	public static final String OUTPUT_FILE_KEY_ISSUES_LIST_AFTER_DEPLOY = "task.report.issues_list_after_deploy";
	public static final String TIME_DEPLOY = "task.deploytime";
	public static final String TIME_START_PERIOD_BEFORE_DEPLOY = "task.deploytime.period_before_deploy_start";
	public static final String TIME_END_PERIOD_BEFORE_DEPLOY = "task.deploytime.period_before_deploy_end";
	public static final String TIME_START_PERIOD_AFTER_DEPLOY = "task.deploytime.period_after_deploy_start";
	public static final String TIME_END_PERIOD_AFTER_DEPLOY = "task.deploytime.period_after_deploy_end";
	public static final String DATE_FORMATTER_PATTERN = "yyyy-MM-dd HH:mm";
	public static final String PUBLISHER_DISPLAY = "task.publisher.statistics.display";
	
	private long deploymentTimstamp;
	private long nowTimestamp;
	private long comparisonPeriodTimestamp;
	
	private Date deploymentDate;
	private Date nowDate;
	private Date comparisonPeriodDate;
	
    public StatisticsTask(
    		com.atlassian.bamboo.process.ProcessService processService,
			CapabilityContext capabilityContext) {
		super(processService, capabilityContext);
	}
    
    public TaskResult execute(CommonTaskContext commonTaskContext)
			throws TaskException {
		
		TaskResultBuilder builder = TaskResultBuilder.newBuilder(commonTaskContext);
		buildLogger = commonTaskContext.getBuildLogger();
		buildLogger.addBuildLogEntry("Preparing Statistics (in Bamboo-Deploy context).");
		
		Deploy deploy = new Deploy(commonTaskContext);
		init(commonTaskContext, deploy);
		
		return doExecute(commonTaskContext, deploy, builder,commonTaskContext.getConfigurationMap()); 
	}

	public TaskResult execute(TaskContext taskContext) throws TaskException {
		TaskResultBuilder builder = TaskResultBuilder.newBuilder(taskContext);
		buildLogger = taskContext.getBuildLogger();
		buildLogger.addBuildLogEntry("Preparing Statistics (in Bamboo-Build context).");
		
		Build build = new Build(taskContext);
		init(taskContext, build);
		
		return doExecute((CommonTaskContext) taskContext, build, builder, taskContext.getConfigurationMap());
	}
	
	public TaskResult doExecute(CommonTaskContext taskContext, BuildEnv buildEnv, TaskResultBuilder builder, ConfigurationMap configMap) {
		builder.success();
		
		try {
			ProcessHandler applicationGetStatus = processHandlerService.applicationGetStatus();
			applicationGetStatus.execute();
			
			ResultParserApplicationGetStatus statusParser;
			String filename = applicationGetStatus.getOutputFilename();
			statusParser = new ResultParserApplicationGetStatus(filename);
			
			String applicationId = statusParser.getApplicationId(configMap.get("app_name"));
			String deploymentTime = statusParser.getDeploymentTime(configMap.get("app_name"));
			
			buildDates(deploymentTime);
			
			ProcessHandler issuesListPeriodAfterDeploy = processHandlerService.monitorGetIssuesListPredefinedFilter(deploymentTimstamp, nowTimestamp, false);
			issuesListPeriodAfterDeploy.execute();
			ResultParserMonitorGetIssuesListPredefinedFilter parserPeriodAfterDeploy = new ResultParserMonitorGetIssuesListPredefinedFilter(issuesListPeriodAfterDeploy.getOutputFilename());
			
			ProcessHandler issuesListPeriodBeforeDeploy = processHandlerService.monitorGetIssuesListPredefinedFilter(comparisonPeriodTimestamp, deploymentTimstamp, true);
			issuesListPeriodBeforeDeploy.execute();
			ResultParserMonitorGetIssuesListPredefinedFilter parserPeriodBeforeDeploy = new ResultParserMonitorGetIssuesListPredefinedFilter(issuesListPeriodBeforeDeploy.getOutputFilename());
			
			if (issuesListPeriodAfterDeploy.getBuildEnv() instanceof Build) {
				final Map<String, String> customBuildData = errorCollatorListener.getTaskContext().getBuildContext().getBuildResult().getCustomBuildData();
	            customBuildData.put(OUTPUT_FILE_KEY_ISSUES_LIST_BEFORE_DEPLOY, issuesListPeriodBeforeDeploy.getOutputFilename());
	            customBuildData.put(OUTPUT_FILE_KEY_ISSUES_LIST_AFTER_DEPLOY, issuesListPeriodAfterDeploy.getOutputFilename());
	            
	            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMATTER_PATTERN);
	            customBuildData.put(TIME_DEPLOY, sdf.format(deploymentDate));
	            customBuildData.put(TIME_START_PERIOD_BEFORE_DEPLOY, sdf.format(comparisonPeriodDate));
	            customBuildData.put(TIME_END_PERIOD_BEFORE_DEPLOY, sdf.format(deploymentDate));
	            customBuildData.put(TIME_START_PERIOD_AFTER_DEPLOY, sdf.format(deploymentDate));
	            customBuildData.put(TIME_END_PERIOD_AFTER_DEPLOY, sdf.format(nowDate));
	            
	            customBuildData.put(PUBLISHER_DISPLAY, "1");
			}
		}
		catch (Exception e) {
			buildLogger.addErrorLogEntry("Exception: " + e.getMessage());
			builder.failed();
		}
		return builder.build();
	}
	
	private Date getDateByString(String date) throws ParseException {
		String pattern = "(\\d{4})-(\\d{2})-(\\d{2})T(\\d{2}):(\\d{2}):(\\d{2})\\+.*";
		date = date.replaceAll(pattern, "$1-$2-$3 $4:$5");
		
		return new SimpleDateFormat(DATE_FORMATTER_PATTERN).parse(date);
	}
	
	private void buildTimestamps() {
		nowTimestamp = System.currentTimeMillis() / 1000L;
		deploymentTimstamp = deploymentDate.getTime() / 1000;
		long timestampDiff = nowTimestamp - deploymentTimstamp;
		
		comparisonPeriodTimestamp = deploymentTimstamp - timestampDiff;
	}
	
	private void buildDates(String deploymentTime) throws ParseException {
		deploymentDate = getDateByString(deploymentTime);
		
		buildTimestamps();
		
		comparisonPeriodDate = new Date(comparisonPeriodTimestamp * 1000L);
		nowDate = new Date(nowTimestamp * 1000L);
	}
}
