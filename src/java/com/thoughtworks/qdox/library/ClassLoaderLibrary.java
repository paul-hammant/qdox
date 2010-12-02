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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.List;

import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.ModelBuilder;
import com.thoughtworks.qdox.parser.impl.BinaryClassParser;

/**
 * <strong>Important!! Be sure to add a classloader with the bootstrap classes.</strong>
 * <p>
 * Normally you can generate your classLibrary like this:<br/>
 * <code>
 *  ClassLibrary classLibrary = new ClassLibrary();
 *  classLibrary.addDefaultLoader();
 * </code>
 * </p>
 * <p>
 * If you want full control over the classLoaders you might want to create your library like:<br/>
 * <code>
 * ClassLibrary classLibrary = new ClassLibrary( ClassLoader.getSystemClassLoader() )
 * </code>
 * </p>
 * 
 * @author <a href="mailto:joew@thoughtworks.com">Joe Walnes</a>
 * @author Aslak Helles&oslash;y
 * @author Robert Scholte
 * @since 2.0
 */
public class ClassLoaderLibrary
    extends AbstractClassLibrary
{
    private transient List<ClassLoader> classLoaders = new LinkedList<ClassLoader>();

    private boolean defaultClassLoadersAdded = false;

    public ClassLoaderLibrary( AbstractClassLibrary parent )
    {
        super( parent );
    }

    public ClassLoaderLibrary( AbstractClassLibrary parent, ClassLoader classLoader )
    {
        super( parent );
        this.classLoaders.add( classLoader );
    }

    public void addClassLoader( ClassLoader classLoader )
    {
        classLoaders.add( classLoader );
    }

    public void addDefaultLoader()
    {
        if ( !defaultClassLoadersAdded )
        {
            classLoaders.add( getClass().getClassLoader() );
            classLoaders.add( Thread.currentThread().getContextClassLoader() );
        }
        defaultClassLoadersAdded = true;
    }

    @Override
    protected JavaClass resolveJavaClass( String name )
    {
        JavaClass result = null;
        for (Iterator<ClassLoader> iter = classLoaders.iterator(); iter.hasNext(); )
        {
            ClassLoader classLoader = (ClassLoader) iter.next();
            try
            {
                Class<?> clazz = classLoader.loadClass( name );
                ModelBuilder builder = getModelBuilder();
                BinaryClassParser parser = new BinaryClassParser( clazz, builder );
                if ( parser.parse() )
                {
                    //this works, classloaders parse the FQN (including nested classes) directly
                    result = builder.getSource().getClasses().get( 0 );
                }
                break;
            }
            catch ( ClassNotFoundException e )
            {
            }
        }
        return result;
    }

    private void readObject( ObjectInputStream in )
        throws IOException, ClassNotFoundException
    {
        in.defaultReadObject();
        classLoaders = new LinkedList<ClassLoader>();
        if ( defaultClassLoadersAdded )
        {
            defaultClassLoadersAdded = false;
            addDefaultLoader();
        }
    }
    
    @Override
    protected boolean containsClassReference( String name )
    {
        boolean result = false;
        for(Iterator<ClassLoader> iter = classLoaders.iterator();!result && iter.hasNext(); )
        {
            ClassLoader classLoader = (ClassLoader) iter.next();
            try
            {
                Class<?> clazz = classLoader.loadClass( name );
                result = ( clazz != null );
            }
            catch ( ClassNotFoundException e )
            {
            }
        }
        return result;
    }
}
