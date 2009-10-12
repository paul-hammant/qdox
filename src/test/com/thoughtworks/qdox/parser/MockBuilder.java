package com.thoughtworks.qdox.parser;

import org.jmock.expectation.ExpectationCounter;
import org.jmock.expectation.ExpectationList;

import com.thoughtworks.qdox.model.Annotation;
import com.thoughtworks.qdox.model.Type;
import com.thoughtworks.qdox.parser.structs.ClassDef;
import com.thoughtworks.qdox.parser.structs.FieldDef;
import com.thoughtworks.qdox.parser.structs.MethodDef;
import com.thoughtworks.qdox.parser.structs.PackageDef;
import com.thoughtworks.qdox.parser.structs.TagDef;
import com.thoughtworks.qdox.parser.structs.TypeDef;

class MockBuilder implements Builder {
    private ExpectationCounter myAddPackageCalls = new ExpectationCounter("com.thoughtworks.qdox.parser.Builder AddPackageCalls");
    private ExpectationList myAddPackageParameter0Values = new ExpectationList("com.thoughtworks.qdox.parser.Builder.addPackage() : java.lang.String packageName");
    private ExpectationCounter myAddImportCalls = new ExpectationCounter("com.thoughtworks.qdox.parser.Builder AddImportCalls");
    private ExpectationList myAddImportParameter0Values = new ExpectationList("com.thoughtworks.qdox.parser.Builder.addImport() : java.lang.String importName");
    private ExpectationCounter myAddJavaDocCalls = new ExpectationCounter("com.thoughtworks.qdox.parser.Builder AddJavaDocCalls");
    private ExpectationList myAddJavaDocParameter0Values = new ExpectationList("com.thoughtworks.qdox.parser.Builder.addJavaDoc() : java.lang.String text");
    private ExpectationCounter myAddJavaDocTagCalls = new ExpectationCounter("com.thoughtworks.qdox.parser.Builder AddJavaDocTagCalls");
    private ExpectationList myAddJavaDocTagParameter0Values = new ExpectationList("com.thoughtworks.qdox.parser.Builder.addJavaDocTag() : java.lang.String tag");
    private ExpectationList myAddJavaDocTagParameter1Values = new ExpectationList("com.thoughtworks.qdox.parser.Builder.addJavaDocTag() : java.lang.String text");
    private ExpectationCounter myBeginClassCalls = new ExpectationCounter("com.thoughtworks.qdox.parser.Builder BeginClassCalls");
    private ExpectationList myBeginClassParameter0Values = new ExpectationList("com.thoughtworks.qdox.parser.Builder.beginClass() : com.thoughtworks.qdox.parser.structs.ClassDef def");
    private ExpectationCounter myEndClassCalls = new ExpectationCounter("com.thoughtworks.qdox.parser.Builder EndClassCalls");
    private ExpectationCounter myBeginMethodCalls = new ExpectationCounter("com.thoughtworks.qdox.parser.Builder BeginMethodCalls");
    private ExpectationCounter myEndMethodCalls = new ExpectationCounter("com.thoughtworks.qdox.parser.Builder EndMethodCalls");
    private ExpectationList myEndMethodParameter0Values = new ExpectationList("com.thoughtworks.qdox.parser.Builder.endMethod() : com.thoughtworks.qdox.parser.structs.MethodDef def");
    private ExpectationCounter myAddFieldCalls = new ExpectationCounter("com.thoughtworks.qdox.parser.Builder AddFieldCalls");
    private ExpectationList myAddFieldParameter0Values = new ExpectationList("com.thoughtworks.qdox.parser.Builder.addField() : com.thoughtworks.qdox.parser.structs.FieldDef def");
    private ExpectationCounter myAddParameterCalls = new ExpectationCounter("com.thoughtworks.qdox.parser.Builder AddParameterCalls");
    private ExpectationList myAddParameter0Values = new ExpectationList("com.thoughtworks.qdox.parser.Builder.addParameter() : com.thoughtworks.qdox.parser.structs.FieldDef def");

    
    public void setExpectedAddPackageCalls(int calls) {
        myAddPackageCalls.setExpected(calls);
    }

    public void addExpectedAddPackageValues(PackageDef arg0) {
        myAddPackageParameter0Values.addExpected(arg0);
    }

