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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaModule;
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

    public JavaModule addSourceFolder( File sourceFolder )
    {
        this.sourceFolders.add( sourceFolder );
        return resolveJavaModule( sourceFolder );
    }

    @Override
    public Collection<JavaModule> getJavaModules()
    {
        return resolveJavaModules();
    }
    
    private Collection<JavaModule> resolveJavaModules()
    {
        Collection<JavaModule> modules = new ArrayList<JavaModule>(sourceFolders.size());
        for ( File sourceFolder : sourceFolders )
        {
            JavaModule module = resolveJavaModule( sourceFolder );
            if( module != null)
            {
                modules.add( module );
            }
        }
        return modules;
    }
    
    private JavaModule resolveJavaModule( File sourceFolder )
    {
        JavaModule result = null;
        File moduleInfoFile = new File( sourceFolder, "module-info.java" );
        if ( moduleInfoFile.isFile()  )
        {
            try
            {
                result = parse( new FileReader( moduleInfoFile ), moduleInfoFile.toURI().toURL() ).getModuleInfo();
            }
            catch ( FileNotFoundException e )
            {
                // noop
            }
            catch ( MalformedURLException e )
            {
               // noop
            }
        }
        return result;
    }

    @Override
    protected JavaClass resolveJavaClass( String className )
    {
        JavaClass result = super.resolveJavaClass( className );
        for ( File sourceFolder : sourceFolders )
        {
            String mainClassName = className.split( "\\$" )[0];
            File classFile = new File( sourceFolder, mainClassName.replace( '.', File.separatorChar ) + ".java" );
            if ( classFile.isFile() )
            {
                try
                {
                    JavaSource source = parse( new FileReader( classFile ), classFile.toURI().toURL() ).getSource();
                    result = source.getClassByName( className );
                    break;
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
        boolean result = super.containsClassReference( className );
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
