package com.thoughtworks.qdox.builder;

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
import java.util.Set;
import java.util.Map.Entry;

import com.thoughtworks.qdox.io.ModelWriterFactory;
import com.thoughtworks.qdox.model.AbstractBaseJavaEntity;
import com.thoughtworks.qdox.model.Annotation;
import com.thoughtworks.qdox.model.DefaultJavaClass;
import com.thoughtworks.qdox.model.DefaultJavaField;
import com.thoughtworks.qdox.model.DefaultJavaMethod;
import com.thoughtworks.qdox.model.DefaultJavaPackage;
import com.thoughtworks.qdox.model.DefaultJavaParameter;
import com.thoughtworks.qdox.model.DefaultJavaSource;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.DocletTagFactory;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.model.Type;
import com.thoughtworks.qdox.model.TypeVariable;
import com.thoughtworks.qdox.parser.expression.AnnotationFieldRef;
import com.thoughtworks.qdox.parser.expression.AnnotationValue;
import com.thoughtworks.qdox.parser.expression.AnnotationValueList;
import com.thoughtworks.qdox.parser.expression.AnnotationVisitor;
import com.thoughtworks.qdox.parser.expression.RecursiveAnnotationVisitor;
import com.thoughtworks.qdox.parser.structs.AnnoDef;
import com.thoughtworks.qdox.parser.structs.ClassDef;
import com.thoughtworks.qdox.parser.structs.FieldDef;
import com.thoughtworks.qdox.parser.structs.MethodDef;
import com.thoughtworks.qdox.parser.structs.PackageDef;
import com.thoughtworks.qdox.parser.structs.TagDef;
import com.thoughtworks.qdox.parser.structs.TypeDef;
import com.thoughtworks.qdox.parser.structs.TypeVariableDef;

/**
 * @author <a href="mailto:joew@thoughtworks.com">Joe Walnes</a>
 * @author Robert Scholte
 */
public class ModelBuilder implements Builder {

    private final DefaultJavaSource source;
    private LinkedList<DefaultJavaClass> classStack = new LinkedList<DefaultJavaClass>();
    private DefaultJavaMethod currentMethod;
    private List<AnnoDef> currentAnnoDefs;
    private String lastComment;
    private List<TagDef> lastTagSet;
    private DocletTagFactory docletTagFactory;
    private ModelWriterFactory modelWriterFactory;

    public ModelBuilder(com.thoughtworks.qdox.library.ClassLibrary classLibrary, DocletTagFactory docletTagFactory) {
        this.docletTagFactory = docletTagFactory;
        this.source = new DefaultJavaSource(classLibrary);
        this.currentAnnoDefs = new LinkedList<AnnoDef>();
    }
    
    public void setModelWriterFactory( ModelWriterFactory modelWriterFactory )
    {
        this.modelWriterFactory = modelWriterFactory;
        source.setModelWriterFactory(modelWriterFactory);
    }

    public void addPackage(PackageDef packageDef) {
        DefaultJavaPackage jPackage = new DefaultJavaPackage(packageDef.name);
        jPackage.setClassLibrary( source.getJavaClassLibrary());
        jPackage.setLineNumber(packageDef.lineNumber);
        jPackage.setModelWriterFactory(modelWriterFactory);
        addJavaDoc(jPackage);
    	setAnnotations(jPackage);
        source.setPackage(jPackage);
    }

    public void addImport(String importName) {
        source.addImport(importName);
    }

    public void addJavaDoc(String text) {
        lastComment = text;
        lastTagSet = new LinkedList<TagDef>();
    }

    public void addJavaDocTag(TagDef tagDef) {
        lastTagSet.add(tagDef);
    }

