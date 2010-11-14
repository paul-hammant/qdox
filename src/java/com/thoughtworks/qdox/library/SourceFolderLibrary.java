package com.thoughtworks.qdox.library;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaSource;

/**
 * 
 * @author Robert Scholte
 * @since 2.0
 */
public class SourceFolderLibrary
    extends SourceLibrary
{
    private List<File> sourceFolders = new LinkedList<File>();

    public SourceFolderLibrary( AbstractClassLibrary parent  )
    {
        super( parent );
    }

    public SourceFolderLibrary( AbstractClassLibrary parent, File sourceFolder )
    {
        super( parent );
        this.sourceFolders.add( sourceFolder );
    }

    public void addSourceFolder( File sourceFolder )
    {
        this.sourceFolders.add( sourceFolder );
    }

    /**
     * {@inheritDoc}
     */
    protected JavaClass resolveJavaClass( String className )
    {
        JavaClass result = null;
        for ( File sourceFolder : sourceFolders )
        {
            String mainClassName = className.split( "\\$" )[0];
            File classFile = new File( sourceFolder, mainClassName.replace( '.', File.separatorChar ) + ".java" );
            if ( classFile.exists() && classFile.isFile() )
            {
                try
                {
                    JavaSource source = parse( new FileReader( classFile ) );
                    result = source.getNestedClassByName( className );
                }
                catch ( FileNotFoundException e )
                {
                }
            }
        }
        return result;
    }
    
    /**
     * Loops over the sourceFolder
     * 
     */
    protected boolean containsClassReference( String className )
    {
        boolean result = false;
        for ( Iterator<File> iterator = sourceFolders.iterator(); !result && iterator.hasNext(); )
        {
            File sourceFolder = (File) iterator.next();
            String mainClassName = className.split( "\\$" )[0];
            File classFile = new File( sourceFolder, mainClassName.replace( '.', File.separatorChar ) + ".java" );
            
            //@todo check if source contains the classname
            //@todo if not, check all files in this packages and check if it's there.
//            try
//            {
//                JavaSource source = addSource( classFile );
//            }
//            catch ( ParseException e )
//            {
//                //ignore 
//            }
//            catch ( IOException e )
//            {
//            }
            result = ( classFile.exists() && classFile.isFile() );
//            if( !result ) {
//                
//            }
        }
        return result;
    }
    
}
