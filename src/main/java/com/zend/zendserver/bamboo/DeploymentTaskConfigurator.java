package com.zend.zendserver.bamboo;

import java.util.Map;

import com.atlassian.bamboo.collections.ActionParametersMap;
import com.atlassian.bamboo.task.AbstractTaskConfigurator;
import com.atlassian.bamboo.task.TaskDefinition;
import com.atlassian.bamboo.utils.error.ErrorCollection;
import com.atlassian.struts.TextProvider;

public class DeploymentTaskConfigurator extends AbstractTaskConfigurator {
	private Validator validator = new Validator();

    public Map<String, String> generateTaskConfigMap(final ActionParametersMap params, final TaskDefinition previousTaskDefinition)
    {
        final Map<String, String> context = super.generateTaskConfigMap(params, previousTaskDefinition);

        context.put("zs_url", params.getString("zs_url"));
        context.put("api_key", params.getString("api_key"));
        context.put("api_secret", params.getString("api_secret"));
        context.put("base_url", params.getString("base_url"));
        context.put("app_name", params.getString("app_name"));
        context.put("zsversion", params.getString("zsversion"));
        context.put("userparams", params.getString("userparams"));
        context.put("custom_options", params.getString("custom_options"));
        context.put("customzpk", params.getString("customzpk"));
        context.put("buildnr", params.getString("buildnr"));
        
        return context;
    }

    public void populateContextForCreate(final Map<String, Object> context)
    {
        super.populateContextForCreate(context);

        context.put("zs_url", "http://HOSTNAME:10081");
        context.put("api_key", "");
        context.put("api_secret", "");
        context.put("base_url", "");
        context.put("app_name", "myApp");
        context.put("zsversion", "6.3");
        context.put("userparams", "");
        context.put("custom_options", "");
        context.put("customzpk", "");
        context.put("buildnr", "${bamboo.buildNumber}");
    }

    public void populateContextForEdit(final Map<String, Object> context, final TaskDefinition taskDefinition)
    {
        super.populateContextForEdit(context, taskDefinition);

        context.put("zs_url", taskDefinition.getConfiguration().get("zs_url"));
        context.put("api_key", taskDefinition.getConfiguration().get("api_key"));
        context.put("api_secret", taskDefinition.getConfiguration().get("api_secret"));
        context.put("base_url", taskDefinition.getConfiguration().get("base_url"));
        context.put("app_name", taskDefinition.getConfiguration().get("app_name"));
        context.put("zsversion", taskDefinition.getConfiguration().get("zsversion"));
        context.put("userparams", taskDefinition.getConfiguration().get("userparams"));
        context.put("custom_options", taskDefinition.getConfiguration().get("custom_options"));
        context.put("customzpk", taskDefinition.getConfiguration().get("customzpk"));
        context.put("buildnr", taskDefinition.getConfiguration().get("buildnr"));
    }

    public void populateContextForView(final Map<String, Object> context, final TaskDefinition taskDefinition)
    {
        super.populateContextForView(context, taskDefinition);
        
        context.put("zs_url", taskDefinition.getConfiguration().get("zs_url"));
        context.put("api_key", taskDefinition.getConfiguration().get("api_key"));
        context.put("api_secret", taskDefinition.getConfiguration().get("api_secret"));
        context.put("base_url", taskDefinition.getConfiguration().get("base_url"));
        context.put("app_name", taskDefinition.getConfiguration().get("app_name"));
        context.put("zsversion", taskDefinition.getConfiguration().get("zsversion"));
        context.put("userparams", taskDefinition.getConfiguration().get("userparams"));
        context.put("custom_options", taskDefinition.getConfiguration().get("custom_options"));
        context.put("customzpk", taskDefinition.getConfiguration().get("customzpk"));
        context.put("buildnr", taskDefinition.getConfiguration().get("buildnr"));
    }

    public void validate(final ActionParametersMap params, final ErrorCollection errorCollection)
    {
        super.validate(params, errorCollection);

        this.validator.params = params;
        this.validator.errorCollection = errorCollection;
        
        validator.validateZsUrl();
        validator.validateApiKey();
        validator.validateApiSecret();
        validator.validateAppName();
        validator.validateBaseUrl();
        validator.validateZsVersion();
        validator.validateCustomZpkFilename();
    }
    
    public void setTextProvider(final TextProvider textProvider)
    {
        this.validator.textProvider = textProvider;
    }
}
