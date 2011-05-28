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

import java.io.Serializable;


public class Constant implements AnnotationValue, Serializable {

    private final Object value;
    private final String image;

    private Constant( Object value, String image ) {
        this.value = value;
        this.image = image;
    }

    public static Constant newBooleanLiteral( String value ) 
    {
        return new Constant( toBoolean( value ), value );
    }

    public static Constant newCharacterLiteral( String value ) 
    {
        return new Constant( toChar( value ), value );
    }

    public static Constant newFloatingPointLiteral( String value ) 
    {
        return new Constant( toFloatingPointLiteral( value ), value );
    }

    public static Constant newIntegerLiteral( String value ) 
    {
        return new Constant( toIntegerLiteral( value ), value );
    }
    
    public static Constant newStringLiteral( String value ) {
        return new Constant( toString( value ), value );
    }
    
    public Object getValue() {
        return value;
    }

    public String getImage() {
        return image;
    }

    public String toString() {
        return image;
    }

    public Object accept( AnnotationVisitor visitor ) {
        return visitor.visit( this );
    }

    public String getParameterValue() {
        return image;
    }
 
    protected static String convertString(String str) {
        StringBuffer buf = new StringBuffer();
        boolean escaped = false;
        int unicode = 0;
        int value = 0;
        int octal = 0;
        boolean consumed = false;
        
        for(int i = 0; i < str.length(); ++ i) {
            char ch = str.charAt( i );
            
            if(octal > 0) {
                if( value >= '0' && value <= '7' ) {
                    value = ( value << 3 ) | Character.digit( ch, 8 );
                    -- octal;
                    consumed = true;
                }
                else {
                    octal = 0;
                }
                
                if( octal == 0 ) {
                    buf.append( (char) value );     
                    value = 0;
                }
            }
            
            if(!consumed) {
                if(unicode > 0) {
                    value = ( value << 4 ) | Character.digit( ch, 16 );
                    
                    -- unicode;
            
                    if(unicode == 0) {
                        buf.append( (char)value );
                        value = 0;
                    }
                }
                else if(escaped) {
                    if(ch == 'u' || ch == 'U') {
                        unicode = 4;
                    }
                    else if(ch >= '0' && ch <= '7') {
                        octal = (ch > '3') ? 1 : 2;
                        value = Character.digit( ch, 8 );
                    }
                    else {
                        switch( ch ) {
                            case 'b':
                                buf.append('\b');
                                break;
                                
                            case 'f':
                                buf.append('\f');
                                break;
                                
                            case 'n':
                                buf.append('\n');
                                break;
                                
                            case 'r':
                                buf.append('\r');
                                break;
                                
                            case 't':
                                buf.append('\t');
                                break;
                                
                            case '\'':
                                buf.append('\'');
                                break;
        
                            case '\"':
                                buf.append('\"');
                                break;
        
                            case '\\':
                                buf.append('\\');
                                break;
                                
                            default:
                                //yyerror( "Illegal escape character '" + ch + "'" );
                        }
                    }
                    
                    escaped = false;
                }
                else if(ch == '\\') {
                    escaped = true;
                }
                else {
                    buf.append( ch );
                }
            }
        }

        return buf.toString();
    }

    protected static  Boolean toBoolean(String str) {
        str = str.trim();

        return Boolean.valueOf( str );
    }

    protected static Byte toByte(String str) {
       str = str.trim().replaceAll("_", "");
       
       return Byte.valueOf(str.substring(2), 2);
    }

    protected static Number toIntegerLiteral(String str) {
        str = str.trim().replaceAll("_", "");
        
        Number result;
        int radix = 10; //default
        int offset = 0;
        
        if(str.startsWith("0x") || str.startsWith( "0X" ) ) {
            radix = 16;
            offset = 2;
            //result = Integer.valueOf( str.substring( 2 ), 16 );
        }
        else if(str.startsWith("0b") || str.startsWith( "0B" ) ) {
            radix = 2;
            offset = 2;
            //result = Integer.valueOf( str.substring( 2 ), 2 );
        }
        else if(str.length() > 1 && str.startsWith("0") ) {
            radix = 8;
            offset = 1;
            //result = Integer.valueOf( str.substring( 1 ), 8 );
        }
        else {
            //result = Integer.valueOf( str );
        }
        
        if( str.matches( ".+[lL]" )) {
            result = Long.valueOf( str.substring( offset, str.length() - 1 ), radix );
        }
        else {
            result = Integer.valueOf( str.substring( offset ), radix );
        }
        
        return result;
    }

    protected static Number toFloatingPointLiteral(String str) {
        str = str.trim().replaceAll("_", "");
        
        Number result;
         
        if( str.matches( ".+[dD]" ) ) 
        {
            result = Double.valueOf( str.substring( 0, str.length() - 1 ) );
        }
        else if ( str.matches( ".+[fF]" ) ) 
        {
            result = Float.valueOf( str.substring( 0, str.length() - 1 ) );
        } 
        else 
        {
            result = Float.valueOf( str );
        }
        
        return result;
    }

    protected static Double toDouble(String str) {
        str = str.trim().replaceAll("_", "");

        if( !str.endsWith("d") && !str.endsWith("D") ) {
//            yyerror( "Double literal must end with 'd' or 'D'." );
        }
        
        return Double.valueOf( str.substring( 0, str.length() - 1 ) );
    }

    /**
     * Convert a character literal into a character.
     */
    protected static Character toChar(String str) {
        str = str.trim();

        if( !str.startsWith("'") && !str.endsWith("'") ) {
//            yyerror("Character must be single quoted.");
        }

        String str2 = convertString( str.substring( 1, str.length() - 1 ) );
        
        if( str2.length() != 1) {
//            yyerror("Only one character allowed in character constants.");
        }
        
        return Character.valueOf( str2.charAt( 0 ) );
    }

    /**
     * Convert a string literal into a string.
     */
    protected static String toString(String str) {
        str = str.trim();

        if( str.length() < 2 && !str.startsWith("\"") && !str.endsWith("\"") ) {
//            yyerror("String must be double quoted.");
        }

        String str2 = convertString( str.substring( 1, str.length() - 1 ) );
        return str2;
    }    
}
