package com.zend.zendserver.bamboo.Publisher;

import java.util.Map;

import com.atlassian.bamboo.plan.PlanManager;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.Condition;

public class ZendServerDeploymentStatisticsViewCondition implements Condition {

    public static final String BUILD_KEY = "buildKey";

    private PlanManager planManager;

    /**
     * Initializes the condition.
     * @param map
     * @throws PluginParseException 
     */
    @Override
    public void init(Map<String, String> map) throws PluginParseException {
    }

    /**
     * Used to select if the web-item will displayed to the user or not.
     * Returns true if the build has a blitz-curl task. Returns false otherwise.
     * @param context Condition context
     * @return true if build has a blitz-curl task
     */
    @Override
    public boolean shouldDisplay(Map<String, Object> context) {
    	/*
        String buildKey = (String) context.get(BUILD_KEY);
        if (buildKey == null) {
            return false;
        }
        Buildable build = planManager.getPlanByKeyIfOfType(buildKey, Buildable.class);
        if (build != null) {
            // get the list of tasks in the build
            List<TaskDefinition> tasks = build
                    .getBuildDefinition().getTaskDefinitions();
            for(TaskDefinition task : tasks) {
                //if there is a blitz-curl task
                if(task.getPluginKey().equals(CurlTask.KEY) ) {
                    
                    return true;
                }
            }
        }
        */
        return true;
    }

    /**
     * Injects the planManager
     * @param planManager 
     */
    public void setPlanManager(PlanManager planManager) {
        this.planManager = planManager;
    }

}