package org.zend.zendserver.bamboo.plugin.TaskResult;

import java.io.File;

import org.zend.zendserver.bamboo.plugin.Helper.Build;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.task.TaskContext;

public class ResultFile {
	public static final String INSTALL_APP_TPL = "installApp-#BUILDNR#-#REVISION#.xml";
	public static final String PACK_ZPK_TPL = "packZpk-#BUILDNR#-#REVISION#.log";
	public static final String APPLICATION_GET_DETAILS = "applicationGetDetails.xml";
	public static final String APPLICATION_ROLLBACK = "applicationRollback.xml";
	
	public static final String ZPK_PATH = "zpk";
	public static final String ZSCLIENT_LOG_PATH = "zsclient-log";
	
	private String wd = ""; 
	
	private Build build;
	
	public ResultFile(TaskContext taskContext, Build build) {
		this.build = build;
		this.wd = taskContext.getWorkingDirectory().getAbsolutePath() + "/";
	}
	
	public String getPathApplicationGetDetails() {
		return wd + APPLICATION_GET_DETAILS;
	}
	
	public String getPathApplicationRollback() { 
		return wd + APPLICATION_ROLLBACK;
	}
	
	public String getPathPackZpk() {
		File zsclientLogDir = new File(wd + ZSCLIENT_LOG_PATH);
		zsclientLogDir.mkdirs();
		
		String packZpk = PACK_ZPK_TPL.replace("#BUILDNR#", build.getBuildNr());
		packZpk = packZpk.replace("#REVISION#", build.getRevision());
		
		return zsclientLogDir.getAbsolutePath() + "/" + packZpk;
	}
	
	public String getPathInstallApp() {
        File zsclientLogDir = new File(wd + ZSCLIENT_LOG_PATH);
		zsclientLogDir.mkdirs();
		
		String installApp = INSTALL_APP_TPL.replace("#BUILDNR#", build.getBuildNr());
		installApp = installApp.replace("#REVISION#", build.getRevision());
		
		return zsclientLogDir.getAbsolutePath() + "/" + installApp;
	}
}
