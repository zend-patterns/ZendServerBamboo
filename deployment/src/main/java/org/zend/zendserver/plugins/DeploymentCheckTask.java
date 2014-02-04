package org.zend.zendserver.plugins;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.Arrays;

import javax.xml.xpath.*;

import org.xml.sax.InputSource;

import com.atlassian.bamboo.bandana.PlanAwareBandanaContext;
import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.build.test.TestCollationService;
import com.atlassian.bamboo.process.ExternalProcessBuilder;
import com.atlassian.bamboo.process.ProcessService;
import com.atlassian.bamboo.task.TaskContext;
import com.atlassian.bamboo.task.TaskException;
import com.atlassian.bamboo.task.TaskResult;
import com.atlassian.bamboo.task.TaskResultBuilder;
import com.atlassian.bamboo.task.TaskType;
import com.atlassian.bamboo.v2.build.BuildContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.utils.process.ExternalProcess;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DeploymentCheckTask implements TaskType {
	private BandanaManager bandanaManager;
	private TestCollationService testCollationService;
	private Boolean isDeploying; 
	
	public DeploymentCheckTask(TestCollationService testCollationService)
    {
        this.testCollationService = testCollationService;
    }

	@Override
	public TaskResult execute(final TaskContext taskContext)
			throws TaskException {
		
		final BuildLogger buildLogger = taskContext.getBuildLogger();

		buildLogger.addBuildLogEntry("*** Sleep start ");
		try {
			Thread.sleep(5000);
		}
		catch (Exception e) {}
		buildLogger.addBuildLogEntry("*** Sleep end ");
		
		TaskResultBuilder builder = TaskResultBuilder.create(taskContext);

		
		//Object deploymentLogFile = bandanaManager.getValue(PlanAwareBandanaContext.GLOBAL_CONTEXT, "org.zend.zendserver.plugins:deploymentLogFile");
		//buildLogger.addBuildLogEntry("*** deploymentLogFile "+String.valueOf(deploymentLogFile));

		/*
		String deploymentLogFile = "/home/jan/workspaces/sandboxx/deployment/target/bamboo/home/xml-data/build-dir/TES-TEST-TEST/zsclient-log/deploy-46-0ba7ff8ef83d9828e4944d9f0971e2290ea3244b.log";
		
		try {

			File stocks = new File(deploymentLogFile);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(stocks);
			doc.getDocumentElement().normalize();

			buildLogger.addBuildLogEntry("root of xml file" + doc.getDocumentElement().getNodeName());
			Node responseData = doc.getElementsByTagName("responseData").item(0);

			Element responseDataElement = (Element) responseData;
			buildLogger.addBuildLogEntry("+++ appName: " + getValue("appName", responseDataElement));
			buildLogger.addBuildLogEntry("+++ Status: " + getValue("status", responseDataElement)); 


		} catch (Exception ex) {
			ex.printStackTrace();
		}
		*/
		String deployResultFilename = getDeployResultFilename(taskContext, buildLogger);
		String applicationId = getApplicationIdByDeployResult(deployResultFilename, buildLogger);
		
		ProcessBuilder pb;
		String deploymentLogFile = "result.xml";
		//String placeholder = "%s getSystemInfo --zsurl=%s --zskey=%s --zssecret=%s --zsversion=%s";
        String placeholder = "%s applicationGetDetails --application=%s --zsurl=%s --zskey=%s --zssecret=%s --zsversion=%s > /home/jan/workspaces/sandboxx/deployment/target/bamboo/home/xml-data/build-dir/TES-TEST-TEST/result.xml";
        String cmd = String.format(placeholder,
        		taskContext.getConfigurationMap().get("zs_client_location"),
        		applicationId,
        		taskContext.getConfigurationMap().get("url"),
        		taskContext.getConfigurationMap().get("api_key"),
        		taskContext.getConfigurationMap().get("api_secret"),
        		taskContext.getConfigurationMap().get("zsversion"));
        
        buildLogger.addBuildLogEntry("*** cmd "+cmd);
        
        
        Process process;
        int repeat = 5;
        int wait = 5;
        int it = 0;
        do {
        	isDeploying = false; 
        	try {
        		it++;
        		buildLogger.addBuildLogEntry("************* it: " + it);
        		if (it > 1) {
        			Thread.sleep(wait * 1000);
        		}
        		pb = new ProcessBuilder("bash", "-c", cmd);
        		process = pb.start();
        		process.waitFor();
        		/*
			BufferedReader bri = new BufferedReader(new InputStreamReader(process.getInputStream()));

			String line;
	        PrintWriter writer = new PrintWriter(deploymentLogFile, "UTF-8");
			while ((line = bri.readLine()) != null) {
				writer.println(line);
	        }
			writer.close();

			if (process.exitValue() == 0) {
				builder.success();
			}
        		 */
        	} catch (IOException e) {
        		buildLogger.addBuildLogEntry(e.getMessage());
        	} catch (InterruptedException e) {
        		buildLogger.addBuildLogEntry(e.getMessage());
        	} catch (Exception e) {
        		buildLogger.addBuildLogEntry(e.getMessage());
        	}

        	testCollationService.collateTestResults(taskContext, "result.xml", new DeploymentCheckReportCollector(this, buildLogger));
        	//buildLogger.addBuildLogEntry("*** TASK STATE: " + builder.checkTestFailures().getTaskState().toString());
        	buildLogger.addBuildLogEntry("*** TASK isDeploying: " + String.valueOf(isDeploying));
        } while(isDeploying && it < repeat);
        
        if (isDeploying && it >= repeat) {
        	buildLogger.addBuildLogEntry("*** isDeploying && repeat ");
        	builder.failed();
        }
        
        buildLogger.addBuildLogEntry("***+++ " +  builder.toString());
        
        if (builder.checkTestFailures().getTaskState().toString() == "Failed") {
        	try {
        		buildLogger.addBuildLogEntry("*** FAILED ");
        		placeholder = "%s applicationRollback --appId=%s --zsurl=%s --zskey=%s --zssecret=%s --zsversion=%s > /home/jan/workspaces/sandboxx/deployment/target/bamboo/home/xml-data/build-dir/TES-TEST-TEST/result2.xml";
        		cmd = String.format(placeholder,
        				taskContext.getConfigurationMap().get("zs_client_location"),
        				applicationId,
        				taskContext.getConfigurationMap().get("url"),
        				taskContext.getConfigurationMap().get("api_key"),
        				taskContext.getConfigurationMap().get("api_secret"),
        				taskContext.getConfigurationMap().get("zsversion"));
        		pb = new ProcessBuilder("bash", "-c", cmd);
        		process = pb.start();
        		process.waitFor();
        		buildLogger.addBuildLogEntry("***+++ cmd " +  cmd);
        	}
        	catch (Exception e) {
        		buildLogger.addBuildLogEntry("+++ EX: "+e.getMessage());
        	}
        }
        else {
        	buildLogger.addBuildLogEntry("*** SUCESS ");
        }

		return builder.build();
	}

	private static String getValue(String tag, Element element) {
		NodeList nodes = element.getElementsByTagName(tag).item(0).getChildNodes();
		Node node = (Node) nodes.item(0);
		return node.getNodeValue();
	}

	public void isDeploying(Boolean isDeploying) {
		this.isDeploying = isDeploying;
	}
	
	public String getDeployResultFilename(TaskContext tc, BuildLogger bl) {
		Object[] repositoryIds = tc.getBuildContext().getRelevantRepositoryIds().toArray();
        Long repositoryId = Long.valueOf(String.valueOf(repositoryIds[0]));
        
		String revision = tc.getBuildContext().getBuildChanges().getVcsRevisionKey(repositoryId);
        String buildNr = String.valueOf(tc.getBuildContext().getBuildNumber());
        
        bl.addBuildLogEntry("*** buildNr: " + buildNr + " - revision: " + revision);
        //String zpkName = buildNr + "-" + revision + ".zpk";
       
        File zsclientLogDir = new File(tc.getWorkingDirectory().getAbsolutePath() + "/zsclient-log");
		zsclientLogDir.mkdirs();
		
		String deploymentLogFile = zsclientLogDir.getAbsolutePath() + "/deploy-" + buildNr + "-" + revision + ".log";
		
		return deploymentLogFile;
	}
	
	public String getApplicationIdByDeployResult(String deployResultFilename, BuildLogger buildLogger) {
		String id = null;
		try {
			buildLogger.addErrorLogEntry("~~~ 002: " + deployResultFilename);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(new File(deployResultFilename));
			doc.getDocumentElement().normalize();

			Element responseData = (Element) doc.getElementsByTagName("responseData").item(0);
			buildLogger.addBuildLogEntry("~~~ 001: " + responseData.getNodeName()); 
			
			Element applicationInfo = (Element) responseData.getElementsByTagName("applicationInfo").item(0);
			buildLogger.addBuildLogEntry("~~~ 001: " + applicationInfo.getNodeName()); 
			//Element responseDataElement = (Element) responseData;
			id = applicationInfo.getElementsByTagName("id").item(0).getChildNodes().item(0).getNodeValue();
			buildLogger.addErrorLogEntry("~~~ 002: " + id);
		}
		catch (Exception e) {
			buildLogger.addErrorLogEntry(e.getMessage());
			buildLogger.addBuildLogEntry(e.getMessage());
		}
		return id;
	}
}
