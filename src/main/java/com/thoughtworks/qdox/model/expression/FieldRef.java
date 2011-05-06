package com.thoughtworks.qdox.model.expression;

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

import java.util.StringTokenizer;

import com.thoughtworks.qdox.model.AbstractBaseJavaEntity;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaClassParent;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.Type;

public class FieldRef implements AnnotationValue {

    private final int[] parts;

    private final String name;

    private AbstractBaseJavaEntity context;

    private JavaField field;

    private int fieldIndex = -1;

    public FieldRef( String name ) {
        this.name = name;

        int length = new StringTokenizer( name, "." ).countTokens();
        this.parts = new int[length + 1];
        this.parts[0] = -1;

        for( int i = 1; i < length; ++i ) {
            this.parts[i] = name.indexOf( '.', this.parts[i - 1] + 1 );
        }

        this.parts[length] = name.length();
    }

    public String getName() {
        return name;
    }

    public String getNamePrefix( int end ) {
        return name.substring( 0, parts[end + 1] );
    }

    public String getNamePart( int index ) {
        return name.substring( parts[index] + 1, parts[index + 1] );
    }

    public int getPartCount() {
        return parts.length - 1;
    }

    public Object accept( AnnotationVisitor visitor ) {
        return visitor.visitAnnotationFieldRef( this );
    }

    public Object getParameterValue() {
        return getName();
    }

    public String toString() {
        return getName();
    }

    public void setContext( AbstractBaseJavaEntity context ) {
        this.context = context;
    }

    public String getClassPart() {
        String result = null;

        if( getField() != null ) {
            result = name.substring( 0, parts[fieldIndex] );
        }

        return result;
    }

    public String getFieldPart() {
        String result = null;

        if( getField() != null ) {
            result = name.substring( parts[fieldIndex] + 1 );
        }

        return result;
    }

    protected JavaField resolveField( JavaClass javaClass, int start, int end ) {
        JavaField field = null;

        for( int i = start; i < end; ++i ) {
            field = javaClass.getFieldByName( getNamePart( i ) );

            if( field == null ) {
                break;
            }

            javaClass = field.getType().getJavaClass();
        }

        return field;
    }

    public JavaField getField() {
        if( fieldIndex < 0 ) {
            if( context.getParentClass() != null ) {
                JavaClass javaClass = context.getParentClass();
                field = resolveField( javaClass, 0, parts.length -1 );
                fieldIndex = 0;
            }

            if( field == null ) {
                JavaClassParent classParent = context.getParentClass();

                //assume context is a JavaClass itself
                if(classParent == null) {
                    classParent = (JavaClass) context;
                }
                
                for( int i = 0; i < parts.length - 1; ++i ) {
                    String className = getNamePrefix( i );
                    String typeName = classParent.resolveType( className );

                    if( typeName != null ) {
                        Type type = Type.createUnresolved( typeName, 0, classParent );
                        JavaClass javaClass = type.getJavaClass();

                        if( javaClass != null ) {
                            fieldIndex = i + 1;
                            field = resolveField( javaClass, i + 1, parts.length - 1 );
                            break;
                        }
                    }
                }
            }
        }

        return field;
    }
}
