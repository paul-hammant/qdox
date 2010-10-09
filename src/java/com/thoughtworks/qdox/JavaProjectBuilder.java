package com.thoughtworks.qdox;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;

import com.thoughtworks.qdox.directorywalker.DirectoryScanner;
import com.thoughtworks.qdox.directorywalker.FileVisitor;
import com.thoughtworks.qdox.directorywalker.SuffixFilter;
import com.thoughtworks.qdox.library.ClassLibraryBuilder;
import com.thoughtworks.qdox.library.OrderedClassLibraryBuilder;
import com.thoughtworks.qdox.library.SortedClassLibraryBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaPackage;
import com.thoughtworks.qdox.model.JavaSource;

/**
 * This is the improved version of the JavaDocBuilder. It has the following tasks:
 * <ul>
 *   <li>Provide adders for all kind of resources, such as classloaders, java files and source directories</li>
 *   <li>Provide setters to enable the debug-mode for the Lexer and Parser (which are used when parsing sourcefiles) and the encoding
 *   <li>Provide getter for retrieving Java Object Models from these libraries, such as JavaSources, JavaClasses and JavaPackages</li>
 *   <li>Provide a method to search through all the parsed JavaClasses </li>
 *   <li>Provide store and load methods for the JavaProjectBuilder</li> 
 * </ul>
 * 
 * By default the JavaProjectBuilder will use the {@link} SortedClassLibraryBuilder}, which means it doesn't matter in which order you add the resources,
 * first all sources and sourcefolders, followed by the classloaders. Another implementation for the ClassLibraryBuilder is the
 * {@link OrderedClassLibraryBuilder}, which preserves the order in which resources are added. 
 * By creating a new JavaProjectBuilder with your own ClassLibraryBuilder you can decide which loading strategy should be used.  
 * 
 * @author Robert Scholte
 * @since 2.0
 */
public class JavaProjectBuilder
{
    private final ClassLibraryBuilder classLibraryBuilder;
    
    // Constructors
    
    /**
     * Default constructor, which will use the {@link SortedClassLibraryBuilder} implementation
     */
    public JavaProjectBuilder()
    {        
        this.classLibraryBuilder = new SortedClassLibraryBuilder();
    }

    /**
     * Custom constructor, so another resource loading strategy can be defined
     * 
     * @param classLibraryBuilder custom implementation of {@link ClassLibraryBuilder}
     */
    public JavaProjectBuilder(ClassLibraryBuilder classLibraryBuilder)
    {        
        this.classLibraryBuilder = classLibraryBuilder;
    }

    // Lexer and Parser -setters
    
    /**
     * Enable the debugmode for the Lexer
     * 
     * @param debugLexer true to enable, false to disable
     * @return This javaProjectBuilder itself 
     */
    public JavaProjectBuilder setDebugLexer( boolean debugLexer )
    {
        classLibraryBuilder.setDebugLexer( debugLexer );
        return this;
    }

    /**
     * Enable the debugmode for the Parser
     * 
     * @param debugParser true to enable, false to disable
     * @return This javaProjectBuilder itself
     */
    public JavaProjectBuilder setDebugParser( boolean debugParser )
    {
        classLibraryBuilder.setDebugParser( debugParser );
        return this;
    }

    /**
     * Sets the encoding when using Files or URL´s to parse.
     * 
     * @param encoding
     * @return
     */
    public JavaProjectBuilder setEncoding( String encoding )
    {
        classLibraryBuilder.setEncoding( encoding );
        return this;
    }

    public JavaSource addSource(File file) throws IOException, FileNotFoundException {
        return classLibraryBuilder.addSource( file );
    }
    
    // Resource adders
    
    public JavaSource addSource(Reader reader) {
        return classLibraryBuilder.addSource( reader );
    }

    public void addSourceFolder( File sourceFolder )
    {
        classLibraryBuilder.appendSourceFolder( sourceFolder );
    }

    public void addSourceTree( File file )
    {
        FileVisitor errorHandler = new FileVisitor() {
            public void visitFile(File badFile) {
                throw new RuntimeException("Cannot read file : " + badFile.getName());
            }
        };
        addSourceTree(file, errorHandler);        
    }

    public void addClassLoader( ClassLoader classLoader )
    {
        classLibraryBuilder.appendClassLoader( classLoader );
    }

    public void addSourceTree( File file, final FileVisitor errorHandler )
    {
        DirectoryScanner scanner = new DirectoryScanner(file);
        scanner.addFilter(new SuffixFilter(".java"));
        scanner.scan(new FileVisitor() {
            public void visitFile(File currentFile) {
                try {
                    addSource(currentFile);
                } catch (IOException e) {
                    errorHandler.visitFile(currentFile);
                }
            }
        });
    }

    // Java Object Model -getters
    
    public JavaClass getClassByName( String name )
    {
        return classLibraryBuilder.getClassLibrary().getJavaClass( name );
    }
    
    public JavaSource[] getSources() {
        return classLibraryBuilder.getClassLibrary().getJavaSources();
    }

    public JavaClass[] getClasses()
    {
        return classLibraryBuilder.getClassLibrary().getJavaClasses();
    }

    public JavaPackage getPackageByName( String name )
    {
        return classLibraryBuilder.getClassLibrary().getJavaPackage( name );
    }
    
    public JavaPackage[] getPackages()
    {
        return classLibraryBuilder.getClassLibrary().getJavaPackages();
    }

    // Searcher
    
    public JavaClass[] search( Searcher searcher )
    {
        List results = new LinkedList();
        JavaClass[] classArray = classLibraryBuilder.getClassLibrary().getJavaClasses();
        for (int classIndex = 0;classIndex < classArray.length; classIndex++) {
            JavaClass clazz = classArray[classIndex];
            if (searcher.eval(clazz)) {
                results.add(clazz);
            }
        }
        return (JavaClass[]) results.toArray( new JavaClass[0] );
    }

    /**
     * Persist the classLibraryBuilder to a file
     * 
     * @param file
     * @throws IOException
     */
    public void save( File file ) throws IOException
    {
        FileOutputStream fos = new FileOutputStream(file);
        ObjectOutputStream out = new ObjectOutputStream(fos);
        try {
            out.writeObject(classLibraryBuilder);
        } finally {
            out.close();
            fos.close();
        }
    }

    /**
     * Note that after loading JavaDocBuilder classloaders need to be re-added.
     */
    public static JavaProjectBuilder load(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        ObjectInputStream in = new ObjectInputStream(fis);
        JavaProjectBuilder builder;
        try {
            ClassLibraryBuilder libraryBuilder = (ClassLibraryBuilder) in.readObject();
            builder = new JavaProjectBuilder(libraryBuilder);
        } catch (ClassNotFoundException e) {
            throw new Error("Couldn't load class : " + e.getMessage());
        } finally {
            in.close();
            fis.close();
        }
        return builder;
    }
}
