package com.zend.zendserver.bamboo.Publisher;

import java.util.List;
import java.util.Map;

import com.atlassian.bamboo.build.Job;
import com.atlassian.bamboo.chains.Chain;
import com.atlassian.bamboo.chains.ChainStage;
import com.atlassian.bamboo.plan.Plan;
import com.atlassian.bamboo.plan.PlanKey;
import com.atlassian.bamboo.plan.PlanKeys;
import com.atlassian.bamboo.plan.PlanManager;
import com.atlassian.bamboo.task.TaskDefinition;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.Condition;
import com.zend.zendserver.bamboo.DeploymentTask;
import com.zend.zendserver.bamboo.StatisticsTask;

public class ZendServerViewCondition implements Condition {

    private PlanManager planManager;

    public void init(Map<String, String> map) throws PluginParseException {
    }

    public boolean shouldDisplay(Map<String, Object> context) {
        PlanKey planKey = PlanKeys.getPlanKey((String) context.get("planKey"));
        Chain planChain = (Chain) planManager.getPlanByKey(planKey);
        
        for (ChainStage chainStage : planChain.getStages())
        {
            for (Job  job: chainStage.getJobs())
            {
            	Plan plan = planManager.getPlanByKey(job.getPlanKey());
            	List<TaskDefinition> tasks = plan.getBuildDefinition().getTaskDefinitions();
                for(TaskDefinition task : tasks) {
                	if(
                    	task.getPluginKey().equals(DeploymentTask.KEY) ||
                    	task.getPluginKey().equals(StatisticsTask.KEY) 
                    ) {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }

    public void setPlanManager(PlanManager planManager) {
        this.planManager = planManager;
    }

}