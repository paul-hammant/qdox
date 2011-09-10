package com.thoughtworks.qdox.directorywalker;

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
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * A directory scanner, which can scan files based on optional filters.
 */
public class DirectoryScanner
{

    private File file;

    private Collection<Filter> filters = new HashSet<Filter>();

    /**
     * 
     * @param file the directory (or file) to scan
     */
    public DirectoryScanner( File file )
    {
        this.file = file;
    }

    /**
     * Add a filter to this scanner.
     * 
     * @param filter the filter
     */
    public void addFilter( Filter filter )
    {
        this.filters.add( filter );
    }

    /**
     * 
     * @return a list of files matching the filters, never <code>null</code>
     */
    public List<File> scan()
    {
        final List<File> result = new LinkedList<File>();
        walk( new FileVisitor()
        {
            public void visitFile( File file )
            {
                result.add( file );
            }
        }, this.file );
        return result;
    }

    /**
     * Scans the directory. Every file not filtered out by a filter fill be passed to the {@code fileVisitor}
     * 
     * @param fileVisitor handler for matching files.
     */
    public void scan( FileVisitor fileVisitor )
    {
        walk( fileVisitor, this.file );
    }

    private void walk( FileVisitor visitor, File current )
    {
        if ( current.isDirectory() )
        {
            File[] currentFiles = current.listFiles();
            for ( int i = 0; i < currentFiles.length; i++ )
            {
                walk( visitor, currentFiles[i] );
            }
        }
        else
        {
            for ( Filter filter : this.filters )
            {
                if ( !filter.filter( current ) )
                {
                    return;
                }
            }
            visitor.visitFile( current );
        }
    }
}
