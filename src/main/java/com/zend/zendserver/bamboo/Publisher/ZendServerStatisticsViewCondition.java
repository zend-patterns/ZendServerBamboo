package com.zend.zendserver.bamboo.Publisher;

import java.util.List;
import java.util.Map;

import com.atlassian.bamboo.plan.Plan;
import com.atlassian.bamboo.plan.PlanKey;
import com.atlassian.bamboo.plan.PlanKeys;
import com.atlassian.bamboo.plan.PlanManager;
import com.atlassian.bamboo.task.TaskDefinition;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.Condition;
import com.zend.zendserver.bamboo.StatisticsTask;

public class ZendServerStatisticsViewCondition implements Condition {

    public static final String BUILD_KEY = "buildKey";

    private PlanManager planManager;

    public void init(Map<String, String> map) throws PluginParseException {
    }

    public boolean shouldDisplay(Map<String, Object> context) {
        String buildKey = (String) context.get(BUILD_KEY);
        
        PlanKey planKey = PlanKeys.getPlanKey(buildKey);
        Plan plan = planManager.getPlanByKey(planKey);
       
        List<TaskDefinition> tasks = plan.getBuildDefinition().getTaskDefinitions();
        for(TaskDefinition task : tasks) {
            if(task.getPluginKey().equals(StatisticsTask.KEY) ) {
                return true;
            }
        }

        return false;
    }

    public void setPlanManager(PlanManager planManager) {
        this.planManager = planManager;
    }

}