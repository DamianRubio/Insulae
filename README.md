# Insulae
Insulae is presented as a support tool for maritime navigation with which a boat captain can consult information relevant to the route or point where he is, from his position on the bridge. The information can be consumed as it is produced by Spanish weather forecasting agencies, and can be compressed to save space on its transmission or storage. The user can provide the coordinates of at least one point that he wishes to consult and the system will offer him interpretable information according to what was collected by the agency.

## Installation
Insulae has been thought to be a desktop application due to the requirements of its original scope of users. Once the project has been finished, and for distribution purposes, insulae has been packaged in an executable JAR file.


A JAR file, that come from Java archive, is a package file format used to aggregate many Java class files and associated metadata and resources into one file for distribution. These files are archive files that include a file containing metadata about the program itself, called the manifest file. The JAR archives are built on the lossless data compression format known as ZIP. Its extension is known as .jar.


Insulae, as being distributed on an executable jar, does not require any kind of installation in the user’s machine as it is not being installed, just simply executed. For its execution, Insulae needs to find a couple of requirements on the machine that will be exposed in the next section.

## Execution
Insulae has been developed based on the Java programming and language and it is being distributed within an executable JAR file. For its proper execution, the user needs to have installed Java Runtime Environment. The minimum version required for Insulae to run is Java 1.8.0.
This module is distributed as an executable jar that is available here: https://goo.gl/sjMSXD.
The jar needs a configuration file for its proper execution. The configuration file can be found here: https://goo.gl/cHqmwc.
To proceed to a successfull execution the confiuration file must be placed at the same level than the jar. Then, double click on the jar file and Insulae will execute.

## Content
This repository contains the source code of Insulae. For the execution of the program from the source code it lacks of some dependencies that must be included:
* commons-compress-1.15.jar
* GMapsFX-2.12.0.jar
* netcdfAll-4.6.jar
* slf4j-jdk14-1.7.25.jar

## Documentation
This project contains comments in the specific javadoc format that have allowed to generate a complete technical dcumentation of itself. The documentation is available at: https://damianrubio.github.io/Insulae-Documentation/

## Author
Damián Rubio Cuervo.
