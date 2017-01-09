package com.thoughtworks.qdox.builder.impl;

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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.thoughtworks.qdox.builder.TypeAssembler;
import com.thoughtworks.qdox.library.ClassLibrary;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaType;
import com.thoughtworks.qdox.model.expression.Add;
import com.thoughtworks.qdox.model.expression.And;
import com.thoughtworks.qdox.model.expression.AnnotationValue;
import com.thoughtworks.qdox.model.expression.AnnotationValueList;
import com.thoughtworks.qdox.model.expression.Assignment;
import com.thoughtworks.qdox.model.expression.Cast;
import com.thoughtworks.qdox.model.expression.Constant;
import com.thoughtworks.qdox.model.expression.Divide;
import com.thoughtworks.qdox.model.expression.Equals;
import com.thoughtworks.qdox.model.expression.ExclusiveOr;
import com.thoughtworks.qdox.model.expression.Expression;
import com.thoughtworks.qdox.model.expression.FieldRef;
import com.thoughtworks.qdox.model.expression.GreaterEquals;
import com.thoughtworks.qdox.model.expression.GreaterThan;
import com.thoughtworks.qdox.model.expression.LessEquals;
import com.thoughtworks.qdox.model.expression.LessThan;
import com.thoughtworks.qdox.model.expression.LogicalAnd;
import com.thoughtworks.qdox.model.expression.LogicalNot;
import com.thoughtworks.qdox.model.expression.LogicalOr;
import com.thoughtworks.qdox.model.expression.MinusSign;
import com.thoughtworks.qdox.model.expression.Multiply;
import com.thoughtworks.qdox.model.expression.Not;
import com.thoughtworks.qdox.model.expression.NotEquals;
import com.thoughtworks.qdox.model.expression.Or;
import com.thoughtworks.qdox.model.expression.ParenExpression;
import com.thoughtworks.qdox.model.expression.PlusSign;
import com.thoughtworks.qdox.model.expression.PostDecrement;
import com.thoughtworks.qdox.model.expression.PostIncrement;
import com.thoughtworks.qdox.model.expression.PreDecrement;
import com.thoughtworks.qdox.model.expression.PreIncrement;
import com.thoughtworks.qdox.model.expression.Query;
import com.thoughtworks.qdox.model.expression.Remainder;
import com.thoughtworks.qdox.model.expression.ShiftLeft;
import com.thoughtworks.qdox.model.expression.ShiftRight;
import com.thoughtworks.qdox.model.expression.Subtract;
import com.thoughtworks.qdox.model.expression.TypeRef;
import com.thoughtworks.qdox.model.expression.UnsignedShiftRight;
import com.thoughtworks.qdox.model.impl.DefaultJavaAnnotation;
import com.thoughtworks.qdox.parser.expression.AddDef;
import com.thoughtworks.qdox.parser.expression.AndDef;
import com.thoughtworks.qdox.parser.expression.AssignmentDef;
import com.thoughtworks.qdox.parser.expression.CastDef;
import com.thoughtworks.qdox.parser.expression.ConstantDef;
import com.thoughtworks.qdox.parser.expression.CreatorDef;
import com.thoughtworks.qdox.parser.expression.DivideDef;
import com.thoughtworks.qdox.parser.expression.ElemValueDef;
import com.thoughtworks.qdox.parser.expression.ElemValueListDef;
import com.thoughtworks.qdox.parser.expression.ElemValueTransformer;
import com.thoughtworks.qdox.parser.expression.EqualsDef;
import com.thoughtworks.qdox.parser.expression.ExclusiveOrDef;
import com.thoughtworks.qdox.parser.expression.FieldRefDef;
import com.thoughtworks.qdox.parser.expression.GreaterEqualsDef;
import com.thoughtworks.qdox.parser.expression.GreaterThanDef;
import com.thoughtworks.qdox.parser.expression.LessEqualsDef;
import com.thoughtworks.qdox.parser.expression.LessThanDef;
import com.thoughtworks.qdox.parser.expression.LogicalAndDef;
import com.thoughtworks.qdox.parser.expression.LogicalNotDef;
import com.thoughtworks.qdox.parser.expression.LogicalOrDef;
import com.thoughtworks.qdox.parser.expression.MethodInvocationDef;
import com.thoughtworks.qdox.parser.expression.MethodReferenceDef;
import com.thoughtworks.qdox.parser.expression.MinusSignDef;
import com.thoughtworks.qdox.parser.expression.MultiplyDef;
import com.thoughtworks.qdox.parser.expression.NotDef;
import com.thoughtworks.qdox.parser.expression.NotEqualsDef;
import com.thoughtworks.qdox.parser.expression.OrDef;
import com.thoughtworks.qdox.parser.expression.ParenExpressionDef;
import com.thoughtworks.qdox.parser.expression.PlusSignDef;
import com.thoughtworks.qdox.parser.expression.PostDecrementDef;
import com.thoughtworks.qdox.parser.expression.PostIncrementDef;
import com.thoughtworks.qdox.parser.expression.PreDecrementDef;
import com.thoughtworks.qdox.parser.expression.PreIncrementDef;
import com.thoughtworks.qdox.parser.expression.QueryDef;
import com.thoughtworks.qdox.parser.expression.RemainderDef;
import com.thoughtworks.qdox.parser.expression.ShiftLeftDef;
import com.thoughtworks.qdox.parser.expression.ShiftRightDef;
import com.thoughtworks.qdox.parser.expression.SubtractDef;
import com.thoughtworks.qdox.parser.expression.TypeRefDef;
import com.thoughtworks.qdox.parser.expression.UnsignedShiftRightDef;
import com.thoughtworks.qdox.parser.structs.AnnoDef;
import com.thoughtworks.qdox.parser.structs.TypeDef;
import com.thoughtworks.qdox.type.TypeResolver;

