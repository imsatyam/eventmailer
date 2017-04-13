# eventmailer
A utility to send emails for the events.
It makes use of gmail, google drive and OAuth to read the events in an excel file and send emails as per the events.
Code is extensible and can be easily extended to read data from any source and send email using any API.

## Pre-requisite
1. You need to have an email id to send the email.
2. You'll have to enable the permissions to compose and send email via gmail in Google API console.
3. You'll have to enable the permission to iterate the google drive documents. 

## To run this project:
1. Checkout the project and go to JobRunner.java
2. Job runner expects a configuration file to run. This file provides the necessary information for the job to run. A sample configuration file is like:

  <configuration>
	
    <application id="{param1-id}">
      <applicationName>{param2-applicationName}</applicationName>
      <clientSecretFile>{param3-secretKeyFile}</clientSecretFile>
    </application>

    <user>
      <firstName>{param4-yourFirstName}</firstName>
      <lastName>{param5-yourLastName}</lastName>
      <petName>{param6-yourPetName}</petName>
      <dateOfBirth>{param7-yourDob}</dateOfBirth>
      <email>{param8-yourEmail}</email>
    </user>

    <repository id="{param9-whereToReadEventsFrom}">
      <repositoryType>{param10-repositoryType}</repositoryType>
      <dataTable id="{param11-repositoryName}" url="{param12-repositoryUrl}" username="" password="" />
    </repository>

  </configuration>
  
  Let us have a look at the parameters:
  
  ## {param1-id}
  It is the type of event. e.g. personalEvent. Other possible values are calendarEvent, meeting etc.
  This ID is used to retrieve the correct event processor from EventProcessorFactory.java
  
  ## {param2-applicationName}
  It is the name of the application. I call it - Event Book of Satyam.
  If you are using google API. This name is to be mentioned at google API console.
  
  ## {param3-secretKeyFile}
  To connect to Google API or any other OAUTH API, we may need a secret key file. The url of secret key file goes in here.
  
  ## {param4-yourFirstName}  to {param8-yourEmail}
  These parameters capture your details, details of the user. 
  Your first name will be sent in the signature of email if you have not mentioned a signature explicitly.
  Your email id will be sent in Reply To field of email.
  
  ## {param9-whereToReadEventsFrom}
  Where are you keeping the events. Code is extensible and can be made to read from anywhere.
  If you are keeping the data somewhere on Google, put this field as *google*. 
  This will be further used to retrieve the data service API from the factory.
  
  ## {param10-repositoryType}
  How are you keeping data? Simplest way is to keep data in an excel. If you are using excel, mention this as *excel*
  
  ## {param11-repositoryName}
  What is the name of the data repository? 
  In case it is excel and it is meant for personal events, you may just name the file as people.xlsx
  Valud of the parameter in this case will be *people*.
  
  ## {param12-repositoryUrl}
  In case you are using an excel then this represents the url of the file on say google drive. 
