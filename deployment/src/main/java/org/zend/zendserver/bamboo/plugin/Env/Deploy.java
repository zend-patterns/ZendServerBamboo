package org.zend.zendserver.bamboo.plugin.Env;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.task.CommonTaskContext;
import com.atlassian.bamboo.task.TaskContext;

public class Deploy implements BuildEnv {
	private CommonTaskContext taskContext;
	
	public Deploy(CommonTaskContext taskContext) {
		this.taskContext = taskContext;
	}
	
	public String getWorkingDir() {
		return taskContext.getWorkingDirectory().getAbsolutePath();
	}
	
	public String getVersion() throws Exception {
		String displayName = taskContext.getCommonContext().getDisplayName();
		Pattern pattern = Pattern.compile("'(.*?)'");
		Matcher matcher = pattern.matcher(displayName);
		if (!matcher.find())
		{
		    throw new Exception("Couldn't find revision number in display name");
		}
		
		return matcher.group(1);
	}
	
	public String getZpkFileName() throws Exception {
		String displayName = taskContext.getCommonContext().getDisplayName();
		Pattern pattern = Pattern.compile("'.*-(.*?)'");
		Matcher matcher = pattern.matcher(displayName);
		if (!matcher.find())
		{
		    throw new Exception("Couldn't find revision number in display name");
		}
		
		String buildNr = matcher.group(1);
		String zpkFile = null;
		
		File dir = new File(getWorkingDir());
		File[] list = dir.listFiles();
       	for (File file : list)
       	{
       		if (file.getName().indexOf(buildNr) == 0) {
       			zpkFile = file.getName();
       			break;
       		}
        }
		return zpkFile;
	}
	
	public String getZpkPath() throws Exception {
        return getWorkingDir() + "/" + getZpkFileName();
	}
	
	public String getZpkDir() {
        return getWorkingDir();
	}
}
