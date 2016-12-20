# QDox

### Status

[![Build Status](https://travis-ci.org/paul-hammant/qdox.png)](https://travis-ci.org/paul-hammant/qdox)

QDox is a high speed, small footprint parser for fully extracting class/interface/method definitions (including annotations, parameters, param names). It is designed to be used by active code generators or documentation tools.

Not so relevant any more, but it also also processes JavaDoc @tags

# Migration from Codehaus

This project used to be on Codehaus, in Subversion. The trunk of that has been git-svn-cloned to here. Maven repos have the sources jars for released versions of Qdox.  The [old issues from codehaus are hosted statically on a GH-pages repo](http://paul-hammant.github.io/Old_Qdox_Issues/)

# Download

Maven's central repo [holds versions of QDox](http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22com.thoughtworks.qdox%22%20AND%20a%3A%22qdox%22)

# In A Nutshell

A custom built parser has been built using JFlex and BYacc/J. These have been chosen because of their proven performance and they require no external libraries at runtime.

The parser skims the source files only looking for things of interest such as class/interface definitions, import statements, JavaDoc and member declarations. The parser ignores things such as actual method implementations to avoid overhead (while in method blocks, curly brace counting suffices).

The end result of the parser is a very simple document model containing enough information to be useful.

# Frequently Asked Questions

## General

### What's the object type of an interface?

The JavaClass method is used to represent both classes and interfaces. The isInterface() method allows you to distinguish between the two.

When using a class, the getSuperClass() return which class is extended. If this has not been defined in the input source code, java.lang.Object is returned. When using an interface, this method ALWAYS returns null.

When using a class, the getImplements() returns an array of the interfaces implemented by the class. If none are implemented, an empty array is returned. When using an interface, this returns an array of interfaces the current interface EXTENDS.

## Can I have full control over the classloader?

I some cases QDox is used to generate classes for another project with it's own dependencies. This could result in class-collision. By default the JavadocBuilder will contain the classloader(s) of the current project, but by defining your own classLibrary you can have the required control.

```java
/* new ClassLibrary() will give you an empty classLoader
 * Big chance you want at least the system classloader.
 */
ClassLibraryBuilder libraryBuilder = new SortedClassLibraryBuilder(); //or OrderedClassLibraryBuilder()
libraryBuilder.addClassLoader( ClassLoader.getSystemClassLoader() );
JavaProjectBuilder builder = new JavaProjectBuilder( libraryBuilder );
```

## I'm getting an ArrayIndexOutOfBoundsException: 500. What to do?

During the parsing of java files the Parser needs to remember states, which are kept in a stack. Due to recursive calls the stack can become very large. By default the size of this this stack is 500 and it can only be set during compile-time of QDox. Normally 500 per sourcefile will do, but in very, very rare cases this might be too little. The only way to increase this number is by rebuilding it. Download the sources and build it like mvn install -Dqdox.javaparser.stack=750 if you want to change it to 750.

# Download

QDox is available at the Maven Central. To include the most recent of QDox in your pom, include the following dependency:

```xml
  	 <dependency>
  	   <groupId>${project.groupId}</groupId>
  	   <artifactId>${project.artifactId}</artifactId>
  	   <version>${project.version}</version>
  	 </dependency>
```

Latest stable release - QDox ${project.rel.org.thoughtworks.qdox:qdox}: binary jar | sources jar | javadoc jar | project tar.bz2 | project tar.gz | project zip

# What is using QDox

Project	Description	How QDox is Used
AspectWerkz	AspectWerkz is an Aspect Oriented Programming (AOP) toolkit for Java that modifies byte-code to weave in interceptors and mixins.	Attributes can be used to associate interceptors and mixins with a specific class.

## Avalon Phoenix

Phoenix was a micro-kernel designed and implemented on top of the Avalon framework. It provided a number of facilities to manage the environment of Server Applications. Apache cancelled the Avalon project 2004	'MetaGenerate' is part of the Phoenix toolset and picks up @phoenix prefixed JavaDoc tags to make XML manifests for the components that will be laced together in more XML to make a complete server application.

## Cocoon
Apache Cocoon is an XML publishing framework that raises the usage of XML and XSLT technologies for server applications to a new level. Designed for performance and scalability around pipelined SAX processing, Cocoon offers a flexible environment based on a separation of concerns between content, logic, and style.	The QDox Block reads in Java source files and fires out SAX events enabling processing by standard XML tools (such as XSLT).

## Commons Attribute

Jakarta Commons Attributes provides an API to runtime metadata attributes. Attributes are specified as doclet tags and then compiled into the classpath where they can be accessed at runtime (without requiring the source). The aim is to provide a framework similar to .NET attributes.	QDox is used to extract the JavaDoc tags from the source files.

## GWT-maven-plugin

The gwt-maven-plugin provides support for GWT projects, including running the GWT developer tools (the compiler, shell and i18n generation) and performing support operations to help developers make GWT fit more closely in with their standard JEE web application development (debugging, managing the embedded server, running noserver, merging web.xml, generating I18N interfaces, generating Async GWT-RPC interfaces, and more) and environment (Eclipse IDE).	QDox is used to generate the Async GWT-RPC interfaces based on their Service-classes.

## Ivory

Ivory provides easy integration between your exiting Java classes, Avalon services, and Axis. It allows easy deployment of soap services with none of the WSDD configuration that Axis normally mandates.	Attributes are used to provide additional hints to Ivory about Java classes that cannot be determined via reflection.

## maven-javadoc-plugin

The Javadoc Plugin uses the Javadoc tool to generate javadocs for the specified project.	When developers write code, they could forget to create (or update) the Javadoc comments. The <fix> and <test-fix> goals are interactive goals to fix the actual Javadoc comments.

Maven 2 & 3

## Maven is a software project management and comprehension tool.	QDox is used for extraction of Javadoc tags from source files to generate plugin descriptors

## Mock Maker

Mock Maker is a tool for automatically generating mock objects from custom classes to aid in testing the untestable. This supports the practises of Test Driven Development and eXtreme Programming. Mock Maker scans the source repository for any class/interface marked with the @mock JavaDoc tag and automatically creates the Mock Object for this that matches the class definition.

## Nanning

Nanning is an Aspect Oriented Programming (AOP) toolkit for Java that does not require a custom compiler. It uses dynamic proxies to weave in interceptors and mixins at runtime.	QDox is used to allow aspects to be applied to classes by specifiying meta-data in doclet tags.

## Paranamer

Paranamer a mechamism for accessing the parameter names of methods of Java classes compiled into jars. QDox is used to parse the source and generate the parameter names list.

## Spring ME

A version of Spring that not only has a very small runtime footprint (none), but also is capable of running on small handheld devices, since it does not rely on reflection.

## vDoclet

vDoclet is a framework for code-generation using Velocity templates, based on annotated Java source-code. vDoclet uses QDox to produce input-data for it's templates.

## Voruta

Voruta is a data access framework for embedding SQL statements in Java methods using custom JavaDoc tags and dynamic code generation at runtime. QDox is used to parse metadata and CGLib to generate implementation at runtime.

## XDoclet2

XDoclet2 is a framework for code-generation using Velocity or Jelly templates, based on annotated Java source-code. It is a rewrite of XDoclet.

XDoclet2 uses QDox to produce input-data for it's templates, as well as QDox' APITestCase to validate the generated sources.

# QDox Usage

## Entry Point

JavaProjectBuilder is the entry point to QDox. It is responsible for parsing source code, resolving imports and storing the data.

To create it, all you need to do is call the default constructor.

```java
JavaProjectBuilder builder = new JavaProjectBuilder();
```

## Reading Source Files

Java source code can then be added to the JavaProjectBuilder. Source can either be read one file at a time (using a java.io.Reader) or an entire source tree can be added recursively.

```java
// Reading a single source file.
builder.addSource(new FileReader("MyFile.java"));

// Reading from another kind of input stream.
builder.addSource(new StringReader("package test; public class Hello {}"));

// Adding all .java files in a source tree (recursively).
builder.addSourceTree(new File("mysrcdir"));
```

## Resolving Class Names

In order to resolve classes that have been imported using a wildcard (e.g. import java.util.*;), the ClassLibrary must be aware of other classes used in the project.

ClassLibrary has 4 ways to resolve classes:

* By looking at other sources that have been added.
* By searching through the supplied sourceFolders
* By looking in the current classpath (including the standard JRE classes).
* By looking at additional ClassLoaders specified at runtime.

All sources and sourcetrees added to the JavaProjectBuilder will be parsed. This is often much more than required. To increase efficiency use the ClassLibrary to add sourcefolders. Consider these files as lazy parsed sources.

The current classpath is automatically set by JavaProjectBuilder. In most cases this shall be sufficient, however in some situations you may want resolve the full classes in external libraries.

```java
// Get the ClassLibrary
JavaProjectBuilder builder = new JavaProjectBuilder();

// Add a sourcefolder;
builder.addSourceFolder( new File( "src/main/java" ) );
builder.addSourceFolder( new File( "target/generated-sources/foobar" ) );

// Add a custom ClassLoader
builder.addClassLoader( myCustomClassLoader );

// Ant example : add the <classpath> element's contents
builder.addClassLoader( new AntClassLoader( getProject(), classpath ) );
```

It is important that additional ClassLoaders are added before any source files are parsed.

## Navigating The Model

Now the files have been parsed, move on to navigating the model.

# The Model

After the source code has been parsed, the content of the files can be navigated using a simple to use and intuitive object model.

## JavaSource

Represents a complete .java file. This contains a collection of classes.

Example Input

```java
package com.blah.foo;

import java.awt.*;
import java.util.List;

public class Class1 {
  ...
}

class Class2 {
}

interface Interface1 {
}
```

```java            
Example Code
JavaProjectBuilder builder = new JavaProjectBuilder();
JavaSource src = builder.addSource(myReader);

JavaPackage pkg      = src.getPackage();
List<String> imports     = src.getImports(); // {"java.awt.*",
                                     //  "java.util.List"}

JavaClass class1     = src.getClasses().get(0);
JavaClass class2     = src.getClasses().get(1);
JavaClass interface1 = src.getClasses().get(2);
```    

## JavaPackage

Represents the package of the class.

Example input

```java
package com.blah.foo;

public class BarClass  {
...
}
 ```

Example Code

```java
JavaProjectBuilder builder = new JavaProjectBuilder();
JavaSource src = builder.addSource(myReader);

JavaPackage pkg      = src.getPackage();

Collection<JavaClass> classes  = pkg.getClasses(); // BarClass
String name          = pkg.getName(); // "com.blah.foo"
String toString      = pkg.toString(); // "package com.blah.foo" conform javaAPI
JavaPackage parent   = pkg.getParentPackage(); //
```

## JavaClass

Represents a class or interface. This contains doclet tags, fields and methods. Information about the class definition is available, such as which classes are extended, which interfaces implemented and modifiers.

```java
Example Input
package com.blah.foo;

import java.io.*;
import com.custom.*;
import com.base.SubClass;

/**
 * @author Joe
 */
public abstract class MyClass extends SubClass
            implements Serializable, CustomInterface  {

  private String name;
  public void doStuff() { ... }
  private int getNumber() { ... }

}
```

Example Code

```java
JavaProjectBuilder builder = new JavaProjectBuilder();
builder.addSource(myReader);

JavaClass cls = builder.getClassByName("com.blah.foo.MyClass");

String pkg      = cls.getPackage();            // "com.blah.foo"
String name     = cls.getName();               // "MyClass"
String fullName = cls.getCanonicalName(); // "com.blah.foo.MyClass";
String canonicalName = cls.getFullyQualifiedName(); // "com.blah.foo.MyClass";
boolean isInterface = cls.isInterface();       // false

boolean isPublic   = cls.isPublic();   // true
boolean isAbstract = cls.isAbstract(); // true
boolean isFinal    = cls.isFinal();    // false

JavaType superClass = cls.getSuperClass(); // "com.base.SubClass";
List<JavaType> imps     = cls.getImplements(); // {"java.io.Serializable",
                                       //  "com.custom.CustomInterface"}

String author = cls.getTagsByName("author").getValue(); // "joe"

JavaField nameField = cls.getFields()[0];
JavaMethod doStuff = cls.getMethods()[0];
JavaMethod getNumber = cls.getMethods()[1];

JavaSource javaSource = cls.getParentSource();
```

## JavaField

Represents a field in a class. This has doclet tags, a name and a type.

Example Input

```java
import java.util.Date;

public class MyClass  {

  /**
   * @magic
   */
  private String email;

  public static Date[][] dates;

}
```    

Example Code

```java
JavaField e = cls.getFields()[0];

JavaType eType     = e.getType(); // "java.lang.String";
String eName   = e.getName(); // "email";
DocletTag eTag = e.getTagsByName("magic"); // @magic
boolean eArray = e.getType().isArray(); // false;

JavaField d = cls.getFields()[1];

JavaType dType     = d.getType(); // "java.util.Date";
String dName   = d.getName(); // "dates";
DocletTag dTag = d.getTagsByName("magic"); // null
boolean dArray = d.getType().isArray(); // true;
int dDimensions= d.getType().getDimensions(); // 2;
boolean dStatic= d.isStatic(); // true;

JavaClass javaClass = d.getParentClass();
```

## JavaMethod

Represents a method in a class. This has doclet tags, a name, return type, parameters and exceptions.

Example Input

```java
import java.util.Date;
import java.io.*;

public class MyClass  {

  /**
   * @returns Lots of dates
   */
  public static Date[] doStuff(int number,
                               String stuff)
            throws RuntimeException, IOException {
    ...
  }

}
```

Example Code

```java
JavaMethod m = cls.getMethods()[0];

String mName = m.getName(); // "doStuff";
JavaType mReturns = m.getReturns(); // "java.util.Date";
boolean mArray = m.getReturns().isArray(); // true
boolean mStatic = m.isStatic(); // true
boolean mPublic = m.isPublic(); // true

String doc = m.getTagByName("returns").getValue();
  // "Lots of dates"

List<JavaType> exceptions = m.getExceptions();
  // {"java.lang.RuntimeException", "java.io.IOException"}

JavaParameter numberParam = m.getParameters()[0];
JavaParameter stuffParam = m.getParameters()[1];

JavaClass javaClass = m.getParentClass();
```

## JavaParameter

Represents a parameter passed to a method. This has a name and a type.

```java
Example Input
public class MyClass  {

  public void stuff(int n, Object[] objects) {
    ...
  }

}
```

Example Code

```java
JavaMethod m = cls.getMethods()[0];

JavaParameter n = m.getParameters()[0];
String nName = n.getName(); // "n"
JavaType nType   = n.getType(); // "int";

JavaParameter o = m.getParameters()[1];
String oName   = o.getName(); // "objects"
JavaType oType     = o.getType(); // "java.lang.Object";
boolean oArray = o.getType().isArray(); // true

JavaMethod javaMethod = o.getParentMethod();
```

## JavaType

Represents a specific instance of a class used by another class (such as return value, superclass, etc). The value represents the name of the class. Array dimensions are also available. Since 1.8 it's also possible to get the generic value of the Type

Example Input

```java  
import java.util.*;

public class MyClass  {

  public void stuff(int n, Object[] objects,
	                  Date[][] dates, List<String> stringList) {
    ...
  }

}
```    

Example Code


```java
JavaMethod m = cls.getMethods()[0];

JavaType returns = m.getReturns();
returns.getValue(); // "void"
returns.isArray(); // false
returns.getDimensions(); // 0

JavaType n = m.getParameters()[0].getType();
n.getValue(); // "int"
n.isArray(); // false
n.getDimensions(); // 0

JavaType objects = m.getParameters()[1].getType();
objects.getValue(); // "java.lang.Object"
objects.isArray(); // true
objects.getDimensions(); // 1

JavaType dates = m.getParameters()[2].getType();
dates.getValue(); // "java.util.Date"
dates.isArray(); // true
dates.getDimensions(); // 2

JavaType stringList = m.getParameters()[3].getType();
stringList.getValue(); // "java.util.List"
stringList.getGenericValue(); // "java.util.List<java.lang.String>"
stringList.isArray(); // false
stringList.getDimensions(); // 0
```

## DocletTag

Represents a JavaDoc tag. Each tag has a name and a value. Optionally, the value can be broken up into tokens accessed by index or name.

The JavaClass, JavaField and JavaMethod classes all support comments and DocletTags

The returned DocletTag carries the name, value and methods for breaking up the value into specific parameters.

Example Input

```java
/**
 * This method does nothing at all.
 *
 * @returns A boolean of whether we care or not.
 * @param email Someone's email address.
 * @param dob Date of birth.
 *
 * @permission administrator full-access
 * @webservice publish=true name=myservice type=rpc
 */
boolean doWeCare(String email, Date dob);
```

Example Code

```java
JavaMethod mth = cls.getMethods()[0];

// Access the JavaDoc comment
String comment = mth.getComment();
// "This method does nothing at all."

// Access a single doclet tag
DocletTag returns = mth.getTagByName("returns");
returns.getName(); // "returns";
returns.getValue(); // "A boolean of whether we care or not."

// Access multiple doclet tags with the same name
DocletTag[] params = mth.getTagsByName("param");
params[0].getValue(); // "Someone's email address."
params[1].getValue(); // "Date of birth."

// Access specific parameters of a doclet tag by index
DocletTag permission = mth.getTagByName("permission");
permission.getParameter[0]; // "administrator"
permission.getParameter[1]; // "full-access"

// Access specific parameters of a doclet tag by name
DocletTag webservice = mth.getTagByName("webservice");
webservice.getNamedParameter("type"); // "rpc"
webservice.getNamedParameter("name"); // "myservice"
```

# Building QDox

Snapshot deployments: snapshots directory.

To build QDox there are two prerequisites:

Key Dependencies:

1. [Maven 3](http://maven.apache.org)

2. [BYacc/J](http://byaccj.sourceforge.net)

Paarser generator used to create an effective parser for JavaDoc.
If using Windows, Linux, Solaris or Mac OS X, no additional installation is
needed as yacc binaries are supplied in the bootstrap directory.
If using any other platform, download BYacc/J from the site or build it yourself, in which case
we would be grateful to receive a copy of your binary, so we can upgrade the bootstrap support.

Build goals:

mvn package				- Create qdox jar
mvn generate-sources 	- Generate the Java parser code (allowing you to develop in an IDE).
mvn site      			- Build the QDox website
mvn release:prepare		- Prepare release (confirm or change release version interactively)
mvn release:perform		- Perform release (perform release from tag of prepare phase)
