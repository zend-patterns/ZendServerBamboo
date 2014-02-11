package org.zend.zendserver.bamboo.plugin.ZendServerSDK;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.configuration.ConfigurationMap;

public class Command {
	private ConfigurationMap configMap;

	public Command(ConfigurationMap configMap) {
		this.configMap = configMap;
	}
	
	public String getPackZpk(String workingDir, String zpkDir, String name, String version, String result) {
		String placeholder = "%s packZpk --folder=%s --destination=%s --name=%s --version=%s > %s";
        String cmd = String.format(placeholder,
        		configMap.get("zs_client_location"),
        		workingDir,
        		zpkDir,
        		name,
        		version,
        		result);
        
        return cmd;
	}
	
	public String getInstallApp (String zpkPath, String installAppFilePath) {
		String placeholder = "%s installApp --zpk %s --baseUri=%s --userAppName=%s --zsurl=%s --zskey=%s --zssecret=%s --zsversion=%s > %s";
        String cmd = String.format(placeholder,
        		configMap.get("zs_client_location"),
        		zpkPath,
        		configMap.get("base_url"),
        		configMap.get("app_name"),
        		configMap.get("url"),
        		configMap.get("api_key"),
        		configMap.get("api_secret"),
        		configMap.get("zsversion"),
        		installAppFilePath);
        return cmd;
	}
	
	public String getApplicationGetDetails(String applicationId, String applicationGetDetailsFilePath) {
        String placeholder = "%s applicationGetDetails --application=%s --zsurl=%s --zskey=%s --zssecret=%s --zsversion=%s > %s";
        String cmd = String.format(placeholder,
        		configMap.get("zs_client_location"),
        		applicationId,
        		configMap.get("url"),
        		configMap.get("api_key"),
        		configMap.get("api_secret"),
        		configMap.get("zsversion"),
        		applicationGetDetailsFilePath);
        
        return cmd;
	}
	
	public String getApplicationRollback(String applicationId, String applicationRollbackFilePath) {
		String placeholder = "%s applicationRollback --appId=%s --zsurl=%s --zskey=%s --zssecret=%s --zsversion=%s > %s";
		String cmd = String.format(placeholder,
				configMap.get("zs_client_location"),
				applicationId,
				configMap.get("url"),
				configMap.get("api_key"),
				configMap.get("api_secret"),
				configMap.get("zsversion"),
				applicationRollbackFilePath);
		
		return cmd;
	}
}
