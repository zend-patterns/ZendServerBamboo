<style type="text/css">
#title\:com\.zend\.zendserver\.plugins\.capability\.zswa\.executable\.phar + span.subGrey,
#title\:com\.zend\.zendserver\.plugins\.capability\.zswa\.executable\.php + span.subGrey {
	display: none;
}
</style>
[@ww.select labelKey='Executable' name='zswa-clientExecutableKind' list=capabilityType.executableTypes listKey='key' listValue='value' toggle=true /]
[#list capabilityType.executableTypes.keySet() as executableTypeKey]
    [@ui.bambooSection  dependsOn='zswa-clientExecutableKind' showOn=executableTypeKey]
        [@ww.textfield labelKey='Path' value=capabilityType.getExtraInfo(executableTypeKey) name=executableTypeKey description=capabilityType.getExecutableDescription(executableTypeKey) /]
    [/@ui.bambooSection]
[/#list]