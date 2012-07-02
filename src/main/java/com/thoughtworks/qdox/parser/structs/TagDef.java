package com.thoughtworks.qdox.parser.structs;

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

public class TagDef
    extends LocatedDef
{

    private String name;
    private String text;

    public TagDef( String name, String text, int lineNumber )
    {
        this.name = name;
        this.text = text;
        this.setLineNumber( lineNumber );
    }

    public TagDef( String name, String text )
    {
        this( name, text, -1 );
    }

    public String getName()
    {
        return name;
    }

    public String getText()
    {
        return text;
    }

    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder();
        result.append( '@' );
        result.append( name );
        result.append( " => \"" );
        result.append( text );
        result.append( "\" @ line " );
        result.append( getLineNumber() );
        return result.toString();
    }
}
