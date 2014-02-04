package org.zend.zendserver.plugins;

import java.util.Map;

import com.atlassian.bamboo.collections.ActionParametersMap;
import com.atlassian.bamboo.task.AbstractTaskConfigurator;
import com.atlassian.bamboo.task.TaskDefinition;
import com.atlassian.bamboo.utils.error.ErrorCollection;
import com.opensymphony.xwork.TextProvider;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DeploymentCheckTaskConfigurator extends AbstractTaskConfigurator {
	private TextProvider textProvider;

    @NotNull
    @Override
    public Map<String, String> generateTaskConfigMap(@NotNull final ActionParametersMap params, @Nullable final TaskDefinition previousTaskDefinition)
    {
        final Map<String, String> context = super.generateTaskConfigMap(params, previousTaskDefinition);

        context.put("zs_client_location", params.getString("zs_client_location"));
        context.put("api_key", params.getString("api_key"));
        context.put("api_secret", params.getString("api_secret"));
        context.put("url", params.getString("url"));
        context.put("package", params.getString("package"));
        context.put("base_url", params.getString("base_url"));
        context.put("app_name", params.getString("app_name"));
        context.put("zsversion", params.getString("zsversion"));
        context.put("params", params.getString("params"));
        
        return context;
    }

    @Override
    public void populateContextForCreate(@NotNull final Map<String, Object> context)
    {
        super.populateContextForCreate(context);

        context.put("zs_client_location", "/tmp/zs-client");
        context.put("api_key", "bamboo");
        context.put("api_secret", "34b41b8df84d8c4de36f5927760071b9daf4d1dab3cd26459fa34d35ed48e7a7");
        context.put("url", "http://10.11.12.68:10081");
        context.put("package", "abcxyz");
        context.put("base_url", "http://10.11.12.68");
        context.put("app_name", "myApp");
        context.put("zsversion", "6.1");
        context.put("params", "-abc test123");
    }

    @Override
    public void populateContextForEdit(@NotNull final Map<String, Object> context, @NotNull final TaskDefinition taskDefinition)
    {
        super.populateContextForEdit(context, taskDefinition);

        context.put("zs_client_location", taskDefinition.getConfiguration().get("zs_client_location"));
        context.put("api_key", taskDefinition.getConfiguration().get("api_key"));
        context.put("api_secret", taskDefinition.getConfiguration().get("api_secret"));
        context.put("url", taskDefinition.getConfiguration().get("url"));
        context.put("package", taskDefinition.getConfiguration().get("package"));
        context.put("base_url", taskDefinition.getConfiguration().get("base_url"));
        context.put("app_name", taskDefinition.getConfiguration().get("app_name"));
        context.put("zsversion", taskDefinition.getConfiguration().get("zsversion"));
        context.put("params", taskDefinition.getConfiguration().get("params"));
    }

    @Override
    public void populateContextForView(@NotNull final Map<String, Object> context, @NotNull final TaskDefinition taskDefinition)
    {
        super.populateContextForView(context, taskDefinition);
        context.put("zs_client_location", taskDefinition.getConfiguration().get("zs_client_location"));
        context.put("api_key", taskDefinition.getConfiguration().get("api_key"));
        context.put("api_secret", taskDefinition.getConfiguration().get("api_secret"));
        context.put("url", taskDefinition.getConfiguration().get("url"));
        context.put("package", taskDefinition.getConfiguration().get("package"));
        context.put("base_url", taskDefinition.getConfiguration().get("base_url"));
        context.put("app_name", taskDefinition.getConfiguration().get("app_name"));
        context.put("zsversion", taskDefinition.getConfiguration().get("zsversion"));
        context.put("params", taskDefinition.getConfiguration().get("params"));
    }

    @Override
    public void validate(@NotNull final ActionParametersMap params, @NotNull final ErrorCollection errorCollection)
    {
        super.validate(params, errorCollection);

        final String zsClientLocationValue = params.getString("zs_client_location");
        if (StringUtils.isEmpty(zsClientLocationValue))
        {
            errorCollection.addError("zs_client_location", textProvider.getText("com.zend.zendserver.plugins.zs_client_location.error"));
        }
        
        final String apiKeyValue = params.getString("api_key");
        if (StringUtils.isEmpty(apiKeyValue))
        {
            errorCollection.addError("api_key", textProvider.getText("com.zend.zendserver.plugins.api_key.error"));
        }
        
        final String apiSecretValue = params.getString("api_secret");
        if (StringUtils.isEmpty(apiSecretValue))
        {
            errorCollection.addError("api_secret", textProvider.getText("com.zend.zendserver.plugins.api_secret.error"));
        }
        
        final String urlValue = params.getString("url");
        if (StringUtils.isEmpty(urlValue))
        {
            errorCollection.addError("url", textProvider.getText("com.zend.zendserver.plugins.url.error"));
        }
        
        final String packageValue = params.getString("package");
        if (StringUtils.isEmpty(packageValue))
        {
            errorCollection.addError("package", textProvider.getText("com.zend.zendserver.plugins.package.error"));
        }
        
        final String baseUrlValue = params.getString("base_url");
        if (StringUtils.isEmpty(baseUrlValue))
        {
            errorCollection.addError("base_url", textProvider.getText("com.zend.zendserver.plugins.base_url.error"));
        }
        
        final String zsversionValue = params.getString("zsversion");
        if (StringUtils.isEmpty(zsversionValue))
        {
            errorCollection.addError("zsversion", textProvider.getText("com.zend.zendserver.plugins.zsversion.error"));
        }
    }

    public void setTextProvider(final TextProvider textProvider)
    {
        this.textProvider = textProvider;
    }
}
