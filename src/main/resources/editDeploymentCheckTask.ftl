[@ww.textfield labelKey="Zend Server URL" name="zs_url" required='true'/]
<small>The Zend Server URL. E.g. http://localhost:10081</small>
[@ww.textfield labelKey="API Key" name="api_key" required='true'/]
<small>The name of the API key</small>
[@ww.textfield labelKey="API Secret" name="api_secret" required='true'/]
<small>The secret/hash of the API key</small>
[@ww.textfield labelKey="Application Base URI" name="base_url" required='true'/]
<small>The baseUri of where the application will be installed</small>
[@ww.textfield labelKey="Application Name" name="app_name" required='true'/]
<small>Name of the application</small>
[@ww.textfield labelKey="Zend Server Version" name="zsversion" required='true'/]
<small>The major Zend Server version. Ex: 6.0, 6.1, 6.3</small>
[@ww.textfield labelKey="Amount of retries for status check" name="retry" required='true'/]
[@ww.textfield labelKey="Wait time (sec) between retry iterations" name="waittime" required='true'/]
[@ww.checkbox labelKey="Rollback automatically in case of a deployment error" name="rollback"/]