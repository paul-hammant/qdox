To build QDox there are two prerequisites:

* Maven 2 (2.0.9 or later)
http://maven.apache.org
 
* BYacc/J (1.8 or later)
http://byaccj.sourceforge.net/
Paarser generator used to create an effective parser for JavaDoc.
If using Windows, Linux, Solaris or Mac OS X, no additional installation is 
needed as yacc binaries are supplied in the bootstrap directory. 
If using any other platform, download BYacc/J from the site or build it yourself, in which case
we would be grateful to receive a copy of your binary, so we can upgrade the bootstrap support.

Build goals:

mvn install				- Create qdox jar
mvn generate-sources 	- Generate the Java parser code (allowing you to develop in an IDE).
mvn site      			- Build the QDox website
mvn release:prepare		- Prepare release (confirm or change release version interactively)
mvn release:perform		- Perform release (perform release from tag of prepare phase)

If you are releasing, remember to 

1) Update src/site/content/download.html
2) Go to JIRA and release the applicable version for QDOX : http://jira.codehaus.org/secure/project/ManageVersions.jspa?pid=10103
3) Copy the contents of target/site/ to the DAV folder for QDox's website : https://dav.codehaus.org/qdox/