// Generated MockObject for com.thoughtworks.qdox.parser.Builder. Do not edit!

package com.thoughtworks.qdox.parser.test;

import java.lang.*;
import com.thoughtworks.qdox.parser.structs.FieldDef;
import com.thoughtworks.qdox.parser.structs.ClassDef;
import com.thoughtworks.qdox.parser.structs.MethodDef;

import com.thoughtworks.qdox.parser.*;
import com.mockobjects.*;
import mockmaker.ReturnValues;

public class MockBuilder implements Builder, Verifiable {

	// Methods for addPackage(java.lang.String packageName)

	private ExpectationCounter myAddPackageCalls = new ExpectationCounter("com.thoughtworks.qdox.parser.Builder AddPackage");
	private ExpectationList myAddPackageParameterPackageNameValues0 = new ExpectationList("com.thoughtworks.qdox.parser.Builder addPackage packageName");

	public void addPackage(java.lang.String packageName) {
		myAddPackageCalls.inc();

		myAddPackageParameterPackageNameValues0.addActual(packageName);

	}

	public void setExpectedAddPackageCalls(int calls) {
		myAddPackageCalls.setExpected(calls);
	}


	public void addExpectedAddPackageValues(java.lang.String packageName){
		myAddPackageParameterPackageNameValues0.addExpected(packageName);
	}

	// Methods for addImport(java.lang.String importName)

	private ExpectationCounter myAddImportCalls = new ExpectationCounter("com.thoughtworks.qdox.parser.Builder AddImport");
	private ExpectationList myAddImportParameterImportNameValues0 = new ExpectationList("com.thoughtworks.qdox.parser.Builder addImport importName");

	public void addImport(java.lang.String importName) {
		myAddImportCalls.inc();

		myAddImportParameterImportNameValues0.addActual(importName);

	}

	public void setExpectedAddImportCalls(int calls) {
		myAddImportCalls.setExpected(calls);
	}


	public void addExpectedAddImportValues(java.lang.String importName){
		myAddImportParameterImportNameValues0.addExpected(importName);
	}

	// Methods for addJavaDoc(java.lang.String text)

	private ExpectationCounter myAddJavaDocCalls = new ExpectationCounter("com.thoughtworks.qdox.parser.Builder AddJavaDoc");
	private ExpectationList myAddJavaDocParameterTextValues0 = new ExpectationList("com.thoughtworks.qdox.parser.Builder addJavaDoc text");

	public void addJavaDoc(java.lang.String text) {
		myAddJavaDocCalls.inc();

		myAddJavaDocParameterTextValues0.addActual(text);

	}

	public void setExpectedAddJavaDocCalls(int calls) {
		myAddJavaDocCalls.setExpected(calls);
	}


	public void addExpectedAddJavaDocValues(java.lang.String text){
		myAddJavaDocParameterTextValues0.addExpected(text);
	}

	// Methods for addJavaDocTag(java.lang.String tag,java.lang.String text)

	private ExpectationCounter myAddJavaDocTagCalls = new ExpectationCounter("com.thoughtworks.qdox.parser.Builder AddJavaDocTag");
	private ExpectationList myAddJavaDocTagParameterTagValues1 = new ExpectationList("com.thoughtworks.qdox.parser.Builder addJavaDocTag tag");
	private ExpectationList myAddJavaDocTagParameterTextValues1 = new ExpectationList("com.thoughtworks.qdox.parser.Builder addJavaDocTag text");

	public void addJavaDocTag(java.lang.String tag,java.lang.String text) {
		myAddJavaDocTagCalls.inc();

		myAddJavaDocTagParameterTagValues1.addActual(tag);
		myAddJavaDocTagParameterTextValues1.addActual(text);

	}

	public void setExpectedAddJavaDocTagCalls(int calls) {
		myAddJavaDocTagCalls.setExpected(calls);
	}


	public void addExpectedAddJavaDocTagValues(java.lang.String tag,java.lang.String text){
		myAddJavaDocTagParameterTagValues1.addExpected(tag);
		myAddJavaDocTagParameterTextValues1.addExpected(text);
	}

	// Methods for addClass(com.thoughtworks.qdox.parser.structs.ClassDef def)

	private ExpectationCounter myAddClassCalls = new ExpectationCounter("com.thoughtworks.qdox.parser.Builder AddClass");
	private ExpectationList myAddClassParameterDefValues2 = new ExpectationList("com.thoughtworks.qdox.parser.Builder addClass def");

	public void addClass(com.thoughtworks.qdox.parser.structs.ClassDef def) {
		myAddClassCalls.inc();

		myAddClassParameterDefValues2.addActual(def);

	}

	public void setExpectedAddClassCalls(int calls) {
		myAddClassCalls.setExpected(calls);
	}


	public void addExpectedAddClassValues(com.thoughtworks.qdox.parser.structs.ClassDef def){
		myAddClassParameterDefValues2.addExpected(def);
	}

	// Methods for addMethod(com.thoughtworks.qdox.parser.structs.MethodDef def)

	private ExpectationCounter myAddMethodCalls = new ExpectationCounter("com.thoughtworks.qdox.parser.Builder AddMethod");
	private ExpectationList myAddMethodParameterDefValues3 = new ExpectationList("com.thoughtworks.qdox.parser.Builder addMethod def");

	public void addMethod(com.thoughtworks.qdox.parser.structs.MethodDef def) {
		myAddMethodCalls.inc();

		myAddMethodParameterDefValues3.addActual(def);

	}

	public void setExpectedAddMethodCalls(int calls) {
		myAddMethodCalls.setExpected(calls);
	}


	public void addExpectedAddMethodValues(com.thoughtworks.qdox.parser.structs.MethodDef def){
		myAddMethodParameterDefValues3.addExpected(def);
	}

	// Methods for addField(com.thoughtworks.qdox.parser.structs.FieldDef def)

	private ExpectationCounter myAddFieldCalls = new ExpectationCounter("com.thoughtworks.qdox.parser.Builder AddField");
	private ExpectationList myAddFieldParameterDefValues4 = new ExpectationList("com.thoughtworks.qdox.parser.Builder addField def");

	public void addField(com.thoughtworks.qdox.parser.structs.FieldDef def) {
		myAddFieldCalls.inc();

		myAddFieldParameterDefValues4.addActual(def);

	}

	public void setExpectedAddFieldCalls(int calls) {
		myAddFieldCalls.setExpected(calls);
	}


	public void addExpectedAddFieldValues(com.thoughtworks.qdox.parser.structs.FieldDef def){
		myAddFieldParameterDefValues4.addExpected(def);
	}


	// Verify method

	public void verify() {
		myAddPackageCalls.verify();
		myAddPackageParameterPackageNameValues0.verify();
		myAddImportCalls.verify();
		myAddImportParameterImportNameValues0.verify();
		myAddJavaDocCalls.verify();
		myAddJavaDocParameterTextValues0.verify();
		myAddJavaDocTagCalls.verify();
		myAddJavaDocTagParameterTagValues1.verify();
		myAddJavaDocTagParameterTextValues1.verify();
		myAddClassCalls.verify();
		myAddClassParameterDefValues2.verify();
		myAddMethodCalls.verify();
		myAddMethodParameterDefValues3.verify();
		myAddFieldCalls.verify();
		myAddFieldParameterDefValues4.verify();
	}

}
