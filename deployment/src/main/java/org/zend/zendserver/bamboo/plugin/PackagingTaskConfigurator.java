package org.zend.zendserver.bamboo.plugin;

import java.util.Map;

import com.atlassian.bamboo.collections.ActionParametersMap;
import com.atlassian.bamboo.task.AbstractTaskConfigurator;
import com.atlassian.bamboo.task.TaskDefinition;
import com.atlassian.bamboo.utils.error.ErrorCollection;
import com.opensymphony.xwork.TextProvider;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PackagingTaskConfigurator extends AbstractTaskConfigurator {
	private TextProvider textProvider;

    @NotNull
    @Override
    public Map<String, String> generateTaskConfigMap(@NotNull final ActionParametersMap params, @Nullable final TaskDefinition previousTaskDefinition)
    {
        final Map<String, String> context = super.generateTaskConfigMap(params, previousTaskDefinition);

        context.put("zs_client_location", params.getString("zs_client_location"));
        
        return context;
    }

    @Override
    public void populateContextForCreate(@NotNull final Map<String, Object> context)
    {
        super.populateContextForCreate(context);

        context.put("zs_client_location", "/usr/local/bin/zs-client");
    }

    @Override
    public void populateContextForEdit(@NotNull final Map<String, Object> context, @NotNull final TaskDefinition taskDefinition)
    {
        super.populateContextForEdit(context, taskDefinition);

        context.put("zs_client_location", taskDefinition.getConfiguration().get("zs_client_location"));
    }

    @Override
    public void populateContextForView(@NotNull final Map<String, Object> context, @NotNull final TaskDefinition taskDefinition)
    {
        super.populateContextForView(context, taskDefinition);
        context.put("zs_client_location", taskDefinition.getConfiguration().get("zs_client_location"));
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
    }

    public void setTextProvider(final TextProvider textProvider)
    {
        this.textProvider = textProvider;
    }
}
