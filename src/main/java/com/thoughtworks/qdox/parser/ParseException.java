package com.thoughtworks.qdox.parser;

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

/**
 * Thrown to indicate an error during parsing
 */
public class ParseException
    extends RuntimeException
{

    /**
     * The line where the ParserException occurred
     */
    private int line = -1;

    /**
     * The column where the ParserException occurred
     */
    private int column = -1;

    /**
     * The sourceInfo of where the exception occurred
     */
    private String sourceInfo;

    /**
     * Default constructor for the ParseException
     * 
     * @param message the message
     * @param line the line number
     * @param column the column number
     */
    public ParseException( String message, int line, int column )
    {
        super( message );
        this.line = line;
        this.column = column;
    }

    public int getLine()
    {
        return line;
    }

    public int getColumn()
    {
        return column;
    }

    public void setSourceInfo( String sourceInfo )
    {
        this.sourceInfo = sourceInfo;
    }

    @Override
    public String getMessage()
    {
        StringBuilder buffer = new StringBuilder( super.getMessage() );
        if ( line >= 0 )
        {
            buffer.append( " @[" ).append( line );

            if ( column >= 0 )
            {
                buffer.append( ',' ).append( column );
            }
            buffer.append( ']' );
        }
        if ( sourceInfo != null )
        {
            buffer.append( " in " ).append( sourceInfo );
        }
        return buffer.toString();
    }
}
