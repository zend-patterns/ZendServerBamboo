package org.zend.zendserver.bamboo.plugin;

import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

import com.atlassian.bamboo.collections.ActionParametersMap;
import com.atlassian.bamboo.utils.error.ErrorCollection;
import com.opensymphony.xwork.TextProvider;

public class Validator {
	public TextProvider textProvider;
	public ActionParametersMap params;
	public ErrorCollection errorCollection;
	
	public static final String MIN_ZS_VERSION = "6.0";

	void validateZsVersion() {
		try {
			final String zsversion = params.getString("zsversion");
			if (StringUtils.isEmpty(zsversion))
			{
				errorCollection.addError("zsversion", textProvider.getText("com.zend.zendserver.plugins.zsversion.error"));
				throw new Exception("com.zend.zendserver.plugins.zsversion.error");
			}
			
	    	DefaultArtifactVersion minVersion = new DefaultArtifactVersion(MIN_ZS_VERSION);
	    	DefaultArtifactVersion zsVersion = new DefaultArtifactVersion(zsversion);
	    	
	    	if (zsVersion.compareTo(minVersion) == -1) {
	            errorCollection.addError("zsversion", textProvider.getText("com.zend.zendserver.plugins.zsversion.too_old_zs"));
	        }
	    } catch (Exception e) {
	    	errorCollection.addError("zsversion", textProvider.getText("com.zend.zendserver.plugins.zsversion.nan"));
	    }
	}

	void validateBaseUrl() {
		try {
			
			final String urlValue = params.getString("base_url");
			if (StringUtils.isEmpty(urlValue))
			{
				throw new Exception(textProvider.getText("com.zend.zendserver.plugins.base_url.error"));// errorCollection.addError("zs_url", textProvider.getText("com.zend.zendserver.plugins.zs_url.error"));
			}
			
			if (urlValue.substring(0, 4).equals("http")) {
	    
				URL url = new URL(urlValue);
				URLConnection conn = url.openConnection();
				conn.setConnectTimeout(10000);
				conn.connect();
			}
			else if (!urlValue.substring(0, 1).equals("/")) {
				throw new Exception(textProvider.getText("com.zend.zendserver.plugins.base_url.invalid"));
			}
	    } catch (Exception e) {
	    	errorCollection.addError("base_url", e.getMessage());
	    }
	}

	void validateZsUrl() {
		try {
			
			final String urlValue = params.getString("zs_url");
			if (StringUtils.isEmpty(urlValue))
			{
				throw new Exception(textProvider.getText("com.zend.zendserver.plugins.zs_url.error"));// errorCollection.addError("zs_url", textProvider.getText("com.zend.zendserver.plugins.zs_url.error"));
			}
	    
	        URL url = new URL(urlValue);
	        URLConnection conn = url.openConnection();
	        conn.setConnectTimeout(10000);
	        conn.connect();
	    } catch (Exception e) {
	    	errorCollection.addError("zs_url", e.getMessage());
	    }
	    
	}

	void validateApiKey() {
		final String apiKeyValue = params.getString("api_key");
	    if (StringUtils.isEmpty(apiKeyValue))
	    {
	        errorCollection.addError("api_key", textProvider.getText("com.zend.zendserver.plugins.api_key.error"));
	    }
	}

	void validateApiSecret() {
		final String apiSecretValue = params.getString("api_secret");
	    if (StringUtils.isEmpty(apiSecretValue) ||
	    		!StringUtils.isAlphanumeric(apiSecretValue) ||
	    		StringUtils.length(apiSecretValue) != 64)
	    {
	        errorCollection.addError("api_secret", textProvider.getText("com.zend.zendserver.plugins.api_secret.error"));
	    }
	}

	void validateAppName() {
		final String appNameValue = params.getString("app_name");
	    if (StringUtils.isEmpty(appNameValue))
	    {
	        errorCollection.addError("app_name", textProvider.getText("com.zend.zendserver.plugins.app_name.error"));
	    }
	}
	
	void validateNumberGreaterZero(String value, String fieldIdentifier) {
		if (StringUtils.isEmpty(value))
        {
            errorCollection.addError(fieldIdentifier, textProvider.getText("com.zend.zendserver.plugins." + fieldIdentifier + ".required"));
        }
		
		try {
			int normalizedValue = Integer.parseInt(value);
			if (normalizedValue <= 0) {
				errorCollection.addError(fieldIdentifier, textProvider.getText("com.zend.zendserver.plugins." + fieldIdentifier + ".zero"));
			}
			
			if (normalizedValue > 100) {
				errorCollection.addError(fieldIdentifier, textProvider.getText("com.zend.zendserver.plugins." + fieldIdentifier + ".too_large"));
			}
		}
		catch (Exception e) {
			errorCollection.addError(fieldIdentifier, textProvider.getText("com.zend.zendserver.plugins." + fieldIdentifier + ".error"));
		}
		
		
	}
}