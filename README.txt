To build QDox there are two prerequisites:

* Maven (beta 8 or later)
This is the build system used instead of Ant. 
http://jakarta.apache.org/maven/
   
* BYacc/J (1.8 or later)
This is the parser generator used to create an effecitive parser for JavaDoc.
If using Windows, no additional installation is needed as yacc.exe is supplied
in the bootstrap directory. If using any other platform, download BYacc/J from
the site below and update build.properties.
http://byaccj.sourceforge.net/

Common build goals:

maven test      - Run all unit tests
maven java:jar  - Create qdox.jar
maven site      - Build the QDox website
maven clean     - Clean up build files

Tip: using 'maven console' can greatly improve build time.