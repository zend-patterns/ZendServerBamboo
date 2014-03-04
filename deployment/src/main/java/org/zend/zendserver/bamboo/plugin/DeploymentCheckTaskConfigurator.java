package org.zend.zendserver.bamboo.plugin;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.atlassian.bamboo.collections.ActionParametersMap;
import com.atlassian.bamboo.task.AbstractTaskConfigurator;
import com.atlassian.bamboo.task.TaskDefinition;
import com.atlassian.bamboo.utils.error.ErrorCollection;
import com.opensymphony.xwork.TextProvider;

public class DeploymentCheckTaskConfigurator extends AbstractTaskConfigurator {
	private Validator validator = new Validator();
	public ErrorCollection errorCollection;
	public TextProvider textProvider;

    @NotNull
    @Override
    public Map<String, String> generateTaskConfigMap(@NotNull final ActionParametersMap params, @Nullable final TaskDefinition previousTaskDefinition)
    {
        final Map<String, String> context = super.generateTaskConfigMap(params, previousTaskDefinition);

        context.put("zs_url", params.getString("zs_url"));
        context.put("api_key", params.getString("api_key"));
        context.put("api_secret", params.getString("api_secret"));
        context.put("package", params.getString("package"));
        context.put("base_url", params.getString("base_url"));
        context.put("app_name", params.getString("app_name"));
        context.put("zsversion", params.getString("zsversion"));
        context.put("retry", params.getString("retry"));
        context.put("waittime", params.getString("waittime"));
        
        return context;
    }

    @Override
    public void populateContextForCreate(@NotNull final Map<String, Object> context)
    {
        super.populateContextForCreate(context);

        context.put("zs_url", "http://10.11.12.68:10081");
        context.put("api_key", "bamboo");
        context.put("api_secret", "34b41b8df84d8c4de36f5927760071b9daf4d1dab3cd26459fa34d35ed48e7a7");
        context.put("base_url", "http://10.11.12.68");
        context.put("app_name", "myApp");
        context.put("zsversion", "6.1");
        context.put("retry", "5");
        context.put("waittime", "10");
    }

    @Override
    public void populateContextForEdit(@NotNull final Map<String, Object> context, @NotNull final TaskDefinition taskDefinition)
    {
        super.populateContextForEdit(context, taskDefinition);
        
        context.put("zs_url", taskDefinition.getConfiguration().get("zs_url"));
        context.put("api_key", taskDefinition.getConfiguration().get("api_key"));
        context.put("api_secret", taskDefinition.getConfiguration().get("api_secret"));
        context.put("base_url", taskDefinition.getConfiguration().get("base_url"));
        context.put("app_name", taskDefinition.getConfiguration().get("app_name"));
        context.put("zsversion", taskDefinition.getConfiguration().get("zsversion"));
        context.put("retry", taskDefinition.getConfiguration().get("retry"));
        context.put("waittime", taskDefinition.getConfiguration().get("waittime"));
    }

    @Override
    public void populateContextForView(@NotNull final Map<String, Object> context, @NotNull final TaskDefinition taskDefinition)
    {
        super.populateContextForView(context, taskDefinition);
        
        context.put("zs_url", taskDefinition.getConfiguration().get("zs_url"));
        context.put("api_key", taskDefinition.getConfiguration().get("api_key"));
        context.put("api_secret", taskDefinition.getConfiguration().get("api_secret"));
        context.put("base_url", taskDefinition.getConfiguration().get("base_url"));
        context.put("app_name", taskDefinition.getConfiguration().get("app_name"));
        context.put("retry", taskDefinition.getConfiguration().get("retry"));
        context.put("waittime", taskDefinition.getConfiguration().get("waittime"));
    }

    @Override
    public void validate(@NotNull final ActionParametersMap params, @NotNull final ErrorCollection errorCollection)
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
