package com.zend.zendserver.bamboo;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.atlassian.bamboo.collections.ActionParametersMap;
import com.atlassian.bamboo.task.AbstractTaskConfigurator;
import com.atlassian.bamboo.task.TaskDefinition;
import com.atlassian.bamboo.utils.error.ErrorCollection;
import com.atlassian.struts.TextProvider;

public class StatisticsTaskConfigurator extends AbstractTaskConfigurator {
	private Validator validator = new Validator();
	public ErrorCollection errorCollection;
	public TextProvider textProvider;

    public Map<String, String> generateTaskConfigMap(final ActionParametersMap params, final TaskDefinition previousTaskDefinition)
    {
        final Map<String, String> context = super.generateTaskConfigMap(params, previousTaskDefinition);

        context.put("zs_url", params.getString("zs_url"));
        context.put("api_key", params.getString("api_key"));
        context.put("api_secret", params.getString("api_secret"));
        context.put("app_name", params.getString("app_name"));
        context.put("zsversion", params.getString("zsversion"));
        
        return context;
    }

    public void populateContextForCreate(final Map<String, Object> context)
    {
        super.populateContextForCreate(context);

        context.put("zs_url", "http://HOSTNAME:10081");
        context.put("api_key", "");
        context.put("api_secret", "");
        context.put("app_name", "myApp");
        context.put("zsversion", "6.3");
    }

    public void populateContextForEdit(final Map<String, Object> context, final TaskDefinition taskDefinition)
    {
        super.populateContextForEdit(context, taskDefinition);
        
        context.put("zs_url", taskDefinition.getConfiguration().get("zs_url"));
        context.put("api_key", taskDefinition.getConfiguration().get("api_key"));
        context.put("api_secret", taskDefinition.getConfiguration().get("api_secret"));
        context.put("app_name", taskDefinition.getConfiguration().get("app_name"));
        context.put("zsversion", taskDefinition.getConfiguration().get("zsversion"));
    }

    public void populateContextForView(final Map<String, Object> context, final TaskDefinition taskDefinition)
    {
        super.populateContextForView(context, taskDefinition);
        
        context.put("zs_url", taskDefinition.getConfiguration().get("zs_url"));
        context.put("api_key", taskDefinition.getConfiguration().get("api_key"));
        context.put("api_secret", taskDefinition.getConfiguration().get("api_secret"));
        context.put("app_name", taskDefinition.getConfiguration().get("app_name"));
    }

    public void validate(final ActionParametersMap params, final ErrorCollection errorCollection)
    {
        super.validate(params, errorCollection);
        
        this.validator.params = params;
        this.validator.errorCollection = errorCollection;
        this.errorCollection = errorCollection;

        validator.validateZsUrl();
        validator.validateApiKey();
        validator.validateApiSecret();
        validator.validateAppName();
        validator.validateZsVersion();
    }

    public void setTextProvider(final TextProvider textProvider)
    {
        this.validator.textProvider = textProvider;
        this.textProvider = textProvider;
    }
}
