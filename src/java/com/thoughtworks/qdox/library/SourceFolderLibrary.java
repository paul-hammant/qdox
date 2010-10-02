package com.thoughtworks.qdox.library;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaSource;

public class SourceFolderLibrary
    extends SourceLibrary
{
    private List sourceFolders = new ArrayList(); // <java.io.File>

    public SourceFolderLibrary( AbstractClassLibrary parent  )
    {
        super( parent );
    }

    public SourceFolderLibrary( File sourceFolder )
    {
        super( );
        this.sourceFolders.add( sourceFolder );
    }

    public SourceFolderLibrary( File sourceFolder, AbstractClassLibrary parent )
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
        for ( Iterator iterator = sourceFolders.iterator(); iterator.hasNext(); )
        {
            File sourceFolder = (File) iterator.next();
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
}
