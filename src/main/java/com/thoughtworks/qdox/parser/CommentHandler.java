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
 * JavaDoc is not part of the Java Language Specification, it should be treated as a special type of comment. This means
 * it can appear almost everywhere, although there are only a few places where JavaDoc has effect. When the parser has
 * finished a comment, it will trigger the commentHandler by calling the onComment-method.
 * 
 * @author Robert Scholte
 * @since 2.0
 */
public interface CommentHandler
{
    /**
     * Called if the parser hits a comment
     * 
     * @param comment the comment
     * @param line the line number
     * @param column the column number
     */
    void onComment( String comment, int line, int column );
}
