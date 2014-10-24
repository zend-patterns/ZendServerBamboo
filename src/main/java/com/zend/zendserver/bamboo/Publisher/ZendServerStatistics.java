package com.zend.zendserver.bamboo.Publisher;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.atlassian.bamboo.build.PlanResultsAction;
import com.zend.zendserver.bamboo.StatisticsTask;
import com.zend.zendserver.bamboo.TaskResult.ResultParserMonitorGetIssuesListPredefinedFilter;

@SuppressWarnings("serial")
public class ZendServerStatistics extends PlanResultsAction {
	private Map<String, Map<String, Integer>> issuesBeforeDeploy;
	private Map<String, Map<String, Integer>> issuesAfterDeploy;
	
	private Map<String, String> issueTypes;

	private Map<String, Integer> issuesNoticeBeforeDeploy;
	private Map<String, Integer> issuesNoticeAfterDeploy;
	private Map<String, Integer> issuesWarningBeforeDeploy;
	private Map<String, Integer> issuesWarningAfterDeploy;
	private Map<String, Integer> issuesCriticalBeforeDeploy;
	private Map<String, Integer> issuesCriticalAfterDeploy;
	
	private String timeDeploy;
	private String timeStartPeriodBeforeDeploy;
	private String timeEndPeriodBeforeDeploy;
	private String timeStartPeriodAfterDeploy;
	private String timeEndPeriodAfterDeploy;
	private String tmp;
    
    public String doExecute() throws Exception {
        String result = super.doExecute();
        
        Map<String, String> metadata = this.getResultsSummary().getCustomBuildData();
		
        issueTypes = new Hashtable<String, String>();
		initIssueTypes(metadata.get(StatisticsTask.OUTPUT_FILE_KEY_ISSUES_LIST_BEFORE_DEPLOY));
		initIssueTypes(metadata.get(StatisticsTask.OUTPUT_FILE_KEY_ISSUES_LIST_AFTER_DEPLOY));
		
		initMaps();
		
		fillIssuesMap(
			issuesNoticeBeforeDeploy, 
			issuesWarningBeforeDeploy,
			issuesCriticalAfterDeploy, 
			metadata.get(StatisticsTask.OUTPUT_FILE_KEY_ISSUES_LIST_BEFORE_DEPLOY)
		);
		
		fillIssuesMap(
			issuesNoticeAfterDeploy, 
			issuesWarningAfterDeploy,
			issuesCriticalBeforeDeploy, 
			metadata.get(StatisticsTask.OUTPUT_FILE_KEY_ISSUES_LIST_AFTER_DEPLOY)
		);
		
		this.setTimeDeploy(metadata.get(StatisticsTask.TIME_DEPLOY));
		this.setTimeStartPeriodBeforeDeploy(metadata.get(StatisticsTask.TIME_START_PERIOD_BEFORE_DEPLOY));
		this.setTimeEndPeriodBeforeDeploy(metadata.get(StatisticsTask.TIME_END_PERIOD_BEFORE_DEPLOY));
		this.setTimeStartPeriodAfterDeploy(metadata.get(StatisticsTask.TIME_START_PERIOD_AFTER_DEPLOY));
		this.setTimeEndPeriodAfterDeploy(metadata.get(StatisticsTask.TIME_END_PERIOD_AFTER_DEPLOY));
           
        return result;
    }
    
