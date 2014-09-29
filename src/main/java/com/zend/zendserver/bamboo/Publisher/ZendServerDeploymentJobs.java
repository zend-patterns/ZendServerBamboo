package com.zend.zendserver.bamboo.Publisher;

import java.util.ArrayList;
import java.util.List;

import com.atlassian.bamboo.build.Job;
import com.atlassian.bamboo.build.PlanResultsAction;
import com.atlassian.bamboo.chains.Chain;
import com.atlassian.bamboo.chains.ChainStage;
import com.atlassian.bamboo.plan.PlanManager;

@SuppressWarnings("serial")
public class ZendServerDeploymentJobs extends PlanResultsAction {
    private boolean isJob;
    
    private String strTest;
    
    private String urlZsJobLog;
    private String urlZsJobDetails;
    
    public List<Integer> stageJobs = new ArrayList<Integer>();

    public String doExecute() throws Exception {
        String result = super.doExecute();
            this.isJob = true;
            strTest = "+++123+++";
            
            PlanManager planManager = this.getPlanManager();
            Chain myPlan = planManager.getPlanByKey(this.getBuildKey(), Chain.class);
            for (ChainStage chainStage : myPlan.getStages())
            {
                for (Job  job: chainStage.getJobs())
                {
                	buildUrlZsJobDetails(job.getBuildName(), job.getBuildKey());
                	buildUrlZsJobLog(job.getBuildKey());
                	strTest += "<br> this.getBuildKey(): " + this.getBuildKey();
                	strTest += "<br> job.getBuildKey(): " + job.getBuildKey();
                	strTest += "<br> job.getBuildName(): " + job.getBuildName();
                	
                	strTest += "(<a href=\"../../browse/" + this.getBuildKey() + "-" + job.getBuildKey() + "-" + this.getBuildNumber() + "/log\">Logs)</a>";
                }
            }

        return result;

    }

    public boolean getIsJob() {
        return this.isJob;
    }
    
    public String getStrTest() {
        return this.strTest;
    }
    
    private void buildUrlZsJobDetails(String jobBuildName, String jobBuildKey) {
    	String url = "";
    	
    	url += "<a href=\"ZendServerDeploymentDetails.action" 
    	    +  "?buildKey=" + this.getBuildKey() + "-" + jobBuildKey 
    	    +  "&buildNumber=" + this.getBuildNumber() + "\">" + jobBuildName + "</a>";
    	
    	this.urlZsJobDetails = url;
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
}