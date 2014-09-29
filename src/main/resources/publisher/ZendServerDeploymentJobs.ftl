<html>
<body>
<h1>Test Report</h1>

<ul>
<li>${urlZsJobDetails} (${urlZsJobLog})</li>
</ul>
<hr>
***${strTest}***

[#if isJob]
        yes, is Job
    [#else]
        no, is not a job
    [/#if]

</body>
</html>