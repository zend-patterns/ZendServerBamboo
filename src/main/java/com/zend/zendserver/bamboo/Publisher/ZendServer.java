package com.zend.zendserver.bamboo.Publisher;

import java.util.ArrayList;
import java.util.List;

import com.atlassian.bamboo.build.Job;
import com.atlassian.bamboo.build.PlanResultsAction;
import com.atlassian.bamboo.chains.Chain;
import com.atlassian.bamboo.chains.ChainStage;
import com.atlassian.bamboo.plan.Plan;
import com.atlassian.bamboo.plan.PlanKey;
import com.atlassian.bamboo.plan.PlanKeys;
import com.atlassian.bamboo.plan.PlanManager;
import com.atlassian.bamboo.task.TaskDefinition;
import com.zend.zendserver.bamboo.DeploymentTask;
import com.zend.zendserver.bamboo.StatisticsTask;

@SuppressWarnings("serial")
public class ZendServer extends PlanResultsAction {
    private String urlZsJobLog = "";
    private String urlZsJobDetails = "";
    private String urlZsStatistics = "";
    
    public List<Integer> stageJobs = new ArrayList<Integer>();

    public String doExecute() throws Exception {
        String result = super.doExecute();
        	
        PlanKey planKey = PlanKeys.getPlanKey(getBuildKey());
        Chain planChain = (Chain) planManager.getPlanByKey(planKey);
        
        for (ChainStage chainStage : planChain.getStages())
        {
            for (Job  job: chainStage.getJobs())
            {
            	Plan plan = planManager.getPlanByKey(job.getPlanKey());
            	List<TaskDefinition> tasks = plan.getBuildDefinition().getTaskDefinitions();
                for(TaskDefinition task : tasks) {
                	if(task.getPluginKey().equals(DeploymentTask.KEY)) {
                		buildUrlZsJobDetails(job.getBuildName(), job.getBuildKey());
                    }
                    else if (task.getPluginKey().equals(StatisticsTask.KEY)) {
                    	buildUrlZsStatistics(job.getBuildName(), job.getBuildKey());
                    }
                	buildUrlZsJobLog(job.getBuildKey());
                }
            }
        }

        return result;

    }
    
    private void buildUrlZsJobDetails(String jobBuildName, String jobBuildKey) {
    	String url = "";
    	
    	url += "<a href=\"ZendServerDeploymentDetails.action" 
    	    +  "?buildKey=" + this.getBuildKey() + "-" + jobBuildKey 
    	    +  "&buildNumber=" + this.getBuildNumber() + "\">Deployment details for job '" + jobBuildName + "'</a>";
    	
    	this.urlZsJobDetails = url;
    }
    
    private void buildUrlZsStatistics(String jobBuildName, String jobBuildKey) {
    	String url = "";
    	
    	url += "<a href=\"ZendServerStatistics.action" 
    	    +  "?buildKey=" + this.getBuildKey() + "-" + jobBuildKey 
    	    +  "&buildNumber=" + this.getBuildNumber() + "\">Statistics for job '" + jobBuildName + "'</a>";
    	
    	this.setUrlZsStatistics(url);
    }
    
    public String getUrlZsJobDetails() {
    	return this.urlZsJobDetails;
    }
    
    private void buildUrlZsJobLog(String jobBuildKey) {
    	String url = "";
    	url += "<a href=\"../../browse/" + this.getBuildKey() + "-" + jobBuildKey + "-" 
    	    +  this.getBuildNumber() + "/log\">Logs</a>";
    	
    	this.urlZsJobLog = url;
    }
    
    public String getUrlZsJobLog() {
    	return this.urlZsJobLog;
    }

	public String getUrlZsStatistics() {
		return urlZsStatistics;
	}

	public void setUrlZsStatistics(String urlZsStatistics) {
		this.urlZsStatistics = urlZsStatistics;
	}
	
	public Boolean hasUrlZsJobLog() {
		return !urlZsJobLog.isEmpty();
	}
	
	public Boolean hasUrlZsStatistics() {
		return !urlZsStatistics.isEmpty();
	}
	
	public Boolean hasUrlZsJobDetails() {
		return !urlZsJobDetails.isEmpty();
	}
}