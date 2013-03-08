% Manual Molgenis/Compute
% Genomics Coordination Center
% December 16, 2012
<!--generated for pandoc: http://johnmacfarlane.net/pandoc/-->

#Compute framework overview

MOLGENIS compute (or *MOLGENIS Compute*) is 'just enough' to rapidly generate analysis workflows that can run locally, on parallel compute clusters and the grid. 

* Users can use build on their standard expertise in (shell) scripts
* Users can rapidly share their workflows to accross Linux servers
* Users can easily view the scripts generated for provenance and debugging.
* Users can customize *MOLGENIS Compute* to fit their local practices

To use *MOLGENIS Compute*, you need the following.

* `workflow.csv`: a file that describes steps to be executed in order.
* `protocols`: a directory in which each file is a script template describing a step.
* `parameters.csv`: a file which defines all protocol parameters and default values.
* `worksheet.csv`: a file which contains the run specific values of parameters.

Below use of *MOLGENIS Compute* is described:

* [Download compute](#Download-compute) explains how to get the software
* [Workflow creation](#Workflow-creation) explains the basic use of Molgenis Compute in a basic workflow.  
* [Workflow execution](#Workflow-execution) describes how to execute this workflow.  
* [Compute advanced features](#Compute-advanced-features) extends that workflow commonly used features. All sections assume that you run *MOLGENIS Compute* from the command line.  
* [Workflow deployment](#Workflow-deployment) details best practices with binaries and files
* [Compute database]() shows how one can run *MOLGENIS Compute* from a database instead of commandline. Molgenis Compute *db* makes use of pilot jobs which enable 'self-scheduled' execution on grid (such as BigGrid).

The manual is in the form of a walkthrough manual.

##Download compute

Before you start, please first download *MOLGENIS Compute*.

###Download the compute distro
You can download a ready made binary as follows:

>mkdir mycompute  
>cd mycompute  
>\# Check www.molgenis.org/wiki/ComputeStart for the latest and greatest version!

>wget http://www.molgenis.org/raw-attachment/wiki/ComputeStart/molgenis_compute-fb05467.zip

>unzip molgenis_compute-\<version>.zip

>mv molgenis_compute-\<version>/* .  
>  
>\#test  
>sh molgenis_compute.sh  

###Checkout out the latest Compute
Alternatively to download you can checkout the latest code:

>mkdir mycompute  
>cd mycompute  
>git clone https://github.com/molgenis/molgenis_apps-legacy.git  
>git clone https://github.com/molgenis/molgenis-legacy.git  
>ant -f molgenis_apps-legacy/build_compute.xml clean-generate-compile-makedistro  
>unzip molgenis_apps-legacy/dist/molgenis_compute-*.zip  
>mv molgenis_compute-*/* .  
>  
>\#test  
>sh molgenis_compute.sh  

### Software needed
Molgenis/compute is known to work with the following software:

* java 1.6.0 or higher
* git 1.7.1 or higher (only if you build yourself)
* ant 1.7.1 or higher (only if you build yourself)
* mysql 5.1.54 or higher (only for the database version)


##Workflow creation

This section explains how one can use *MOLGENIS Compute* to create a workflow of shell scripts using the example of printing invitations for a party. The next section [Workflow execution](#Workflow-Execution) discusses how to execute. Below is explained how one can create a workflow, a protocol, and a parameters list. 


###Workflow
The most simple workflow contains only one step. Let's call this step `GuestInvitationStep`. Now create a file called `workflow.csv` with the following content as follows:

>\#create new workflow dir  
>mkdir invitationWorkflow    
>nano invitationWorkflow/workflow.csv

Paste:
<table>
		<tr><td>name, </td><td>protocol_name,</td><td>PreviousSteps_name</td></tr>
		<tr><td>GuestInvitationStep,</td><td>GuestInvitation,</td><td></td></tr>
</table>  

*Explanation:* The first row contains the column headers, each of the following rows describes a step in the workflow. In the first column you'll find the step's `name` (unique per workflow), followed, in the second column, by the `protocol_name` that here referers to a file `protocol/GuestInvitation.ftl` (see [Protocols](#Protocol) for details). The third column contains a comma separated list of step names that should be finished before the step in a row starts (see [Previous steps](#Previous-steps)). In our current example, the list in the third column is empty.

###Protocols
A protocol generally is a template of a shell script written in [Freemarker](http://freemarker.org/) language that describes the work is to be done (see http://freemarker.org/ for a manual).

Let's now create a directory called *protocols* and save our first protocol file *protocols/GuestInvitation.ftl* in there, with the following content.

>nano invitationWorkflow/protocols/GuestInvitation.ftl  

Paste:  
>echo "Hello ${guest},"  
>echo "We invite you for our ${party}."   

*Explanation:* A protocol may refer to parameters, such as `${guest}` and `${party}` in our example. The idea is that you can use such templates to rapidly generate up to thousands of shell scripts, given different values for these parameters. Given a value for each of the parameters, this protocol echos an invitation to the standard out.

###Parameters
Each parameter that is used in a protocol should be defined in a parameters file. Let's create such a file and call it `parameters.csv`. Now add our two parameters `${guest}` and `${party}` as follows to this file.

>nano invitationWorkflow/parameters.csv:  

Paste:
<table>
<tr><td>name,</td><td>defaultValue,</td><td>description,</td><td>dataType,</td><td>hasOne_name</td></tr>
<tr><td>guest,</td><td>,</td><td>,</td><td>,</td><td></tr>
<tr><td>party,</td><td>wedding,</td><td>,</td><td>,</td><td></tr>
</table>
  
*Explanation:* There are two types of parameters. First, parameters may be constants, like our parameter `party` which has a default value "wedding". So, in each of the generated scripts, the value of the parameter `party` will be "wedding". If you want to send the same invitation, but for a different party, you only have to change this value in one place. Second, parameters may be variables and have a different value in each of the scripts that are generated from a given protocol. The next section will explain how these parameters, like `guest` in our case, and their different values are defined in a worksheet.

##Workflow execution
To run a workflow you can use `worksheet.csv` to provide the specific parameter values for all your analysis steps. Subsequently you can generate all the scripts and run them locally, on a compute cluster or on the grid.

###Worksheet
While global values can be provided using defaultValue in `parameters.csv`, the specific values need to change everytime you run the workflow. Let's now create a *worksheet* containing these values for our party invitation workflow and save it as `worksheet.csv`:

>nano invitationWorkflow/worksheet.csv  

Paste:
<table>
<tr><td>guest</td></tr>
<tr><td>Charly</td></tr>
<tr><td>Cindy</td></tr>
<tr><td>Abel</td></tr>
<tr><td>Adam</td></tr>
<tr><td>Adri</td></tr>
</table>  
  
*Explanation:* The first row contains parameter names (c.q. target types), comma separated. In our case we only have one parameter, called `guest`. Each of the following rows contains the parameters values. When running \mc, the protocols are subsequently applied to each of the values. I.e., in our example, we will generate a different invitation script for each of our guests.

###Script generation
You need no command line parameters if your  files (workflow.csv, worksheet.csv, parameters.csv) and (protocol, template) directories, is in the directory from which you run molgenis_compute.sh. Otherwise, you may want to specify the *-inputdir* that refers to the directory in which you have stored your `workflow.csv`, `protocol` directory,`parameters.csv` and `worksheet.csv`. Alternatively, you may specify (or overwrite) each of these files individually by `-workflow`, `-protocols`, `-parameters`, `-worksheet`. The second parameter (`id`) refers to the name you give your analysis run. This will automatically create a directory with the same name where all scripts will be generated. Alternatively, you may explicitly specificy `-scripts`, where the parameter `scripts` refers to the directory where *MOLGENIS Compute* will store the generated scripts.   

Let's now generate the scripts with the invitations by running the following command. We assume that you have put your workflow, parameters, worksheet files and protocols directory in a directory called `helloWorld`.
  
>cd ..  
>sh molgenis_compute.sh \   
>-inputdir=invitationWorkflow

*Tip*: In your protocols, you may want to use the values of the command line parameters. However, be aware that the parameter names you have to use are slightly different from the command line parameters: `${McWorkflow}`,`${McProtocols}`, `${McParameters}`, `${McWorksheet}`, and `${McScripts}`. 

###Review generated scripts
So, how do the generated scripts in the `run01` directory look like? Let's first consider one of the five scripts that contain the invitations to our five guests: `run01/run01_s00_GuestInvitation_1.sh`. The script name is constructed as follows.

* `run01`: the id that you used when you ran \mc.
* `s00`: the step number in your workflow, starting from zero.
* `GuestInvitation`: the corresponding workflow step name.
* `1`: the line number in the worksheet.

The step number, step name and line number are separated by underscores. Let's open `run01/run01_s00_GuestInvitation_1.sh` and view its content:

>echo "Hello Charly,"  
>echo "We invite you for our wedding."

Now let's open the second script, `run01_s00_GuestInvitation_2.sh`:

>echo "Hello Cindy,"  
>echo "We invite you for our wedding."  

These scripts correspond to the `GuestInvitation` protocol, where `${party}` got the constant value "wedding", and `${guest}` got a different value each time, as defined in the worksheet. Because "Charly" is the first value in the worksheet, she ends up in the first script. Correspondingly, because "Cindy" is the second value in the worksheet, she ends up in the second script. And so on.

###Script execution
Next to the analysis scripts, three submit scripts are generated to allow you to run the workflow locally, on a PBS cluster, or using some custom compute grid.

* `runlocal.sh` executes the analysis scripts sequentially (ideal for testing)
* `submit.sh` submits the analysis scripts to a PBS scheduler
* `submit.sh.ftl` is a script that is based on the template `submit.sh.ftl` which you can find in the `templates` directory. You can customize the way the analysis scripts are submitted by customizing this protocol.

%todo add grid to commandline?
%todo example output for runlocal?

You can use these scripts to submit and start the execution of your analysis scripts in the right order. Section [Workflow step dependencies](#A-workflow-with-dependencies) below will explain how you can define the order between the steps in your workflow. After executing the analysis scripts, two invitations will be echo'ed to the standard out.

In addition, a copy of your workflow, parameters and worksheet file are put in the `scripts` directory, as well.

##Compute advanced features
This section adds more details to the "inviteWorkflow" we've developed above and demonstrates that *MOLGENIS Compute* can generate more realistic workflows, too. In addition of only inviting guests to our wedding, we will also organize some activity for our guests. The guests will be divided in two groups: child or adult. Each group has one organizer that will plan an activity for his group. After sending out the individual invitations to our guests, for each group, we will send its organizer a letter with a guest list.

###Workflow step dependencies
Let's call the step that sends a letter to each organizer `OrganizerInvitation`. Suppose that before starting this step, we want the `GuestInvitation` step to be finished first. Let's add the new step `OrganizerInvitationStep` to our `workflow.csv` file and define its dependency on the `GuestInvitationStep` step using the column `PreviousSteps_name`:

>nano invitationWorkflow/workflow.csv:

Paste:
<table>
<tr><td>name,</td><td>protocol_name,</td><td>PreviousSteps_name</td></tr>
<tr><td>GuestInvitationStep,</td><td>GuestInvitation,</td></tr>
<tr><td>OrganizerInvitationStep,</td><td>OrganizerInvitation,</td><td>GuestInvitationStep</td></tr>
</table>  
  
Adding `GuestInvitationStep` to its `PreviousSteps_name` will ensure that the `GuestInvitation` scripts will be finished before the `OrganizerInvitation` scripts will be started. Be aware that the values in the third column refer to those in the first column, and not to those in the second column.

###Customized'foreach' iteration
Default all protocols are applied to all rows in the `worksheet.csv`. Often you don't want that and instead only run a particular protocol the unique values in one worksheet column.
In this example we want the OrganizerInvitation to run only for each unique `group`. Therefore, let's create a new protocol and save it as `OrganizerInvitation.ftl` in the protocols directory and add a `#foreach` clause next to the script template content.

>nano invitationWorkflow/protocols/OrganizationInvitation.ftl  

Paste:
>\#FOREACH group  
>  
>echo "Dear ${organizer},"  
>echo "Please organize activities for the ${group} group."  
>echo "List of guests:"  
><\#list guest as g>  
>	echo "${g}"   
></\#list>


In this new protocol, we introduce a new parameter `group` which may have the values "child" and "adult". We will specify these values in the `worksheet.csv` below. Instead of applying this template to all rows in the worksheet, the `#FOREACH group` statement in the first line of this protocol means that this protocol will be applied *only* to each unique values that `group` has. I.e., it will be applied once to "child", and once to "adult".  
What happens under the hood, is that the worksheet is *folded* based on the specified target. The folding reduces the worksheet to only two lines, one for each group. This will thus result in a list of guests per group. This protocol iterates through that list of guests by making use of the `<#list>` Freemarker syntax. Section [Worksheet folding]() explains the folding of the worksheet as a result of the `#FOREACH group` statement, in detail. That section also explains why the parameter `organizer`, which is also new in this protocol, can also be used as a value, insteadof a list.

###Worksheet folding
Let's take the worksheet from section [Worksheet](#Workksheet) and add a `group` ("child" or "adult") to each guest. Let's also add an `organizer` for each group and update the `worksheet.csv` file as follows.

>nano invitationWorkflow/worksheet.csv

Paste:
<table>
<tr><td>guest,</td><td>group,</td><td>organizer</td></tr>
<tr><td>Charly,</td><td>child,</td><td>Oscar</td></tr>
<tr><td>Cindy,</td><td>child,</td><td>Oscar</td></tr>
<tr><td>Abel,</td><td>adult,</td><td>Otto</td></tr>
<tr><td>Adam,</td><td>adult,</td><td>Otto</td></tr>
<tr><td>Adri,</td><td>adult,</td><td>Otto</td></tr>
</table>

In general, a protocol is applied to each of the rows in the original worksheet. However, if a protocol contains a `#FOREACH` statement, then the worksheet will first be *folded*. After the folding, the protocol will be applied to each line in the *folded* worksheet. Because the `OrganizerInvitation.ftl` protocol starts with "`#FOREACH group`", it will be executed *for each unique value* of `group` (i.e. "child" and "adult"). Under the hood, after folding each line contains a different value of group.

Result of 'worksheet.csv' after folding:
<table>
<tr><td>guest,</td><td>group,</td><td>organizer</td></tr>
<tr><td>[Charly, Cindy],</td><td>child,</td><td>[Oscar,Oscar]</td></tr>
<tr><td>[Abel, Adam, Adri],</td><td>adult,</td><td>[Otto,Otto,Otto]</td></tr>
</table>

For each group, you'll get a *list* of guests which are indicated with the brackets [ and ]. 
So, folding on a certain target, results in lists of the other targets. However, although we do see a list of guests, we also see a list of organizers, while there should be only one. To solve this we will update the 'parameters.csv' and relate the 'organizer' one-on-one to 'group' using [Parameter hasOne relationship](#Parameter-hasOne-relationship).

###Parameter hasOne relationship
So, we need to define the new parameters `group` and `organizer`. Moreover, we want to specify that each group *can only have one organizer*:

>nano invitationWorkflow/parameters.csv

Paste:
<table>
<tr><td>name,</td><td>defaultValue,</td><td>description,</td><td>dataType,</td><td>hasOne_name</td><tr>
<tr><td>guest,</td><td>,</td><td>,</td><td>,</td><td><tr>
<tr><td>party,</td><td>wedding,</td><td>,</td><td>,</td><td></tr>
<tr><td>organizer,</td><td>,</td><td>,</td><td>,</td><td></tr>
<tr><td>group,</td><td>,</td><td>,</td><td>,</td><td>organizer</td></tr>
</table>  
 
Note that we have  added relationships between group and organizer in the `hasOne_name` column: a group has only one organizer. If we now re-apply the folding as describing in [Worksheet folding](#Worksheet-folding) there result is now as desired:

Output of 'worksheet.csv' after folding:
<table>
<tr><td>guest,</td><td>group},</td><td>organizer</td></tr>
<tr><td>[Charly, Cindy],</td><td>child,</td><td>Oscar</td></tr>
<tr><td>[Abel, Adam, Adri],</td><td>adult,</td><td>Otto</td></tr>
</table>

##Workflow deployment
The following subsections explain features used during deployment. Typically, during deployment you have to parameterize you have to 

* Specificy requirements: to ensure there are enough cpu and memory resources
* Get input files: to ensure the input files are loaded before your analysis started
* Put output files: the output files are posted back to central storage

Optionally you may also need to:

* Load binaries: ensure that binaries for your analysis (and their dependencies) are available
* Customized headers: to make the generated scripts fit your cluster system

All of this is described below.

###Requirements specification
The header of a protocol may contain the following line in which you specify the hardware requirements for your workflow step:

>\#MOLGENIS walltime=hh:mm:ss mem=$m$ nodes=$n$ cores=$c$

where `walltime` is the maximum execution time, `mem` is the memory (e.g. 512MB or 4GB), `nodes` is the number of nodes (default=1) and `cores` is the number of cores that you request for the execution of this analysis.

###Get input files
A protocol may be executed in a distributed environment. As a result, the data may not be available on the node where the execution takes place. Therefore, one should first 
the data to the execution node. In some distributed environments this may involve a series of statements that one actually does not want to care about. To make this process easier for our users, we come with the following solution. For every file that you want to use in the analysis protocol, you may include the following statement in the protocol before using it.

>getFile "${myInputFile}"

Where `"myInputFile"` is a parameter in your parameter list that refers to the file. The `getFile` command will then take care of putting your data in the right place.
	
###Put output files
After finishing the analysis, you may save the files you want to keep by including the following statement at the end of your protocol.\\

>putFile "${myOutputFile}"

Where `"myOutputFile"` again is a parameter in your parameter list that refers to the respective file. The `putFile` command takes care of all the work needed to store your data in the right place.

###Load software modules
In your protocols typically will want to use of some software tools that are already installed on the backend where your scripts will be run. However, the path to these tools may vary between different backends. One solution to this is to put the tools in the '$PATH$', so that you can just call them without specifying the path. 
We recommend as best practice to use the [Module system](). On two backends, i.e. 'cluster.gcc.rug.nl' and 'grid.sara.nl', we made it quite easy for you to do so. The following statement will load a tool, say ${yourModule}, to the path.
	
>module load ${yourModule}
	
On the two backends, the following modules are available so far:
	
* bwa/0.5.8c\_patched
* capturing\_kits/SureSelect\_All\_Exon\_30MB\_V2
* capturing\_kits/SureSelect\_All\_Exon\_50MB
* capturing\_kits/SureSelect\_All\_Exon\_G3362
* fastqc/v0.7.0
* fastqc/v0.10.1
* gtool/v0.7.5\_x86\_64
* impute/v2.2.2\_x86\_64\_static
* jdk/1.6.0\_33
* picard-tools/1.61
* plink/1.07-x86\_64
* Python/2.7.3
* R/2.14.2

A protocol that wants use plink, for example, may look like this:
	
>module load plink/1.07-x86\_64
>
>plink --noweb --bfile \$WORKDIR/lspilot1/GvNL\_good\_samples.out4 \  
>--het --out \$WORKDIR/lspilot1/GvNL\_good\_samples.out7

###Customize headers and footers
In the `templates` directory of compute you may edit two files `Header.ftl` and `Footer.ftl`. The contents of this files will be respectively prepended and appended to each of your protocols when generating a workflow. Alternatively, you can change the templates directory using option `-templates=myTemplatesDir`.

##Deployment of the database version of compute

This part of the tutorial explains who to deploy the Database version of the Molgenis/compute and submit jobs to the grid (glite-wms) system using the "pilot" job approach.   

###Requirements

Molgenis/compute can be deployed and ready to submit jobs to the grid scheduler via `ssh` just in few straightforward steps. We prepared a shell scripts to automate every deployment and utilise step. The scripts are can be found in Molgenis github:

>https://github.com/molgenis/molgenis_apps/tree/testing/modules/compute4/deployment/  

###Compute database creation

The "compute" database should be created in the MySQL server (although it can also work on a embedded server). Run the following commands for this:


>mysql #use your login detail here  
>CREATE USER 'molgenis' IDENTIFIED BY 'molgenis';  
>CREATE DATABASE compute;  
>GRANT ALL PRIVILEGES ON compute.* TO 'molgenis'@'\%' WITH GRANT OPTION;  
>FLUSH PRIVILEGES;  

If you have further questions about database creation, please follow the MOLGENIS development manual at http://www.molgenis.org/wiki/MolgenisGuide,

###Start the server

Now, the compute project is built and you can start the web-server and run the DB version of compute with running the following command:  

>cd molgenis\_apps  
>sed -i 's/validate/update/g' build/classes/META-INF/persistence.xml  
>nohup ant -f build\_compute.xml runOn -Dport=8080 \&  

In the nohup.out, you should see output like:

>\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*  
>APPLICATION IS RUNNING AT: http://localhost:8080/compute/  
>\*********************************************************  

Later, you can copy the link into your browser and you will see generated user interface with empty database, like in Figure below. In this example, the DB contains two workflows. 

![An example of Molgenis compute UI](img/workflows.png)

Alternatively, you can use `sh restart.sh` script for it. Run the script specifying the port on which you like to run the web server

>sh restart.sh 8080 #change port if needed  

###Workflow import and execution task generation

You can use the `sh importWorkflow.sh` script to import a workflow into a database. Run it with few parameters: 

>sh importWorkflow.sh \  
>\<workflow\_parameters\_file> \  
>\<workflow\_elements\_file> \  
>\<protocols\_directory>

Alternatively, parameters, protocols and workflow elements can be added to the database manually in the mysql server or through the generated UI.

Run the `sh importWorksheet.sh` script with the following parameters to generate *ComputeTasks*in the database. 

>sh importWorksheet.sh \  
>\<workflow_name> \  
>\<worksheet\_file> \  
>\<run\_id>

where `worksheet_file` is the worksheet file with the targets, `workflow_name` is the workflow name in the database for which you would like to generate tasks and `run_id` is the unique generation run id.

###Execution on the grid with the pilot framework

We use the pilot approach to run ComputeTasks. For this, the pilot files should be present at the execution environment. In case of the grid, you need to copy  three "pilot" files to the grid UI node to \$HOME/maverick/ directory. These files are:

* `maverick.sh`: actual pilot job, that calls back to the database and ask for available for execution ComputeTask
* `maverick.jdl`: jdl file used for submission to the glite grid service (used only for the grid)
* `dataTransferSRM.sh`: script to support data transfer in the grid (used only for the grid)

The files can be found at:

>https://github.com/molgenis/molgenis\_apps/tree/testing/modules/compute/pilots/grid/ 
 
The `maverick.sh` should be edited accordingly to your execution setting. You need to specify `back_end`, where you like to submit you ComputeTask for execution. `back_end` can have a value e.g. `ui.grid.sara.nl`. Also, you need to specify `your_ip` and `your_port` of your web-server, where Molgenis/compute is running.

Also, you may edit the `maverick.jdl` to specify the *walltime* and computational sites where you like to run you analysis. The example jdl requirements look like:

>Requirements = (  
>(other.GlueCEInfoHostName == "ce.lsg.psy.vu.nl" ||  
>other.GlueCEInfoHostName == "ce.lsg.hubrecht.eu")  
>\&\& other.GlueCEPolicyMaxCPUTime >= 1440);  

You Read jdl (job description language) manual for more information.

Besides this, the ip address and port on which Molgenis/compute is running should be specified in the database. You can use importComputeServer.sh for this or manually add the server into the ComputeServer table of the database, where the ComputeServer.name should be specified as "default". The script can used with two parameters:  

>sh importComputeServer.sh \  
>\<ip_address> \  
>\<port> \  

After putting these files in the UI node and adding ComputeServer to the database, \emph{ComputeTasks} can be submitted with the command-line with the `sh runPilots.sh`:

>sh 5\_runPilots.sh \  
>\<backend> \  
>\<username> \  
>\<password> \  
>\<backend_type>  

Here, `back_end` also can value 'ui.grid.sara.nl'. `username` and `password` are user grid credentials.
`backend_type`, in the grid case, should have a value "grid". It also can have a value "cluster", that means that the PBS, SGE or BSUB scheduler is used.

There are following statuses of *ComputeTasks* in the compute database:

* `generated`: means that the task is generated
* `ready`: means that the task is ready for execution (all previous *ComputeTasks* are finished)
* `running`: means that the task is running in the current moment
* `done`: means that the task is finished
* `error` means that the task is finished with the error (hart-beat is not received in time)

During execution, the (output/error) logs of the *ComputeTasks* will be placed back in the compute database and job statuses should be `done` or `error`. The frequency of the database update can be changed in the `maverick.sh` file.  

*Try it out!*





    
         
	

