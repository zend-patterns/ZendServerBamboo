package org.zend.zendserver.bamboo.plugin;   

import com.atlassian.bamboo.v2.build.agent.capability.AbstractMultipleExecutableCapabilityTypeModule; 
import com.google.common.collect.Lists; 
import java.util.Arrays; 
import java.util.List; 

class ZendServerWebApiClientCapability extends AbstractMultipleExecutableCapabilityTypeModule { 
	public static final String DEFAULT_EXECUTABLE_PATH_PHAR = "/usr/local/bin/zs-client";
	public static final String DEFAULT_EXECUTABLE_PATH_PHP = "/usr/bin/php /usr/local/share/ZendServerSDK/bin/zs-client.php";
	
	public ZendServerWebApiClientCapability() { }  

	public String getExecutableKindKey() { 
		return "zswa-clientExecutableKind"; 
	}   
	
	public String getMandatoryCapabilityKey() { 
		return "system.zswa-client.executable"; 
	}
	
	public List<String> getAdditionalCapabilityKeys() { 
		return Lists.newArrayList(
			new String[] { "system.zswa-client.executable.php" }
		); 
	}
	
	public List<String> getDefaultWindowPaths() { 
		return Arrays.asList(
			new String[] { 
				"C:\\" }
			); 
	}   
	
	public String getExecutableFilename() { 
		return "zswa-client"; 
	}  
	
	public String getCapabilityUndefinedKey() { 
		return "agent.capability.type.zswa-client.error.undefinedExecutable"; 
	}   
	
	public String getCapabilityKindUndefinedKey() { 
		return "agent.capability.type.zswa-client.error.undefinedExecutableKind"; 
	}   
	
	public String getExecutableDescription(String key) { 
		return getText(
			(new StringBuilder())
				.append("agent.capability.type.")
				.append(key)
				.append(".description")
				.toString(), 
			new String[] { "" }
			); 
	}
}