    public void addPackage(PackageDef arg0) {
        myAddPackageCalls.inc();
        myAddPackageParameter0Values.addActual(arg0);
    }

    public void setExpectedAddImportCalls(int calls) {
        myAddImportCalls.setExpected(calls);
    }

    public void addExpectedAddImportValues(String arg0) {
        myAddImportParameter0Values.addExpected(arg0);
    }

    public void addImport(String arg0) {
        myAddImportCalls.inc();
        myAddImportParameter0Values.addActual(arg0);
    }

    public void setExpectedAddJavaDocCalls(int calls) {
        myAddJavaDocCalls.setExpected(calls);
    }

    public void addExpectedAddJavaDocValues(String arg0) {
        myAddJavaDocParameter0Values.addExpected(arg0);
    }

    public void addJavaDoc(String arg0) {
        myAddJavaDocCalls.inc();
        myAddJavaDocParameter0Values.addActual(arg0);
    }

    public void setExpectedAddJavaDocTagCalls(int calls) {
        myAddJavaDocTagCalls.setExpected(calls);
    }

    public void addExpectedAddJavaDocTagValues(TagDef arg0) {
        myAddJavaDocTagParameter0Values.addExpected(arg0);
    }

    public void addJavaDocTag(TagDef arg0) {
        myAddJavaDocTagCalls.inc();
        myAddJavaDocTagParameter0Values.addActual(arg0);
    }

    public void setExpectedBeginClassCalls(int calls) {
        myBeginClassCalls.setExpected(calls);
    }

    public void addExpectedBeginClassValues(ClassDef arg0) {
        myBeginClassParameter0Values.addExpected(arg0);
    }

    public void beginClass(ClassDef arg0) {
        myBeginClassCalls.inc();
        myBeginClassParameter0Values.addActual(arg0);
    }

    public void setExpectedEndClassCalls(int calls) {
        myEndClassCalls.setExpected(calls);
    }

    public void endClass() {
        myEndClassCalls.inc();
    }

    public void beginMethod() {
        myBeginMethodCalls.inc();
    }
    public void setExpectedEndMethodCalls(int calls) {
        myEndMethodCalls.setExpected(calls);
    }

    public void addExpectedAddMethodValues(MethodDef arg0) {
        myEndMethodParameter0Values.addExpected(arg0);
    }

    public void endMethod(MethodDef arg0) {
        myEndMethodCalls.inc();
        myEndMethodParameter0Values.addActual(arg0);
    }

    public void setExpectedAddFieldCalls(int calls) {
        myAddFieldCalls.setExpected(calls);
    }

    public void addExpectedAddFieldValues(FieldDef arg0) {
        myAddFieldParameter0Values.addExpected(arg0);
    }

    public void addField(FieldDef arg0) {
        myAddFieldCalls.inc();
        myAddFieldParameter0Values.addActual(arg0);
    }

    public void addAnnotation( Annotation annotation ) {
        // Empty
    }
    
    public void verify() {
        myAddPackageCalls.verify();
        myAddPackageParameter0Values.verify();
        myAddImportCalls.verify();
        myAddImportParameter0Values.verify();
        myAddJavaDocCalls.verify();
        myAddJavaDocParameter0Values.verify();
        myAddJavaDocTagCalls.verify();
        myAddJavaDocTagParameter0Values.verify();
        myAddJavaDocTagParameter1Values.verify();
        myBeginClassCalls.verify();
        myBeginClassParameter0Values.verify();
        myEndClassCalls.verify();
        myBeginMethodCalls.verify();
        myEndMethodCalls.verify();
        myEndMethodParameter0Values.verify();
        myAddFieldCalls.verify();
        myAddFieldParameter0Values.verify();
    }

    public Type createType( String name, int dimensions ) {
        return null;
    }
    
    public Type createType(TypeDef name) {
    	return null;
    }

    public void setExpectedAddParameterCalls(int calls) {
        myAddParameterCalls.setExpected(calls);
    }

    public void addExpectedAddParameterValues(FieldDef arg0) {
        myAddParameter0Values.addExpected(arg0);
    }

    public void addParameter(FieldDef arg0) {
        myAddParameterCalls.inc();
        myAddParameter0Values.addActual(arg0);
    }
}
