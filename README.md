[![Build Status](http://www.falc-sim.org/images/falc_entire_logo_v03.png)](http://www.falc-sim.org)

## Overview

FALC enables simulations of the (future) development of population, jobs, construction, real estate demand, transport, and their interaction effects. The models include a large amount of information: for example, age and income of residents, commuting ties as well as the spatial distribution of the companies in various industries. 

The code published in GitHub includes the core of FaLC simulation software and basically involves models of the annual cycles. These are processed sequentially in each simulation cycle. In a first step, distances may are recalculated and changed assumptions are imported. Afterwards, demographic models are processed before the companies may change. 

FaLC-core published in GitHub runs independently of additional FaLC-software packages - e.g. FaLC-pro. Indeed, a GUI and other packages such as a Transport Module or a Synthetic Populatio Modeler are available under [www.falc-sim.org](http://www.falc-sim.org).

To allow the user the greatest possible flexibility in the model design, the code of FaLC-core is openly available and adaptable. Therefore, the core of FaLC is published under a Dual Licence: Open-Source-Licence ([AGPL Version 3](http://www.gnu.org/licenses/licenses.en.html)) as well as under the proprietary [FaLC-pro-License] http://www.falc-sim.org/documents/FaLC-pro_Licence-Agreement_en.pdf). 

## Hardware

FaLC is able to run small projects on 32bit, single core Linux or Windows environment. However, to improve memory usage and increase simulation performance by multi thread computing, we recommed to use 64bit multi core systems.

## Installation

**Prerequisites**

1. Download and install required software
	- JDK 1.7 or above (http://java.oracle.com)
	- Maven 3.0.5 (http://maven.apache.org)
	- Eclipse Luna or above (http://www.eclipse.org)

2. Configure environment variables 
	- JAVA_HOME - root of JDK
	- M2 - bin directory of maven
	- M2_HOME - root of maven
	- MAVEN_OPTS - java options for maven environment like -Xms2G
	
3. Install additional eclipse plugin for SVN, AspectJ and m2e (if not yet installed)


**Build FaLC**

1. Checkout FaLC project from SVN. Now the project is organized as:
	- falc-sim-impl
	- falc-sim-service
	- falc-sim-util
	
2. Run target "mvn package -DskipTests" on parent project falc-sim - this builds also all sub-projects and downloads needed packages
	>Other usable targets:
	* mvn clean					- clean project
	* mvn compile					- compile project
	* mvn {target} -DskipTests			- run target but do not run defined tests
	* mvn install					- install library to the repository	

3. Create build, run and debug configurations for project


**Run sample project**

1. Sample FaLC projects are stored in independent GitHUB project FaLC-Templates. 
    - FaLC_template_sg-ar-ai_1_1_5.proj

2. Run falc using command line 
	- java -Xms1G -Xmx4G -jar falc-sim-impl-1.1.0.jar -Dproject.name="full path to FaLC project directory"
	
	For more information, check "run-info" directory

3. Comment
	- falc creates log file "\log\falc.log" during run with detailed information.