public class DefaultJavaAnnotationAssembler
    implements ElemValueTransformer<AnnotationValue>
{
    private JavaClass declaringClass;
    
    private ClassLibrary classLibrary;
    
    private TypeResolver typeResolver;

    public DefaultJavaAnnotationAssembler( JavaClass declaringClass, ClassLibrary classLibrary, TypeResolver typeResolver )
    {
        this.declaringClass = declaringClass;
        this.classLibrary = classLibrary;
        this.typeResolver = typeResolver;
    }

    public DefaultJavaAnnotation assemble( AnnoDef annoDef ) {
        DefaultJavaAnnotation annotation = new DefaultJavaAnnotation( createType( annoDef.getTypeDef(), 0 ) );
        annotation.setLineNumber( annoDef.getLineNumber() );
        
        for ( Map.Entry<String, ElemValueDef> annoVal : annoDef.getArgs().entrySet() )
        {
            annotation.setProperty( annoVal.getKey(), annoVal.getValue().transform( this ) );
        }
        return annotation;
    }
    
    public Expression assemble( ElemValueDef annoDef )
    {
        return annoDef.transform( this );
    }
    

    private JavaClass createType( TypeDef typeDef, int dimensions )
    {
        if ( typeDef == null )
        {
            return null;
        }
        return TypeAssembler.createUnresolved( typeDef,
                                               dimensions,
                                               typeResolver  );
    }
    
    /** {@inheritDoc} */
    public AnnotationValue transform( AnnoDef annoDef )
    {
        return assemble( annoDef);
    }
    
    /** {@inheritDoc} */
    public AnnotationValue transform( ElemValueListDef elemValueListDef )
    {
        List<AnnotationValue> parsedList = new LinkedList<AnnotationValue>();
        for ( ElemValueDef val : elemValueListDef.getValueList() )
        {
            parsedList.add( val.transform( this ) );
        }
        return new AnnotationValueList( parsedList );
    }

    /** {@inheritDoc} */
    public AnnotationValue transform( AddDef annotationAdd )
    {
        AnnotationValue left = annotationAdd.getLeft().transform( this );
        AnnotationValue right = annotationAdd.getRight().transform( this );
        return new Add( left, right );
    }

    /** {@inheritDoc} */
    public AnnotationValue transform( AndDef annotationAnd )
    {
        AnnotationValue left = annotationAnd.getLeft().transform( this );
        AnnotationValue right = annotationAnd.getRight().transform( this );
        return new And( left, right );
    }

    /** {@inheritDoc} */
    public AnnotationValue transform( DivideDef annotationDivide )
    {
        AnnotationValue left = annotationDivide.getLeft().transform( this );
        AnnotationValue right = annotationDivide.getRight().transform( this );
        return new Divide( left, right );
    }

    /** {@inheritDoc} */
    public AnnotationValue transform( EqualsDef annotationEquals )
    {
        AnnotationValue left = annotationEquals.getLeft().transform( this );
        AnnotationValue right = annotationEquals.getRight().transform( this );
        return new Equals( left, right );
    }

    /** {@inheritDoc} */
    public AnnotationValue transform( ExclusiveOrDef annotationExclusiveOr )
    {
        AnnotationValue left = annotationExclusiveOr.getLeft().transform( this );
        AnnotationValue right = annotationExclusiveOr.getRight().transform( this );
        return new ExclusiveOr( left, right );
    }

    /** {@inheritDoc} */
    public AnnotationValue transform( GreaterEqualsDef annotationGreaterEquals )
    {
        AnnotationValue left = annotationGreaterEquals.getLeft().transform( this );
        AnnotationValue right = annotationGreaterEquals.getRight().transform( this );
        return new GreaterEquals( left, right );
    }

    /** {@inheritDoc} */
    public AnnotationValue transform( GreaterThanDef annotationGreaterThan )
    {
        AnnotationValue left = annotationGreaterThan.getLeft().transform( this );
        AnnotationValue right = annotationGreaterThan.getRight().transform( this );
        return new GreaterThan( left, right );
    }

    /** {@inheritDoc} */
    public AnnotationValue transform( LessEqualsDef annotationLessEquals )
    {
        AnnotationValue left = annotationLessEquals.getLeft().transform( this );
        AnnotationValue right = annotationLessEquals.getRight().transform( this );
        return new LessEquals( left, right );
    }

    /** {@inheritDoc} */
    public AnnotationValue transform( LessThanDef annotationLessThan )
    {
        AnnotationValue left = annotationLessThan.getLeft().transform( this );
        AnnotationValue right = annotationLessThan.getRight().transform( this );
        return new LessThan( left, right );
    }

    /** {@inheritDoc} */
    public AnnotationValue transform( LogicalAndDef annotationLogicalAnd )
    {
        AnnotationValue left = annotationLogicalAnd.getLeft().transform( this );
        AnnotationValue right = annotationLogicalAnd.getRight().transform( this );
        return new LogicalAnd( left, right );
    }

    /** {@inheritDoc} */
    public AnnotationValue transform( LogicalOrDef annotationLogicalOr )
    {
        AnnotationValue left = annotationLogicalOr.getLeft().transform( this );
        AnnotationValue right = annotationLogicalOr.getRight().transform( this );
        return new LogicalOr( left, right );
    }

    /** {@inheritDoc} */
    public AnnotationValue transform( MultiplyDef annotationMultiply )
    {
        AnnotationValue left = annotationMultiply.getLeft().transform( this );
        AnnotationValue right = annotationMultiply.getRight().transform( this );
        return new Multiply( left, right );
    }

    /** {@inheritDoc} */
    public AnnotationValue transform( NotEqualsDef annotationNotEquals )
    {
        AnnotationValue left = annotationNotEquals.getLeft().transform( this );
        AnnotationValue right = annotationNotEquals.getRight().transform( this );
        return new NotEquals( left, right );
    }

    /** {@inheritDoc} */
    public AnnotationValue transform( OrDef annotationOr )
    {
        AnnotationValue left = annotationOr.getLeft().transform( this );
        AnnotationValue right = annotationOr.getRight().transform( this );
        return new Or( left, right );
    }

    /** {@inheritDoc} */
    public AnnotationValue transform( RemainderDef annotationRemainder )
    {
        AnnotationValue left = annotationRemainder.getLeft().transform( this );
        AnnotationValue right = annotationRemainder.getRight().transform( this );
        return new Remainder( left, right );
    }

    /** {@inheritDoc} */
    public AnnotationValue transform( ShiftLeftDef annotationShiftLeft )
    {
        AnnotationValue left = annotationShiftLeft.getLeft().transform( this );
        AnnotationValue right = annotationShiftLeft.getRight().transform( this );
        return new ShiftLeft( left, right );
    }

    /** {@inheritDoc} */
    public AnnotationValue transform( ShiftRightDef annotationShiftRight )
    {
        AnnotationValue left = annotationShiftRight.getLeft().transform( this );
        AnnotationValue right = annotationShiftRight.getRight().transform( this );
        return new ShiftRight( left, right );
    }

    /** {@inheritDoc} */
    public AnnotationValue transform( SubtractDef annotationSubtract )
    {
        AnnotationValue left = annotationSubtract.getLeft().transform( this );
        AnnotationValue right = annotationSubtract.getRight().transform( this );
        return new Subtract( left, right );
    }

    /** {@inheritDoc} */
    public AnnotationValue transform( UnsignedShiftRightDef annotationUnsignedShiftRight )
    {
        AnnotationValue left = annotationUnsignedShiftRight.getLeft().transform( this );
        AnnotationValue right = annotationUnsignedShiftRight.getRight().transform( this );
        return new UnsignedShiftRight( left, right );
    }

    /** {@inheritDoc} */
    public AnnotationValue transform( CastDef annotationCast )
    {
        JavaType type = createType( annotationCast.getTypeDef(), annotationCast.getTypeDef().getDimensions() );
        AnnotationValue value = annotationCast.getElemDef().transform( this );
        return new Cast( type, value );
    }

    /** {@inheritDoc} */
    public AnnotationValue transform( ConstantDef annotationConstant )
    {
        AnnotationValue result;
        String image = annotationConstant.getValue();
        Class<?> type = annotationConstant.getType();
        if ( type == Integer.class )
        {
            result = Constant.newIntegerLiteral( image );
        }
        else if ( type == String.class )
        {
            result = Constant.newStringLiteral( image );
        }
        else if ( type == Boolean.class )
        {
            result = Constant.newBooleanLiteral( image );
        }
        else if ( type == Character.class )
        {
            result = Constant.newCharacterLiteral( image );
        }
        else if ( type == Float.class )
        {
            result = Constant.newFloatingPointLiteral( image );
        }
        else
        {
            result = null; // unknown??
        }
        return result;
    }

    /** {@inheritDoc} */
    public AnnotationValue transform( FieldRefDef annotationFieldRef )
    {
        FieldRef result;
        String name = annotationFieldRef.getName();
        result = new FieldRef( name );
        result.setDeclaringClass( declaringClass );
        result.setClassLibrary( classLibrary );
        return result;
    }

    /** {@inheritDoc} */
    public AnnotationValue transform( LogicalNotDef annotationLogicalNot )
    {
        AnnotationValue value = annotationLogicalNot.getElemValueDef().transform( this );
        return new LogicalNot( value );
    }

    /** {@inheritDoc} */
    public AnnotationValue transform( MinusSignDef annotationMinusSign )
    {
        AnnotationValue value = annotationMinusSign.getElemValueDef().transform( this );
        return new MinusSign( value );
    }

    /** {@inheritDoc} */
    public AnnotationValue transform( NotDef annotationNot )
    {
        AnnotationValue value = annotationNot.getElemValueDef().transform( this );
        return new Not( value );
    }

    /** {@inheritDoc} */
    public AnnotationValue transform( ParenExpressionDef annotationParenExpression )
    {
        AnnotationValue value = annotationParenExpression.getElemValueDef().transform( this );
        return new ParenExpression( value );
    }

    /** {@inheritDoc} */
    public AnnotationValue transform( PlusSignDef annotationPlusSign )
    {
        AnnotationValue value = annotationPlusSign.getElemValueDef().transform( this );
        return new PlusSign( value );
    }

    /** {@inheritDoc} */
    public AnnotationValue transform( QueryDef annotationQuery )
    {
        AnnotationValue condition = annotationQuery.getCondition().transform( this );
        AnnotationValue trueExpression = annotationQuery.getTrueExpression().transform( this );
        AnnotationValue falseExpression = annotationQuery.getFalseExpression().transform( this );
        return new Query( condition, trueExpression, falseExpression );
    }

    /** {@inheritDoc} */
    public AnnotationValue transform( TypeRefDef annotationTypeRef )
    {
        JavaType type = createType( annotationTypeRef.getTypeDef(), annotationTypeRef.getTypeDef().getDimensions() );
        return new TypeRef( type );
    }

    public AnnotationValue transform( AssignmentDef assignmentDef )
    {
        Expression leftHandSide = assignmentDef.getLetfHandSide().transform( this );
        String operator = assignmentDef.getOperator();
        Expression assignmentExpression = assignmentDef.getAssignmentExpression().transform( this );
        return new Assignment( leftHandSide, operator, assignmentExpression );
    }

    public AnnotationValue transform( PreIncrementDef preIncrementDef )
    {
        return new PreIncrement( preIncrementDef.getElemValueDef().transform( this )  );
    }
    
    public AnnotationValue transform( PreDecrementDef preDecrementDef )
    {
        return new PreDecrement( preDecrementDef.getElemValueDef().transform( this )  );
    }
    
    public AnnotationValue transform( PostIncrementDef postIncrement )
    {
        return new PostIncrement( postIncrement.getElemValueDef().transform( this )  );
    }
    
    public AnnotationValue transform( PostDecrementDef postDecrementDef )
    {
        return new PostDecrement( postDecrementDef.getElemValueDef().transform( this )  );
    }
    
    public AnnotationValue transform( MethodInvocationDef methodInvocationDef )
    {
        return null;
    }

    public AnnotationValue transform( MethodReferenceDef methodReferenceDef )
    {
        return null;
    }
    public AnnotationValue transform( CreatorDef newCreator )
    {
        return null;
    }
}