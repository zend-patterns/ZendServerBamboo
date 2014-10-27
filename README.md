Zend Server Bamboo Integration
=========

Prerequisites
-------------
On Bamboo Server and all Agents on which the Plugin Tasks should be executed, PHP 5.3.5 or newer has to be installed for running [Zend Server SDK]. Zend Server SDK is the basis of all communication between Bamboo and Zend Server.

Installation
------------
The [Zend Server SDK] as well as the Zend Server Bamboo Plugin has to be installed in order pack and deploy PHP apps to a Zend Server system.
### Zend Server SDK
Follow the instructions in the README of the [Zend Server SDK]
### Zend Server Bamboo Plugin
Find the zendserver\*.jar file and store it on the local disk. In Bamboo admin interface go to 'ADD-ONS' | Manage Add-ons, pause the Bamboo server and click on 'Upload Add-on'. Choose the zendserver\*.jar file and upload it.  The server can be resumed now.

Usage
-----
Zend Server Bamboo plugin contains one Zend Server Capability and four Task types. The Zend Server Capability is mandatory to configure, as all Task types rely on correct setup of the Zend Server SDK. Afterwards a Plan can be configured which is normally a sequence of the following Task types:
  - Source Code Checkout (Built-in Task type in Bamboo)
  - Zend Server Packaging Task
  - Zend Server Deployment Task
  - Zend Server Deployment Check Task
Of course, QA tasks like for PHPUnit support can be added accordingly.

Zend Server Capability
----------------------
The Zend Server Bamboo Plugin is using the [Zend Server SDK] as a basis for all Tasks. For your convenience a Zend Server Capability has been implemented. In the Bamboo admin interface go to 'Server capabilities' and choose under 'Add capability' the item 'Zend Server Web API Client'. According to your Zend Server SDK installation you can either choose PHAR or the PHP version as the executable. Please modify also the Path so that it fits to your setup.
This capability can be added as a prerequisite for a Bamboo plan (optional). In any case the capability is necessary in order to execute all Zend Server tasks successfully.

Zend Server Packaging Task
--------------------------
See [Configuring Tasks] on how to add a Bamboo task in general.

Packaging Task is very easy to use. Just add a task to your job and give it a description (optional). The Packaging task is taking all files in the working directory and creates a ZPK file. Therefore a valid deployment.xml and deployment.properties files has to be in the root of the working directory. See [Package Structure] for more information on how to prepare for a ZPK creation. 
Alternatively the Zend Server SDK also provides functionality for creating deployment.xml and deployment.properties.

Zend Server Deployment Task
---------------------------
See [Configuring Tasks] on how to add a Bamboo task in general.

The Zend Server Deployment Task is responsible for deploying the application to a Zend Server instance (either single server or cluster environment). Except the User Params all fields are required. 

In order to access Zend Server via the Zend Server SDK which is using the so called WebAPI, one has to pass a valid Zend Server URL (including the port number) as well as the API credentials. The API Key and Secret can be managed in the Zend Server UI in Administration | WebAPI. Please note that the availability of the given Zend Server instance is checked on the fly.

The Application Base URI can be either a path (Zend Server will choose the default vhost) or a full valid hostname (optional: including a path) where the vhost will be created automatically if it's not available already.

Please note that the Zend Server version has to be newer or equal than 6.0. Zend Server below version 6 is not supported.

The User Params can be used in the Deployment hook scripts, see [Package Structure]. Params have to be formatted as a query string, e.g. param1=val1&param2=val2&... 

As deployment can take a while - depending on the application - the Zend Server SDK is following an asynchronous concept. That means that the process invoking a deployment is not waiting for the result of the deployment itself. Therefore the 'Zend Server Deployment Check Task' has to be configured.

Zend Server Deployment Check Task
---------------------------------
See [Configuring Tasks] on how to add a Bamboo task in general.

The Zend Server Deployment Check Task is responsible for checking whether a previous deployment has been finished successfully or not.

In general the same configuration settings as for the 'Zend Server Deployment Task' have to be provided. Additionally one has to specify how often the status of the deployment should be checked and how much time should be waited between these checks. If this is not enough time for a deployment to finish, the Task will be marked as failed.

One of the most important settings in this task is whether you want to rollback automatically in case of an error. This ensures that a working version (from a deployment perspective) is running all the time without the need of any manual interactions.

Zend Server Rollback Task
-------------------------
See [Configuring Tasks] on how to add a Bamboo task in general.

The Zend Server Rollback Task is rolling back an application to the previous version.

In general the same configuration settings as for the 'Zend Server Deployment Task' have to be provided. Additionally one has to specify how often the status of the rollback should be checked and how much time should be waited between these checks. If this is not enough time for a rollback to finish or the rollback is failing, the Task will be marked as failed.

Shared Artifacts
----------------
If the ZPK files built in  Zend Server Packaging Task should be reused for  Deployments to other systems, the ZPK files can be marked as [Shared Artifacts]. 
ZPK files are stored in the zpk sub-directory of the working directory. In order to configure this as shared artifacts, the Location has to be set to 'zpk' and the Copy pattern is '**/*.zpk'.

Zend Server Statistics Task
---------------------------
See [Configuring Tasks] on how to add a Bamboo task in general.

The Zend Server Statistics Graph gives you event related data from Zend Server to analyse a new deployment. All unique events after a deployment will be compared with the number of events which occured in the same time range before the event.

[Zend Server SDK]:https://github.com/zend-patterns/ZendServerSDK
[Package Structure]:http://files.zend.com/help/Zend-Server/zend-server.htm#understanding_the_package_structure.htm
[Configuring Tasks]:https://confluence.atlassian.com/display/BAMBOO/Configuring+tasks
[Shared Artifacts]:https://confluence.atlassian.com/display/BAMBOO/Bamboo+Best+Practice+-+Sharing+artifacts
