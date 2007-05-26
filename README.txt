To build QDox there are two prerequisites:

* Maven 
This is the build system used instead of Ant. 
http://maven.apache.org
 
* BYacc/J (1.8 or later)
This is the parser generator used to create an effecitive parser for JavaDoc.
If using Windows, Linux, Solaris or OS-X, no additional installation is 
needed as yacc binaries are supplied
in the bootstrap directory. If using any other platform, download BYacc/J from
the site below and update build.properties.
http://byaccj.sourceforge.net/

(You might have to download it via CVS and build it yourself, in which case
we would be grateful to receive a copy of your binary).

Build goals:

mvn install				- Create qdox jar
mvn generate-sources 	- Generate the Java parser code (allowing you to develop in an IDE).
mvn site      			- Build the QDox website
