package org.zend.zendserver.plugins;

import com.atlassian.sal.api.ApplicationProperties;

public class ZendServerImpl implements ZendServer
{
    private final ApplicationProperties applicationProperties;

    public ZendServerImpl(ApplicationProperties applicationProperties)
    {
        this.applicationProperties = applicationProperties;
    }

    public String getName()
    {
        if(null != applicationProperties)
        {
            return "Zend Server Bamboo Plugin:" + applicationProperties.getDisplayName();
        }
        
        return "Zend Server Bamboo Plugin";
    }
}