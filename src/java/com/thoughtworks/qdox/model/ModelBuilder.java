package com.thoughtworks.qdox.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import com.thoughtworks.qdox.model.annotation.AnnotationFieldRef;
import com.thoughtworks.qdox.model.annotation.AnnotationVisitor;
import com.thoughtworks.qdox.model.annotation.RecursiveAnnotationVisitor;
import com.thoughtworks.qdox.parser.Builder;
import com.thoughtworks.qdox.parser.structs.ClassDef;
import com.thoughtworks.qdox.parser.structs.FieldDef;
import com.thoughtworks.qdox.parser.structs.MethodDef;
import com.thoughtworks.qdox.parser.structs.TagDef;
import com.thoughtworks.qdox.parser.structs.TypeDef;

/**
 * @author <a href="mailto:joew@thoughtworks.com">Joe Walnes</a>
 * @author Robert Scholte
 */
public class ModelBuilder implements Builder {

    private final ClassLibrary classLibrary;
    private final JavaSource source;
    private JavaClassParent currentParent;
    private JavaClass currentClass;
    private List currentAnnoDefs;
    private String lastComment;
    private List lastTagSet;
    private DocletTagFactory docletTagFactory;

    public ModelBuilder() {
        this(new ClassLibrary(null), new DefaultDocletTagFactory());
    }

    public ModelBuilder(ClassLibrary classLibrary, DocletTagFactory docletTagFactory) {
        this.classLibrary = classLibrary;
        this.docletTagFactory = docletTagFactory;
        source = new JavaSource();
        source.setClassLibrary(classLibrary);
        currentParent = source;
        currentAnnoDefs = new ArrayList();
    }

    public void addPackage(String packageName) {
        source.setPackage(packageName);
    }

    public void addImport(String importName) {
        source.addImport(importName);
    }

    public void addJavaDoc(String text) {
        lastComment = text;
        lastTagSet = new LinkedList();
    }

    public void addJavaDocTag(TagDef tagDef) {
        lastTagSet.add(tagDef);
    }

    public void beginClass(ClassDef def) {
        currentClass = new JavaClass();
        currentClass.setParent(currentParent);
        currentClass.setLineNumber(def.lineNumber);

        // basic details
        currentClass.setName(def.name);
        currentClass.setInterface(ClassDef.INTERFACE.equals(def.type));
        currentClass.setEnum(ClassDef.ENUM.equals(def.type));
        currentClass.setAnnotation(ClassDef.ANNOTATION_TYPE.equals(def.type));


        // superclass
        if (currentClass.isInterface()) {
            currentClass.setSuperClass(null);
        } else if (!currentClass.isEnum()) {
            currentClass.setSuperClass(def.extendz.size() > 0 ? createType((TypeDef) def.extendz.toArray()[0], 0) : null);
        }

        // implements
        {
            Set implementSet = currentClass.isInterface() ? def.extendz : def.implementz;
            Iterator implementIt = implementSet.iterator();
            Type[] implementz = new Type[implementSet.size()];
            for (int i = 0; i < implementz.length && implementIt.hasNext(); i++) {
                implementz[i] = createType((TypeDef) implementIt.next(), 0);
            }
            currentClass.setImplementz(implementz);
        }

        // modifiers
        {
            String[] modifiers = new String[def.modifiers.size()];
            def.modifiers.toArray(modifiers);
            currentClass.setModifiers(modifiers);
        }

        // javadoc
        addJavaDoc(currentClass);

//        // ignore annotation types (for now)
//        if (ClassDef.ANNOTATION_TYPE.equals(def.type)) {
//        	System.out.println( currentClass.getFullyQualifiedName() );
//            return;
//        }

        // annotations
        setAnnotations( currentClass );

        currentParent.addClass(currentClass);
        currentParent = currentClass;
        classLibrary.add(currentClass.getFullyQualifiedName());
    }

    public void endClass() {
        currentParent = currentClass.getParent();
        if (currentParent instanceof JavaClass) {
            currentClass = (JavaClass) currentParent;
        } else {
            currentClass = null;
        }
    }

    public Type createType( String typeName, int dimensions ) {
        if( typeName == null || typeName.equals( "" ) )
            return null;
        return createType(new TypeDef(typeName), dimensions);
    }
    
    public Type createType(TypeDef typeDef) {
    	return createType(typeDef, 0);
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
    	return Type.createUnresolved(typeDef, dimensions, currentClass == null ? currentParent : currentClass);
    }

    private void addJavaDoc(AbstractJavaEntity entity) {
        if (lastComment == null) return;

        entity.setComment(lastComment);
        
        Iterator tagDefIterator = lastTagSet.iterator();
        List tagList = new ArrayList();
        while (tagDefIterator.hasNext()) {
            TagDef tagDef = (TagDef) tagDefIterator.next();
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
        JavaMethod currentMethod = new JavaMethod();
        currentMethod.setParentClass(currentClass);
        currentMethod.setLineNumber(def.lineNumber);

        // basic details
        currentMethod.setName(def.name);
        currentMethod.setReturns(createType(def.returnType, def.dimensions));
        currentMethod.setConstructor(def.constructor);

        // parameters
        {
            JavaParameter[] params = new JavaParameter[def.params.size()];
            int i = 0;
            for (Iterator iterator = def.params.iterator(); iterator.hasNext();) {
                FieldDef fieldDef = (FieldDef) iterator.next();
                params[i++] = new JavaParameter(createType(fieldDef.type, fieldDef.dimensions), fieldDef.name, fieldDef.isVarArgs);
            }
            currentMethod.setParameters(params);
        }

        // exceptions
        {
            Type[] exceptions = new Type[def.exceptions.size()];
            int index = 0;
            for (Iterator iter = def.exceptions.iterator(); iter.hasNext();) {
                exceptions[index++] = createType((String) iter.next(), 0);
            }
            currentMethod.setExceptions(exceptions);
        }

        // modifiers
        {
            String[] modifiers = new String[def.modifiers.size()];
            def.modifiers.toArray(modifiers);
            currentMethod.setModifiers(modifiers);
        }
        
        currentMethod.setSourceCode(def.body);

        // javadoc
        addJavaDoc(currentMethod);

        // annotations
        setAnnotations( currentMethod );

        currentClass.addMethod(currentMethod);
    }

    public void addField(FieldDef def) {
        JavaField currentField = new JavaField();
        currentField.setParent(currentClass);
        currentField.setLineNumber(def.lineNumber);

        currentField.setName(def.name);
        currentField.setType(createType(def.type, def.dimensions));

        // modifiers
        {
            String[] modifiers = new String[def.modifiers.size()];
            def.modifiers.toArray(modifiers);
            currentField.setModifiers(modifiers);
        }
	
        // code body
        currentField.setInitializationExpression(def.body);
	
        // javadoc
        addJavaDoc(currentField);

        // annotations
        setAnnotations( currentField );

        currentClass.addField(currentField);
    }

    private void setAnnotations( final AbstractJavaEntity entity ) {
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

            Annotation[] annotations = new Annotation[currentAnnoDefs.size()];
            for( ListIterator iter = currentAnnoDefs.listIterator(); iter.hasNext(); ) {
                Annotation annotation = (Annotation) iter.next();
                annotation.accept(visitor);
                annotations[iter.previousIndex()] = annotation;
            }

            entity.setAnnotations( annotations );
            currentAnnoDefs.clear();
        }
    }

    // Don't resolve until we need it... class hasn't been defined yet.
    public void addAnnotation( Annotation annotation ) {
        currentAnnoDefs.add( annotation );
    }

    public JavaSource getSource() {
        return source;
    }

}

