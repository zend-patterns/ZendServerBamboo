package com.zend.zendserver.bamboo.Publisher;

import java.io.File;
import java.util.Map;

import com.atlassian.bamboo.build.PlanResultsAction;
import com.zend.zendserver.bamboo.Process.ApplicationGetDetailsProcess;

@SuppressWarnings("serial")
public class ZendServerDeploymentDetails extends PlanResultsAction {
    private boolean isJob;
    private String strTest;
    
    public String doExecute() throws Exception {
        String result = super.doExecute();
            this.isJob = true;
            strTest = "+++";

            strTest += "<br>---------------------------------<br>";
            Map<String, String> data = this.getResultsSummary().getCustomBuildData();
            
            /*
            for (Map.Entry<String, String> entry : data.entrySet()) {
        		//strTest += "<br>data " + entry.getKey() + " Value : " + entry.getValue();
        	}
        	*/
            
            strTest += " working.dir: " + data.get("working.directory");
            strTest += " buildId: " + this.getBuildNumber();
            String revision = data.get("repository.revision.number").substring(0, 6);
            strTest += " revision: " + revision;
            strTest += "<br>---------------------------------<br>";
            strTest += getReportFileNameApplicationGetDetails();
            strTest += "<br>---------------------------------<br>";
            
        return result;

    }
    
    public boolean getIsJob() {
        return this.isJob;
    }
    
    public String getStrTest() {
        return this.strTest;
    }

    private String getReportFileNameApplicationGetDetails() throws Exception {
    	String filename;
    	
    	Map<String, String> data = this.getResultsSummary().getCustomBuildData();
    	
    	filename = data.get("working.directory")
    			+ "/"
    			+ ApplicationGetDetailsProcess.OUTPUT_FILE_PREFIX
    			+ this.getBuildNumber()
    			+ "-"
    			+ data.get("repository.revision.number").substring(0, 6);
    	
    	int i = 0;
    	File file;
    	do {
    		file = new File(filename + "-" + ++i + ApplicationGetDetailsProcess.OUTPUT_FILE_SUFFIX); 
    	} while(file.isFile() && i < 100);
    	
    	file = new File(filename + "-" + (i-1) + ApplicationGetDetailsProcess.OUTPUT_FILE_SUFFIX); 
    	if (!file.isFile()) {
    		throw new Exception("Could not find file " + file.toString());
    	}
    	return file.toString();
    }
}