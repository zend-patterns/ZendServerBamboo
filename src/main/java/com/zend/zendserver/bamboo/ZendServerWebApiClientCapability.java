package com.zend.zendserver.bamboo;   

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.atlassian.bamboo.v2.build.agent.capability.AbstractMultipleExecutableCapabilityTypeModule;
import com.google.common.collect.Lists;

public class ZendServerWebApiClientCapability extends AbstractMultipleExecutableCapabilityTypeModule { 
	public static final String DEFAULT_EXECUTABLE_PATH_PHAR = "/usr/local/bin/zs-client";
	public static final String DEFAULT_EXECUTABLE_PATH_PHP = "/usr/bin/php /usr/local/share/ZendServerSDK/bin/zs-client.php";
	
	public static final String ERROR_UNDEFINED_EXECUTABLE = "The Zend Server Web API Client executable has not been defined";
	public static final String ERROR_UNDEFINED_EXECUTABLE_KIND = "Please select a valid executable kind";
	
	public static final String EXECUTABLE_KIND_KEY = "zswa-clientExecutableKind";
	public static final String EXECUTABLE_DESCRIPTION_PHAR = "Download standalone PHAR file from <a href=\"https://github.com/zendtech/ZendServerSDK/raw/master/bin/zs-client.phar\">here</a> and make it executable.<br />E.g. <b>/usr/local/bin/zs-client.phar</b>";
	public static final String EXECUTABLE_DESCRIPTION_PHP = "Clone https://github.com/zend-patterns/ZendServerSDK.git and specify php executable to run bin/zs-client.php .</br>E.g. <b>/usr/bin/php /usr/local/share/ZendServerSDK/bin/zs-client.php</b>";
	
	public static final String EXECUTABLE_TYPE_PHAR = "Zend Server PHAR Web API Client ";
	public static final String EXECUTABLE_TYPE_PHP = "Zend Server PHP Web API Client ";
	
	public static final String EXECUTABLE_KEY_PHAR = "com.zend.zendserver.plugins.capability.zswa.executable.phar";
	public static final String EXECUTABLE_KEY_PHP = "com.zend.zendserver.plugins.capability.zswa.executable.php";
	
	private Map<String, String> description;
	private Map<String, String> defaultPaths;
	private Map<String, String> executableTypes;
	private Map<String, String> labels;
	
	public ZendServerWebApiClientCapability() { 
		description = new HashMap<String, String>();
		description.put(EXECUTABLE_KEY_PHAR, EXECUTABLE_DESCRIPTION_PHAR);
		description.put(EXECUTABLE_KEY_PHP, EXECUTABLE_DESCRIPTION_PHP);
		
		defaultPaths = new HashMap<String, String>();
		defaultPaths.put(EXECUTABLE_KEY_PHAR, DEFAULT_EXECUTABLE_PATH_PHAR);
		defaultPaths.put(EXECUTABLE_KEY_PHP, DEFAULT_EXECUTABLE_PATH_PHP);
		
		executableTypes = new HashMap<String, String>();
		executableTypes.put(EXECUTABLE_KEY_PHAR, EXECUTABLE_TYPE_PHAR);
		executableTypes.put(EXECUTABLE_KEY_PHP, EXECUTABLE_TYPE_PHP);
		
		labels = new HashMap<String, String>();
		labels.put(EXECUTABLE_KEY_PHAR, EXECUTABLE_TYPE_PHAR);
		labels.put(EXECUTABLE_KEY_PHP, EXECUTABLE_TYPE_PHP);
	}  

	public String getExecutableKindKey() { 
		return EXECUTABLE_KIND_KEY; 
	}   
	
	public String getMandatoryCapabilityKey() { 
		return EXECUTABLE_KEY_PHAR; 
	}
	
	public List<String> getAdditionalCapabilityKeys() { 
		return Lists.newArrayList(
			new String[] { EXECUTABLE_KEY_PHP }
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
		return ERROR_UNDEFINED_EXECUTABLE; 
	}   
	
	public String getCapabilityKindUndefinedKey() { 
		return ERROR_UNDEFINED_EXECUTABLE_KIND; 
	}  
	
	public String getExecutableKey() { 
		return ERROR_UNDEFINED_EXECUTABLE_KIND; 
	}
	
	public String getExtraInfo(String key) {
		return defaultPaths.get(key);
	}
	
	public String getExecutableDescription(String key) {
		return description.get(key);
	}
	
	public Map<String, String> getExecutableTypes() {
		return executableTypes;
	}
	
	public String getLabel(String key) {
		return labels.get(key);
	}
}