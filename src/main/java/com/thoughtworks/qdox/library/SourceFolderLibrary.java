package com.thoughtworks.qdox.library;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.MalformedURLException;
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

    @Override
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
                    JavaSource source = parse( new FileReader( classFile ), classFile.toURI().toURL() );
                    result = source.getNestedClassByName( className );
                }
                catch ( FileNotFoundException e )
                {
                }
                catch ( MalformedURLException e )
                {
                }
            }
        }
        return result;
    }
    
    /**
     * Loops over the sourceFolder to find a classReference.
     * It will try to map the className to a file.
     * 
     */
    @Override
    protected boolean containsClassReference( String className )
    {
        boolean result = false;
        for ( Iterator<File> iterator = sourceFolders.iterator(); !result && iterator.hasNext(); )
        {
            File sourceFolder = (File) iterator.next();
            String mainClassName = className.split( "\\$" )[0];
            File classFile = new File( sourceFolder, mainClassName.replace( '.', File.separatorChar ) + ".java" );
            
            result = ( classFile.exists() && classFile.isFile() );
        }
        return result;
    }
    
}
