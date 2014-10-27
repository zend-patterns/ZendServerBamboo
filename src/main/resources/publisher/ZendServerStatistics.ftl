<script language="javascript">
function drawChart() {
	$('#container').highcharts({

        chart: {
            type: 'column',
            events: {
            	load: function(event) {
            	}
        	} 
        },

        title: {
            text: 'Unique events before and after Deployment'
        },
        
        legend: {
            enabled: false
        },

        xAxis: {
            categories: [
            <#list issueTypes?keys as type>
				'${issueTypes.get(type)}',
			</#list>
			''
            ]
        },

        yAxis: {
            allowDecimals: false,
            min: 0,
            title: {
                text: 'Number of Events'
            },
            stackLabels: {
            	enabled: true,
                rotation: 270,
                certicalAlign: 'bottom',
                y: -37,
            	style: {
                	color: 'gray'
	            },
	            formatter: function() {
	                return this.stack;
	            }
	        }
        },

        tooltip: {
            formatter: function () {
                return '<b>' + this.x + '</b><br/>' +
                    this.series.name + ': ' + this.y + '<br/>' +
                    'Total: ' + this.point.stackTotal;
            }
        },

        plotOptions: {
            column: {
                stacking: 'normal'
            }
        },

        series: [
        {
            name: 'Notices before Deploy',
            data: [${issuesListNoticeBeforeDeploy}],
            stack: 'before deploy',
            color: '#57b5e4'
        }, 
        {
            name: 'Notices after Deploy',
            data: [${issuesListNoticeAfterDeploy}],
            stack: 'after deploy',
            color: '#57b5e4'
        }, 
        {
            name: 'Warnings before Deploy',
            data: [${issuesListWarningBeforeDeploy}],
            stack: 'before deploy',
            color: '#fcb31f'
        }, 
        {
            name: 'Warnings after Deploy',
            data: [${issuesListWarningAfterDeploy}],
            stack: 'after deploy',
            color: '#fcb31f'
        }, 
        {
            name: 'Critical issues before Deploy',
            data: [${issuesListCriticalBeforeDeploy}],
            stack: 'before deploy',
            color: '#ed4e29'
        }, 
        {
            name: 'Critical issues after Deploy',
            data: [${issuesListCriticalAfterDeploy}],
            stack: 'after deploy',
            color: '#ed4e29'
        }]
    });
}

var $ = jQuery;
if (!window.Highcharts) {
	$.when(
		$.getScript("http://code.highcharts.com/highcharts.src.js")).then(function(){
	    	$.when(
				$.getScript("http://code.highcharts.com/modules/exporting.js")).then(function(){
			    	drawChart();
			});
	});
}
else {
	drawChart();
}
</script>

<style type="text/css">
div.bg {
margin-top: 30px;
background-image: url(http://static.zend.com/cmsdata/solutions/zend-server-logo-396x55px.png);
background-repeat: no-repeat;
background-position: top right;
width:100%;
}

.collectionDescLabel {
font-weight: normal; 
}
.collectionDesc {
font-weight: normal;
text-align: left;
}
</style>
<div class="bg">

<h1>Zend Server Statistics</h1>
<h2>App Issue / Event Collection Comparison</h2>
<div class="result-summary aui-group" style="margin-bottom: 30px;>
    <div class="details aui-item">
        <dl class="details-list">
		    <dt class="DeploymentTime" style="font-weight: normal">Deployment Time:</dt>
			<dd>${timeDeploy}</dd>
		</dl>
	</div>
</div>

<div id="container" style="min-width: 300px; height: 500px; margin: 1em"></div>

<table style="width: 100%" class="aui" id="buildMetadata">
	<thead>
		<tr>
			<th style="text-align: left; width:30%; vertical-align: bottom;">
				Error Type
			</th>
			<th style="text-align: left; vertical-align: bottom;">
				<div class="result-summary aui-group" style="margin-bottom: 20px; ">
				    <div class="details aui-item">
				        <dl class="details-list">
						    <dt class="collectionDescLabel">Collection Start:</dt>
							<dd class="collectionDesc">${timeStartPeriodBeforeDeploy}</dd>
						</dl>
						<dl class="details-list">
						    <dt class="collectionDescLabel">Collection End:</dt>
							<dd class="collectionDesc">${timeEndPeriodBeforeDeploy}</dd>
						</dl>
					</div>
				</div>
			 	Severity, event count<br />(before Deploy)
			</th>
			<th style="text-align: left; vertical-align: bottom;">
				<div class="result-summary aui-group" style="margin-bottom: 20px;">
				    <div class="details aui-item">
				        <dl class="details-list">
						    <dt class="collectionDescLabel">Collection Start:</dt>
							<dd class="collectionDesc">${timeStartPeriodAfterDeploy}</dd>
						</dl>
						<dl class="details-list">
						    <dt class="collectionDescLabel">Collection End:</dt>
							<dd class="collectionDesc">${timeEndPeriodAfterDeploy}</dd>
						</dl>
					</div>
				</div>
				Severity, event count<br />(after Deploy)
			</th>
		</tr>
	</thead>
	
	<tbody>
		<#list issueTypes?keys as typeId>
			<tr>
				<td>${issueTypes.get(typeId)}</td>
				<td>
					<div class="result-summary aui-group">
					    <div class="details aui-item">
					        <dl class="details-list">
							    <dt class="collectionDescLabel">Notice:</dt>
								<dd class="collectionDesc">${issuesNoticeBeforeDeploy.get(typeId)}</dd>
							</dl>
							<dl class="details-list">
							    <dt class="collectionDescLabel">Warning:</dt>
								<dd class="collectionDesc">${issuesWarningBeforeDeploy.get(typeId)}</dd>
							</dl>
							<dl class="details-list">
							    <dt class="collectionDescLabel">Critical:</dt>
								<dd class="collectionDesc">${issuesCriticalBeforeDeploy.get(typeId)}</dd>
							</dl>
						</div>
					</div>
				</td>
				<td>
					<div class="result-summary aui-group">
					    <div class="details aui-item">
					        <dl class="details-list">
							    <dt class="collectionDescLabel">Notice:</dt>
								<dd class="collectionDesc">${issuesNoticeAfterDeploy.get(typeId)}</dd>
							</dl>
							<dl class="details-list">
							    <dt class="collectionDescLabel">Warning:</dt>
								<dd class="collectionDesc">${issuesWarningAfterDeploy.get(typeId)}</dd>
							</dl>
							<dl class="details-list">
							    <dt class="collectionDescLabel">Critical:</dt>
								<dd class="collectionDesc">${issuesCriticalAfterDeploy.get(typeId)}</dd>
							</dl>
						</div>
					</div>
				</td>
			</tr>
		</#list>
	</tbody>	
</table>
</div>