    private void initIssueTypes(String filename) {
    	ResultParserMonitorGetIssuesListPredefinedFilter parser;
		try {
			parser = new ResultParserMonitorGetIssuesListPredefinedFilter(filename);
			NodeList issues = parser.getNodeListIssues();
	    	for (int i = 0; i < issues.getLength() - 1; i++) {
				Element issue = (Element) issues.item(i);
				String eventType = parser.getValue(issue, "eventType");
				String rule = parser.getValue(issue, "rule");
				
				issueTypes.put(eventType, rule);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    private void initMaps() {
    	issuesNoticeBeforeDeploy = new Hashtable<String, Integer>();
    	issuesNoticeAfterDeploy = new Hashtable<String, Integer>();
    	issuesWarningBeforeDeploy = new Hashtable<String, Integer>();
    	issuesWarningAfterDeploy = new Hashtable<String, Integer>();
    	issuesCriticalBeforeDeploy = new Hashtable<String, Integer>();
    	issuesCriticalAfterDeploy = new Hashtable<String, Integer>();
    	
    	initMap(issuesNoticeBeforeDeploy);
    	initMap(issuesNoticeAfterDeploy);
    	initMap(issuesWarningBeforeDeploy);
    	initMap(issuesWarningAfterDeploy);
    	initMap(issuesCriticalBeforeDeploy);
    	initMap(issuesCriticalAfterDeploy);
    }
    
    private void initMap(Map<String, Integer> map) {
    	for (Map.Entry<String, String> entry : issueTypes.entrySet()) {
    		map.put(entry.getKey(), 0);
    	}
    }
    
    private void fillIssuesMap(Map<String, Integer> notice, Map<String, Integer> warning, Map<String, Integer> critical, String filename) {
    	ResultParserMonitorGetIssuesListPredefinedFilter parser;
		try {
			parser = new ResultParserMonitorGetIssuesListPredefinedFilter(filename);
			NodeList issues = parser.getNodeListIssues();
			
	    	for (int i = 0; i < issues.getLength() - 1; i++) {
				Element issue = (Element) issues.item(i);
				String severity = parser.getValue(issue, "severity");
				String eventType = parser.getValue(issue, "eventType");
				if (severity.compareTo("Info") == 0) {
					notice.put(eventType, notice.get(eventType) + 1);
				}
				else if (severity.compareTo("Warning") == 0) {
					warning.put(eventType, warning.get(eventType) + 1);
				}
				
				else {
					critical.put(eventType, critical.get(eventType) + 1);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

	public Map<String, Map<String, Integer>> getIssuesBeforeDeploy() {
		return issuesBeforeDeploy;
	}

	public void setIssuesBeforeDeploy(Map<String, Map<String, Integer>> issues) {
		this.issuesBeforeDeploy = issues;
	}

	public Map<String, Map<String, Integer>> getIssuesAfterDeploy() {
		return issuesAfterDeploy;
	}

	public void setIssuesAfterDeploy(Map<String, Map<String, Integer>> issuesAfterDeploy) {
		this.issuesAfterDeploy = issuesAfterDeploy;
	}

	public String getTmp() {
		return tmp;
	}

	public void setTmp(String tmp) {
		this.tmp = tmp;
	}

	public String getTimeDeploy() {
		return timeDeploy;
	}

	public void setTimeDeploy(String timeDeploy) {
		this.timeDeploy = timeDeploy;
	}

	public String getTimeStartPeriodBeforeDeploy() {
		return timeStartPeriodBeforeDeploy;
	}

	public void setTimeStartPeriodBeforeDeploy(
			String timeStartPeriodBeforeDeploy) {
		this.timeStartPeriodBeforeDeploy = timeStartPeriodBeforeDeploy;
	}

	public String getTimeEndPeriodBeforeDeploy() {
		return timeEndPeriodBeforeDeploy;
	}

	public void setTimeEndPeriodBeforeDeploy(String timeEndPeriodBeforeDeploy) {
		this.timeEndPeriodBeforeDeploy = timeEndPeriodBeforeDeploy;
	}

	public String getTimeStartPeriodAfterDeploy() {
		return timeStartPeriodAfterDeploy;
	}

	public void setTimeStartPeriodAfterDeploy(String timeStartPeriodAfterDeploy) {
		this.timeStartPeriodAfterDeploy = timeStartPeriodAfterDeploy;
	}

	public String getTimeEndPeriodAfterDeploy() {
		return timeEndPeriodAfterDeploy;
	}

	public void setTimeEndPeriodAfterDeploy(String timeEndPeriodAfterDeploy) {
		this.timeEndPeriodAfterDeploy = timeEndPeriodAfterDeploy;
	}
	
	public Map<String, String> getIssueTypes() {
		return issueTypes;
	}

	public Map<String, Integer> getIssuesNoticeBeforeDeploy() {
		return issuesNoticeBeforeDeploy;
	}
	
	public String getIssuesListNoticeBeforeDeploy() {
		return join(getIssuesNoticeBeforeDeploy(), ", ");
	}

	public Map<String, Integer> getIssuesNoticeAfterDeploy() {
		return issuesNoticeAfterDeploy;
	}
	
	public String getIssuesListNoticeAfterDeploy() {
		return join(getIssuesNoticeAfterDeploy(), ", ");
	}

	public Map<String, Integer> getIssuesWarningBeforeDeploy() {
		return issuesWarningBeforeDeploy;
	}
	
	public String getIssuesListWarningBeforeDeploy() {
		return join(getIssuesWarningBeforeDeploy(), ", ");
	}

	public Map<String, Integer> getIssuesWarningAfterDeploy() {
		return issuesWarningAfterDeploy;
	}
	
	public String getIssuesListWarningAfterDeploy() {
		return join(getIssuesWarningAfterDeploy(), ", ");
	}

	public Map<String, Integer> getIssuesCriticalBeforeDeploy() {
		return issuesCriticalBeforeDeploy;
	}
	
	public String getIssuesListCriticalBeforeDeploy() {
		return join(getIssuesCriticalBeforeDeploy(), ", ");
	}

	public Map<String, Integer> getIssuesCriticalAfterDeploy() {
		return issuesCriticalAfterDeploy;
	}
	
	public String getIssuesListCriticalAfterDeploy() {
		return join(getIssuesCriticalAfterDeploy(), ", ");
	}
	
	private String join(Map<String, Integer> map, String delimiter) {
		String tmp = "";
		Iterator<Entry<String, Integer>> iterator = map.entrySet().iterator();
		
		while (iterator.hasNext()) {
			Entry<String, Integer> item = iterator.next();
			tmp += item.getValue();
			if (iterator.hasNext()) tmp += delimiter;
		}

		return tmp;
	}
}