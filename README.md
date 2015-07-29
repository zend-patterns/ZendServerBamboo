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

Packages and Shared Artifacts
----------------
In order to deploy a (ZPK) package to a Zend Server instance, you have to make sure, that there is a ZPK file available for the Deployment Task. This can be achieved in several ways.
### Packaging Task and Deployment Task in one stage
In this (unusual) setup no specific configuration is necessary. The Packaging Task creates a file called <buildNr>.zpk and stores it in <workingDir>/zpk . This file is automatically detected by the Deployment Task and is being used for deploying to a Zend Server instance.
### Custom ZPK name
The Packaging Task and the Deployment Task can be configured to use a custom zpk file name. Relative paths to the working dir and absolute paths are supported. Variable "${bamboo.buildNumber}" can be used as part of the filename. It will be replaced with the current Bamboo build number.
The ability to specify a custom zpk path makes Packing and Deployment Task independent from each other (Task Types can live in completely independent stages and even plans), but of course the maintainer of the Bamboo setup has to take care that the zpk created by a Packaging Task (or any other mechanism) can be found by the Deployment Task. 
Directories specified in the custom zpk filname are created automatically if necessary - and possible regarding file permissions.
### Shared Artifacts
#### Artifact Definition
A stage containing a Packaging Task can be configured to provide an Artifact (see [Shared Artifacts]) to other stages and to the Deployment phase. An example setup can look like this:
  - Name: ZPK
  - Location: app-zpk/${bamboo.buildNumber}
  - Copy-Pattern: ${bamboo.buildNumber}.zpk

With this setup, the Packaging task creates for each build a new directory in <workingDir>/app-zpk and stores the created zpk file.
It's not necessary to create a new folder for each build, but at least the zpk filename should be unique accoring to the build number.
#### Artifact Dependeny
In order to use an artifact defined in a Packaging Task, one has to define an Artifact Dependency in the appropriate stage. Here, the Artifact Definition can be simply chosen by name. The Deployment Task will automatically fetch the location and the copy pattern of the zpk file. For the Deployment phase this is the recommended way.

Zend Server Statistics Task
---------------------------
See [Configuring Tasks] on how to add a Bamboo task in general.

The Zend Server Statistics Graph gives you event related data from Zend Server to analyse a new deployment. All unique events after a deployment will be compared with the number of events which occured in the same time range before the event.

[Zend Server SDK]:https://github.com/zend-patterns/ZendServerSDK
[Package Structure]:http://files.zend.com/help/Zend-Server/zend-server.htm#understanding_the_package_structure.htm
[Configuring Tasks]:https://confluence.atlassian.com/display/BAMBOO/Configuring+tasks
[Shared Artifacts]:https://confluence.atlassian.com/display/BAMBOO/Bamboo+Best+Practice+-+Sharing+artifacts

Troubleshooting
---------------
## Process Timeout
If you run into a timeout problem that the process of creating the ZPK or for deploying the app to the server takes more than 60 seconds (default value), you can specify a processTimeout variable in the Build section or in the Deploy section.
For the Build section you can find a 'Variable' tab in the plan configuration.
For the Deploy section you can find a button "Variables" in the environment seettings.
The variable name has to be `processTimeout`, the value has to be an **integer** (unit is **seconds**) according to your needs.
That means that you are able to set the timeout per Plan resp. Deploy but not on a Task level.
