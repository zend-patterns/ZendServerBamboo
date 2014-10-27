<style type="text/css">
div.bg {
margin-top: 30px;
background-image: url(http://static.zend.com/cmsdata/solutions/zend-server-logo-396x55px.png);
background-repeat: no-repeat;
background-position: top right;
width:100%;
}
</style>
<div class="bg">

<h1>Zend Server Deployment Jobs</h1>

<#if urlZsJobDetails != "">
<h2>Zend Server Deployment</h2>
A Zend Server Deployment Task has been executed.<br />
Please click the link to see detailed information about the deployment:
<h3 style="margin-top: 10px;">${urlZsJobDetails}</h3> 
</#if>

<#if urlZsStatistics != "">
<h2>Zend Server Deployment</h2>
A Zend Server Statistics Task has been executed.<br />
Please click the link to get statistical information about the deployment:
<h3 style="margin-top: 10px;">${urlZsStatistics}</h3> 
</#if>

<#if urlZsJobLog != "">
<h2>Logs</h2>
Please find below a short cut link to the Job log:<br>
<h3 style="margin-top: 10px;">${urlZsJobLog}</h3> 
</#if>

</div>