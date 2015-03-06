package com.zend.zendserver.bamboo;

import java.util.Map;

import com.atlassian.bamboo.collections.ActionParametersMap;
import com.atlassian.bamboo.task.AbstractTaskConfigurator;
import com.atlassian.bamboo.task.TaskDefinition;
import com.atlassian.bamboo.utils.error.ErrorCollection;
import com.atlassian.struts.TextProvider;

public class PackagingTaskConfigurator extends AbstractTaskConfigurator {
	private Validator validator = new Validator();
	
    public Map<String, String> generateTaskConfigMap(final ActionParametersMap params, final TaskDefinition previousTaskDefinition)
    {
        final Map<String, String> context = super.generateTaskConfigMap(params, previousTaskDefinition);

        context.put("customzpk", params.getString("customzpk"));
        
        return context;
    }

    public void populateContextForCreate(final Map<String, Object> context)
    {
        super.populateContextForCreate(context);
        
        context.put("customzpk", "");
    }

    public void populateContextForEdit(final Map<String, Object> context, final TaskDefinition taskDefinition)
    {
        super.populateContextForEdit(context, taskDefinition);
        
        context.put("customzpk", taskDefinition.getConfiguration().get("customzpk"));
    }

    public void populateContextForView(final Map<String, Object> context, final TaskDefinition taskDefinition)
    {
        super.populateContextForView(context, taskDefinition);
        
        context.put("customzpk", taskDefinition.getConfiguration().get("customzpk"));
    }

    public void validate(final ActionParametersMap params, final ErrorCollection errorCollection)
    {
        super.validate(params, errorCollection);
        
        this.validator.params = params;
        this.validator.errorCollection = errorCollection;
        
        validator.validateCustomZpkFilename();
    }
    
    public void setTextProvider(final TextProvider textProvider)
    {
        this.validator.textProvider = textProvider;
    }
}
