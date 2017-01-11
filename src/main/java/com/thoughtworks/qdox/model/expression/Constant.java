package com.thoughtworks.qdox.model.expression;

import java.util.regex.Pattern;

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

public abstract class Constant
    implements AnnotationValue
{

    private final String image;

    private Constant( String image )
    {
        this.image = image;
    }

    public abstract Object getValue();

    public static Constant newBooleanLiteral( final String value )
    {
        return new Constant( value )
        {

            @Override
            public Object getValue()
            {
                return toBoolean( value );
            }
        };
    }

    public static Constant newCharacterLiteral( final String value )
    {
        return new Constant( value )
        {
            @Override
            public Object getValue()
            {
                return toChar( value );
            }
        };
    }

    public static Constant newFloatingPointLiteral( final String value )
    {
        return new Constant( value )
        {
            @Override
            public Object getValue()
            {
                return toFloatingPointLiteral( value );
            }
        };
    }

    public static Constant newIntegerLiteral( final String value )
    {
        return new Constant( value )
        {
            @Override
            public Object getValue()
            {
                return toIntegerLiteral( value );
            }
        };
    }

    public static Constant newStringLiteral( final String value )
    {
        return new Constant( value )
        {
            @Override
            public Object getValue()
            {
                return toString( value );
            }
        };
    }

    public String getImage()
    {
        return image;
    }

    @Override
    public String toString()
    {
        return image;
    }

    /** {@inheritDoc} */
    public Object accept( ExpressionVisitor visitor )
    {
        return visitor.visit( this );
    }

    /** {@inheritDoc} */
    public String getParameterValue()
    {
        return image;
    }

    protected static String convertString( String str )
    {
        StringBuilder buf = new StringBuilder();
        boolean escaped = false;
        int unicode = 0;
        int value = 0;
        int octal = 0;
        boolean consumed = false;

        for ( int i = 0; i < str.length(); ++i )
        {
            char ch = str.charAt( i );

            if ( octal > 0 )
            {
                if ( value >= '0' && value <= '7' )
                {
                    value = ( value << 3 ) | Character.digit( ch, 8 );
                    --octal;
                    consumed = true;
                }
                else
                {
                    octal = 0;
                }

                if ( octal == 0 )
                {
                    buf.append( (char) value );
                    value = 0;
                }
            }

            if ( !consumed )
            {
                if ( unicode > 0 )
                {
                    value = ( value << 4 ) | Character.digit( ch, 16 );

                    --unicode;

                    if ( unicode == 0 )
                    {
                        buf.append( (char) value );
                        value = 0;
                    }
                }
                else if ( escaped )
                {
                    if ( ch == 'u' || ch == 'U' )
                    {
                        unicode = 4;
                    }
                    else if ( ch >= '0' && ch <= '7' )
                    {
                        octal = ( ch > '3' ) ? 1 : 2;
                        value = Character.digit( ch, 8 );
                    }
                    else
                    {
                        switch ( ch )
                        {
                            case 'b':
                                buf.append( '\b' );
                                break;

                            case 'f':
                                buf.append( '\f' );
                                break;

                            case 'n':
                                buf.append( '\n' );
                                break;

                            case 'r':
                                buf.append( '\r' );
                                break;

                            case 't':
                                buf.append( '\t' );
                                break;

                            case '\'':
                                buf.append( '\'' );
                                break;

                            case '\"':
                                buf.append( '\"' );
                                break;

                            case '\\':
                                buf.append( '\\' );
                                break;

                            default:
                                // yyerror( "Illegal escape character '" + ch + "'" );
                        }
                    }

                    escaped = false;
                }
                else if ( ch == '\\' )
                {
                    escaped = true;
                }
                else
                {
                    buf.append( ch );
                }
            }
        }

        return buf.toString();
    }

    protected static Boolean toBoolean( String value )
    {
        return Boolean.valueOf( value );
    }

    protected static Number toIntegerLiteral( String value )
    {
        String literal = value.replaceAll( "_", "" );

        Number result;
        int radix = 10; // default
        int offset = 0;

        if ( Pattern.compile( "^0[xX]" ).matcher( literal ).find() )
        {
            radix = 16;
            offset = 2;
        }
        else if ( Pattern.compile( "^0[bB]" ).matcher( literal ).find() )
        {
            radix = 2;
            offset = 2;
        }
        else if ( Pattern.compile( "^0[0-7]" ).matcher( literal ).find() )
        {
            radix = 8;
            offset = 1;
        }
        if ( Pattern.compile( "[lL]$" ).matcher( literal ).find() )
        {
            result = Long.valueOf( literal.substring( offset, literal.length() - 1 ), radix );
        }
        else
        {
            result = Integer.valueOf( literal.substring( offset ), radix );
        }

        return result;
    }

    protected static Number toFloatingPointLiteral( String value )
    {
        String literal = value.replaceAll( "_", "" );

        Number result;

        if ( Pattern.compile( "[dD]$" ).matcher( literal ).find() )
        {
            result = Double.valueOf( literal.substring( 0, literal.length() - 1 ) );
        }
        else if ( Pattern.compile( "[fF]$" ).matcher( literal ).find() )
        {
            result = Float.valueOf( literal.substring( 0, literal.length() - 1 ) );
        }
        else
        {
            result = Float.valueOf( literal );
        }

        return result;
    }

    /**
     * Convert a character literal into a character.
     * 
     * @param value the single quoted value
     * @return the transformed char
     * @throws IllegalArgumentException if value is not a character literal
     */
    protected static Character toChar( String value ) throws IllegalArgumentException
    {
        if ( !value.startsWith( "'" ) && !value.endsWith( "'" ) )
        {
            throw new IllegalArgumentException( "Character must be single quoted." );
        }

        String literal = convertString( value.substring( 1, value.length() - 1 ) );

        if ( literal.length() != 1 )
        {
            throw new IllegalArgumentException( "Only one character allowed in character constants." );
        }

        return Character.valueOf( literal.charAt( 0 ) );
    }

    /**
     * Convert a string literal into a string.
     * 
     * @param value the double quoted value
     * @return the transformed String
     * @throws IllegalArgumentException if value is not a String literal
     */
    protected static String toString( String value ) throws IllegalArgumentException
    {
        if ( value.length() < 2 && !value.startsWith( "\"" ) && !value.endsWith( "\"" ) )
        {
            throw new IllegalArgumentException( "String must be double quoted." );
        }

        return convertString( value.substring( 1, value.length() - 1 ) );
    }
}
