package com.thoughtworks.qdox.parser;

import com.mockobjects.*;
import com.thoughtworks.qdox.parser.Builder;
import com.thoughtworks.qdox.parser.structs.ClassDef;
import com.thoughtworks.qdox.parser.structs.MethodDef;
import com.thoughtworks.qdox.parser.structs.FieldDef;

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
    private ExpectationCounter myAddMethodCalls = new ExpectationCounter("com.thoughtworks.qdox.parser.Builder AddMethodCalls");
    private ExpectationList myAddMethodParameter0Values = new ExpectationList("com.thoughtworks.qdox.parser.Builder.addMethod() : com.thoughtworks.qdox.parser.structs.MethodDef def");
    private ExpectationCounter myAddFieldCalls = new ExpectationCounter("com.thoughtworks.qdox.parser.Builder AddFieldCalls");
    private ExpectationList myAddFieldParameter0Values = new ExpectationList("com.thoughtworks.qdox.parser.Builder.addField() : com.thoughtworks.qdox.parser.structs.FieldDef def");

    public void setExpectedAddPackageCalls(int calls) {
        myAddPackageCalls.setExpected(calls);
    }

    public void addExpectedAddPackageValues(String arg0) {
        myAddPackageParameter0Values.addExpected(arg0);
    }

    public void addPackage(String arg0) {
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

    public void addExpectedAddJavaDocTagValues(String arg0, String arg1) {
        myAddJavaDocTagParameter0Values.addExpected(arg0);
        myAddJavaDocTagParameter1Values.addExpected(arg1);
    }

    public void addJavaDocTag(String arg0, String arg1) {
        myAddJavaDocTagCalls.inc();
        myAddJavaDocTagParameter0Values.addActual(arg0);
        myAddJavaDocTagParameter1Values.addActual(arg1);
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

    public void setExpectedAddMethodCalls(int calls) {
        myAddMethodCalls.setExpected(calls);
    }

    public void addExpectedAddMethodValues(MethodDef arg0) {
        myAddMethodParameter0Values.addExpected(arg0);
    }

    public void addMethod(MethodDef arg0) {
        myAddMethodCalls.inc();
        myAddMethodParameter0Values.addActual(arg0);
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
        myAddMethodCalls.verify();
        myAddMethodParameter0Values.verify();
        myAddFieldCalls.verify();
        myAddFieldParameter0Values.verify();
    }
}
