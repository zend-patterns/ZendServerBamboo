package com.zend.zendserver.bamboo;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.atlassian.bamboo.collections.ActionParametersMap;
import com.atlassian.bamboo.task.AbstractTaskConfigurator;
import com.atlassian.bamboo.task.TaskDefinition;
import com.atlassian.bamboo.utils.error.ErrorCollection;
import com.atlassian.struts.TextProvider;

public class DeploymentCheckTaskConfigurator extends AbstractTaskConfigurator {
	private Validator validator = new Validator();
	public ErrorCollection errorCollection;
	public TextProvider textProvider;

    public Map<String, String> generateTaskConfigMap(final ActionParametersMap params, final TaskDefinition previousTaskDefinition)
    {
        final Map<String, String> context = super.generateTaskConfigMap(params, previousTaskDefinition);

        context.put("zs_url", params.getString("zs_url"));
        context.put("api_key", params.getString("api_key"));
        context.put("api_secret", params.getString("api_secret"));
        context.put("package", params.getString("package"));
        context.put("base_url", params.getString("base_url"));
        context.put("app_name", params.getString("app_name"));
        context.put("zsversion", params.getString("zsversion"));
        context.put("custom_options", params.getString("custom_options"));
        context.put("retry", params.getString("retry"));
        context.put("waittime", params.getString("waittime"));
        context.put("rollback", params.getString("rollback"));
        
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
        context.put("custom_options", "");
        context.put("retry", "3");
        context.put("waittime", "10");
        context.put("rollback", true);
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
        context.put("custom_options", taskDefinition.getConfiguration().get("custom_options"));
        context.put("retry", taskDefinition.getConfiguration().get("retry"));
        context.put("waittime", taskDefinition.getConfiguration().get("waittime"));
        context.put("rollback", taskDefinition.getConfiguration().get("rollback"));
    }

    @Override
    public void populateContextForView(final Map<String, Object> context, final TaskDefinition taskDefinition)
    {
        super.populateContextForView(context, taskDefinition);
        
        context.put("zs_url", taskDefinition.getConfiguration().get("zs_url"));
        context.put("api_key", taskDefinition.getConfiguration().get("api_key"));
        context.put("api_secret", taskDefinition.getConfiguration().get("api_secret"));
        context.put("base_url", taskDefinition.getConfiguration().get("base_url"));
        context.put("app_name", taskDefinition.getConfiguration().get("app_name"));
        context.put("zsversion", taskDefinition.getConfiguration().get("zsversion"));
        context.put("custom_options", taskDefinition.getConfiguration().get("custom_options"));
        context.put("retry", taskDefinition.getConfiguration().get("retry"));
        context.put("waittime", taskDefinition.getConfiguration().get("waittime"));
        context.put("rollback", taskDefinition.getConfiguration().get("rollback"));
    }

    @Override
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
        validator.validateBaseUrl();
        validator.validateZsVersion();
        validateRetry(params.getString("retry"));
        validateWaittime(params.getString("waittime"));
        
    }
    
    void validateWaittime(String value) {
		if (StringUtils.isEmpty(value))
        {
            errorCollection.addError("waittime", textProvider.getText("com.zend.zendserver.plugins.waittime.required"));
        }
		
		try {
			int normalizedValue = Integer.parseInt(value);
			if (normalizedValue <= 0) {
				errorCollection.addError("waittime", textProvider.getText("com.zend.zendserver.plugins.waittime.zero"));
			}
			
			if (normalizedValue > 100) {
				errorCollection.addError("waittime", textProvider.getText("com.zend.zendserver.plugins.waittime.too_large"));
			}
		}
		catch (Exception e) {
			errorCollection.addError("waittime", textProvider.getText("com.zend.zendserver.plugins.waittime.error"));
		}
	}
    
    void validateRetry(String value) {
		if (StringUtils.isEmpty(value))
        {
            errorCollection.addError("retry", textProvider.getText("com.zend.zendserver.plugins.retry.required"));
        }
		
		try {
			int normalizedValue = Integer.parseInt(value);
			if (normalizedValue <= 0) {
				errorCollection.addError("retry", textProvider.getText("com.zend.zendserver.plugins.retry.zero"));
			}
			
			if (normalizedValue > 100) {
				errorCollection.addError("retry", textProvider.getText("com.zend.zendserver.plugins.retry.too_large"));
			}
		}
		catch (Exception e) {
			errorCollection.addError("retry", textProvider.getText("com.zend.zendserver.plugins.retry.error"));
		}
	}

    public void setTextProvider(final TextProvider textProvider)
    {
        this.validator.textProvider = textProvider;
        this.textProvider = textProvider;
    }
}