    public void beginClass(ClassDef def) {
        DefaultJavaClass newClass = new DefaultJavaClass(source);
        newClass.setLineNumber(def.lineNumber);
        newClass.setModelWriterFactory(modelWriterFactory);

        // basic details
        newClass.setName(def.name);
        newClass.setInterface(ClassDef.INTERFACE.equals(def.type));
        newClass.setEnum(ClassDef.ENUM.equals(def.type));
        newClass.setAnnotation(ClassDef.ANNOTATION_TYPE.equals(def.type));

        // superclass
        if (newClass.isInterface()) {
            newClass.setSuperClass(null);
        } else if (!newClass.isEnum()) {
            newClass.setSuperClass(def.extendz.size() > 0 ? createType((TypeDef) def.extendz.toArray()[0], 0) : null);
        }

        // implements
        {
            Set<TypeDef> implementSet = newClass.isInterface() ? def.extendz : def.implementz;
            List<Type> implementz = new LinkedList<Type>();
            for(TypeDef implementType:implementSet) {
                implementz.add(createType(implementType, 0));
            }
            newClass.setImplementz(implementz);
        }

        // modifiers
        {
            newClass.setModifiers(new LinkedList<String>(def.modifiers));
        }
        
        // typeParameters
        if (def.typeParams != null) {
            List<TypeVariable> typeParams = new LinkedList<TypeVariable>();
            for(TypeVariableDef typeVariableDef : def.typeParams) {
                typeParams.add(createTypeVariable(typeVariableDef));
            }
            newClass.setTypeParameters(typeParams);
        }

        // javadoc
        addJavaDoc(newClass);

//        // ignore annotation types (for now)
//        if (ClassDef.ANNOTATION_TYPE.equals(def.type)) {
//        	System.out.println( currentClass.getFullyQualifiedName() );
//            return;
//        }

        // annotations
        setAnnotations( newClass );
        
        if(!classStack.isEmpty()) {
            classStack.getFirst().addClass( newClass );
            newClass.setParentClass( classStack.getFirst() );
            
        }
        else {
            source.addClass( newClass );
        }
        classStack.addFirst( newClass );
    }

    public void endClass() {
        classStack.removeFirst();
    }

    public Type createType( String typeName, int dimensions ) {
        if( typeName == null || typeName.equals( "" ) )
            return null;
        return createType(new TypeDef(typeName), dimensions);
    }
    
    /**
     * this one is specific for those cases where dimensions can be part of both the type and identifier
     * i.e. private String[] matrix[]; //field
     * 		public abstract String[] getMatrix[](); //method  
     *      
     * @param typeDef
     * @param dimensions
     * @return the Type
     */
    public Type createType(TypeDef typeDef, int dimensions) {
    	if(typeDef == null) {
    		return null;
    	}
    	return Type.createUnresolved(typeDef, dimensions, classStack.isEmpty() ? source : classStack.getFirst());
    }

    private void addJavaDoc(AbstractBaseJavaEntity entity) {
        if (lastComment == null) {
            return;
        } 
        entity.setComment(lastComment);
        List<DocletTag> tagList = new LinkedList<DocletTag>();
        for (TagDef tagDef : lastTagSet) {
            tagList.add( 
                docletTagFactory.createDocletTag(
                    tagDef.name, tagDef.text, 
                    entity, tagDef.lineNumber
                )
            );
        }
        entity.setTags(tagList);
        
        lastComment = null;
    }

    public void addMethod(MethodDef def) {
    	beginMethod();
    	endMethod(def);
    }
    
    public void beginMethod() {
    	currentMethod = new DefaultJavaMethod();
        // javadoc
        addJavaDoc(currentMethod);

    	setAnnotations(currentMethod);
    }
    
    public void endMethod(MethodDef def) {
        currentMethod.setParentClass(classStack.getFirst());
        currentMethod.setLineNumber(def.lineNumber);
        currentMethod.setModelWriterFactory(modelWriterFactory);

        // basic details
        currentMethod.setName(def.name);
        currentMethod.setReturns(createType(def.returnType, def.dimensions));
        currentMethod.setConstructor(def.constructor);

        // typeParameters
        if (def.typeParams != null) {
        	List<TypeVariable> typeParams = new LinkedList<TypeVariable>();
        	for(TypeVariableDef typeVariableDef : def.typeParams) {
        		typeParams.add(createTypeVariable(typeVariableDef));
        	}
            currentMethod.setTypeParameters(typeParams);
        }
        
        // exceptions
        {
            List<Type> exceptions = new LinkedList<Type>();
            for (TypeDef type : def.exceptions) {
                exceptions.add(createType(type, 0));
            }
            currentMethod.setExceptions(exceptions);
        }

        // modifiers
        {
            currentMethod.setModifiers(new LinkedList<String>( def.modifiers ));
        }
        
        currentMethod.setSourceCode(def.body);

        classStack.getFirst().addMethod(currentMethod);
        currentMethod.setParentClass(classStack.getFirst());
    }

