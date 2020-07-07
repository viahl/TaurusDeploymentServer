#Deploymentserver#

## Purpose ##

When working with several Docker image, microservice with dependency. It can be difficult to know which ones is alive and which service is working
as intende. __*Deploymentserver*__ is an simple way to download from an git-url to the server. Then run the docker. 
When the Docker have started the UI will indicate with green/read/yellow signal if something is working or not.


### Start Deploymentserver ###
To start the deploymentserver.
1. Open a terminal.
1. Go to the directory deploymentserver-backend
1. type mvn spring-boot:run
1. Press enter.

### Build Deploymentserver ###

At present time there is an circular dependency between the backend and web module.
The backend needs to be compiled first with mvn clean install or package.
After that the same with the web module.

### Known bugs ###

When downloading from git and keep track on what happends in the git project.
The git project needs to have a attachment. This has not yet been added to the project.

## Prerequest for the project ##

At present time the Deploymentserver is been coded for maven/dockers projects.

The maven project needs to have the following properties
deployment.ping  [url to ping]
deployment.db.ping [url to ping if the database is working. Optional if there is no database connection]
deployment.host [The host to ping, i.e. docker information]