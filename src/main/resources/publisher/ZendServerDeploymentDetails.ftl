<style type="text/css">
div.bg {
background-image: url(http://static.zend.com/cmsdata/solutions/zend-server-logo-396x55px.png);
background-repeat: no-repeat;
background-position: top right;
width:100%;
}
</style>
<div class="bg">
<h1>Zend Server Deployment Details</h1>

<h2>Application Info</h2>
<h3>Status: ${status}</h3>
<div class="result-summary aui-group">
    <div class="details aui-item">
        <dl class="details-list">
        
		    <dt class="appId">App Id:</dt>
			<dd>${appId}</dd>
			
			<dt class="appBaseUrl">Base Url:</dt>
			<dd><a href="${appBaseUrl}">${appBaseUrl}</a></dd>
			
			<dt class="appName">App Name:</dt>
			<dd>${appName}</dd>
			
			<dt class="userAppName">User App Name:</dt>
			<dd>${userAppName}</dd>
			
			<dt class="installedLocation">Installed Location:</dt>
			<dd>${installedLocation}</dd>
			
			<dt class="isRollbackable">App is rollbackable:</dt>
			<dd>${isRollbackable}</dd>
			
			<dt class="isRedeployable">App is redeployable:</dt>
			<dd>${isRedeployable}</dd>
			
        </dl>
    </div>
</div>
</div>