    public TypeVariable createTypeVariable(TypeVariableDef typeVariableDef) {
    	if(typeVariableDef == null) {
    		return null;
    	}
    	return TypeVariable.createUnresolved(typeVariableDef, classStack.isEmpty() ? source : classStack.getFirst());

	}

	public void addField(FieldDef def) {
        DefaultJavaField currentField = new DefaultJavaField();
        currentField.setParentClass(classStack.getFirst());
        currentField.setLineNumber(def.lineNumber);
        currentField.setModelWriterFactory(modelWriterFactory);

        currentField.setName(def.name);
        currentField.setType(createType(def.type, def.dimensions));

        // modifiers
        {
            currentField.setModifiers(new LinkedList<String>(def.modifiers));
        }
	
        // code body
        currentField.setInitializationExpression(def.body);
	
        // javadoc
        addJavaDoc(currentField);

        // annotations
        setAnnotations( currentField );

        classStack.getFirst().addField(currentField);
    }
	
	public void addParameter(FieldDef fieldDef) {
	    DefaultJavaParameter jParam = new DefaultJavaParameter(createType(fieldDef.type, fieldDef.dimensions), fieldDef.name, fieldDef.isVarArgs);
        jParam.setParentMethod( currentMethod );
        jParam.setModelWriterFactory(modelWriterFactory);
        addJavaDoc( jParam );
        setAnnotations( jParam );
        currentMethod.addParameter( jParam );
	}

    private void setAnnotations( final AbstractBaseJavaEntity entity ) {
        if( !currentAnnoDefs.isEmpty() ) {
            AnnotationVisitor visitor = new RecursiveAnnotationVisitor() {
                public Object visitAnnotation( Annotation annotation ) {
                    annotation.setContext( entity );
                    return super.visitAnnotation( annotation );
                }
                
                public Object visitAnnotationFieldRef( AnnotationFieldRef fieldRef ) {
                    fieldRef.setContext( entity );
                    return super.visitAnnotationFieldRef( fieldRef );
                }
            };

            List<Annotation> annotations = new LinkedList<Annotation>();
            for( AnnoDef annoDef :  currentAnnoDefs) {
            	Annotation annotation = createAnnotation(annoDef);
            	visitor.visitAnnotation(annotation);
                annotations.add( annotation);
            }
            entity.setAnnotations( annotations );
            currentAnnoDefs.clear();
        }
    }
    
    private Annotation createAnnotation(AnnoDef annoDef) {
    	Annotation annotation = new Annotation(createType(annoDef.typeDef, 0), annoDef.lineNumber);
    	for(Map.Entry<String, AnnotationValue> annoVal : annoDef.args.entrySet()) {
    		annotation.setProperty(annoVal.getKey(), createAnnotation(annoVal.getValue()));
    	}
    	return annotation;
    }
    
    private AnnotationValue createAnnotation(AnnotationValue oldValue) {
		AnnotationValue newValue;
		if(oldValue instanceof AnnoDef) {
			newValue = createAnnotation((AnnoDef) oldValue);
		}
		else if(oldValue instanceof AnnotationValueList) {
			AnnotationValueList annoValList = (AnnotationValueList) oldValue;
			List<AnnotationValue> parsedList = new LinkedList<AnnotationValue>();
			for(AnnotationValue val : annoValList.getValueList()) {
				parsedList.add(createAnnotation(val));
			}
			newValue = new AnnotationValueList(parsedList);
		}
		else {
			newValue = oldValue;
		}
    	return newValue;
    }

    // Don't resolve until we need it... class hasn't been defined yet.
    public void addAnnotation( AnnoDef annotation ) {
        currentAnnoDefs.add( annotation );
    }

    public JavaSource getSource() {
        return source;
    }

}

