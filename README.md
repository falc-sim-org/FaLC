[![Build Status](http://www.falc-sim.org/images/falc_entire_logo_v03.png)](http://www.falc-sim.org)

## Overview

FALC enables simulations of the (future) development of population, jobs, construction, real estate demand, transport, and their interaction effects. The models include a large amount of information: for example, age and income of residents, commuting ties as well as the spatial distribution of the companies in various industries. 

## Development

Project installation:

1. download and install JDK 1.7 or above (http://java.oracle.com), Maven 3.0.5 (http://maven.apache.org), Eclipse Luna or above (http://www.eclipse.org)

2. configure environment variables 
	JAVA_HOME - root of JDK
	M2 - bin directory of maven
	M2_HOME - root of maven
	MAVEN_OPTS - java options for maven environment like -Xms2G
	
3. install to eclipse plugin SVN, AspectJ and m2e (if not yet installed)

4. checkout FaLC project from SVN. Now the project is organized as:
	- falc-sim
	-- falc-sim-impl
	-- falc-sim-service
	-- falc-sim-util
	
5. run target "mvn package -DskipTests" on parent project falc-sim - this builds also all sub-projects and downloads needed packages
	other targets:
		mvn clean					- clean project
		mvn compile					- compile project
		mvn {target} -DskipTests	- run target but do not run defined tests
		mvn install					- install library to the repository	

6. create build, run and debug configurations for project


Run sample project:

1. check sample project in "FaLC_template_sg-ar-ai_1_1_5.proj" sub-directory

2. Run falc using command line 
	java -Xms1G -Xmx4G -jar falc-sim-impl-1.1.0.jar -Dproject.name="full path to project"
	
	For more information, check "run-info" directory