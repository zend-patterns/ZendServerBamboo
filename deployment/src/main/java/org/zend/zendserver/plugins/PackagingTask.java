package org.zend.zendserver.plugins;

import java.io.File;
import java.util.Arrays;

import javax.xml.parsers.DocumentBuilderFactory;

import net.sf.hibernate.mapping.Set;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.process.ExternalProcessBuilder;
import com.atlassian.bamboo.process.ProcessService;
import com.atlassian.bamboo.task.TaskContext;
import com.atlassian.bamboo.task.TaskException;
import com.atlassian.bamboo.task.TaskResult;
import com.atlassian.bamboo.task.TaskResultBuilder;
import com.atlassian.bamboo.task.TaskType;
import com.atlassian.utils.process.ExternalProcess;

public class PackagingTask implements TaskType {

	private final ProcessService processService;
	
	public PackagingTask(final ProcessService processService) {
		this.processService = processService;
	}
	
	@Override
	public TaskResult execute(final TaskContext taskContext)
			throws TaskException {
		
		TaskResultBuilder builder = TaskResultBuilder.create(taskContext);
		
		String wdPath = taskContext.getWorkingDirectory().getAbsolutePath();
		File zpkDir = new File(wdPath + "/zpk");
		zpkDir.mkdirs();
		
		final BuildLogger buildLogger = taskContext.getBuildLogger();
		
 
        // @todo check for existing repos
        Object[] repositoryIds = taskContext.getBuildContext().getRelevantRepositoryIds().toArray();
        Long repositoryId = Long.valueOf(String.valueOf(repositoryIds[0]));
        /*
        for (Object temp : repositoryIds){
        	buildLogger.addBuildLogEntry("*** repoId: " + String.valueOf(temp));
        	repositoryId = Long.valueOf(temp.toString());
        }
        */
        
        String revision = taskContext.getBuildContext().getBuildChanges().getVcsRevisionKey(repositoryId);
        String buildNr = String.valueOf(taskContext.getBuildContext().getBuildNumber());
        
        buildLogger.addBuildLogEntry("*** buildNr: " + buildNr + " - revision: " + revision);
        
        final String zs_client_location = taskContext.getConfigurationMap().get("zs_client_location");
		final String zpkName = buildNr + "-" + revision + ".zpk";
		String placeholder = "%s packZpk --folder=" + wdPath + " --destination=" + wdPath + "/zpk --name=" + zpkName;
        String cmd = String.format(placeholder,
        		zs_client_location);
        
        ExternalProcess process = processService.createProcess(taskContext,
        		new ExternalProcessBuilder()
        		.commandFromString(cmd)
        		.env("DEBUG", "1")
        		.workingDirectory(taskContext.getWorkingDirectory()));
        
        process.execute();
 
        return builder.checkReturnCode(process, 0).build();
	}

}
