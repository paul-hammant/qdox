// Generated MockObject for com.thoughtworks.qdox.directorywalker.FileVisitor. Do not edit!

package com.thoughtworks.qdox.directorywalker.test;

import java.lang.*;
import java.io.File;

import com.thoughtworks.qdox.directorywalker.*;
import com.mockobjects.*;
import mockmaker.ReturnValues;

public class MockFileVisitor implements FileVisitor, Verifiable {

	// Methods for visitFile(java.io.File file)

	private ExpectationCounter myVisitFileCalls = new ExpectationCounter("com.thoughtworks.qdox.directorywalker.FileVisitor VisitFile");
	private ExpectationList myVisitFileParameterFileValues0 = new ExpectationList("com.thoughtworks.qdox.directorywalker.FileVisitor visitFile file");

	public void visitFile(java.io.File file) {
		myVisitFileCalls.inc();

		myVisitFileParameterFileValues0.addActual(file);

	}

	public void setExpectedVisitFileCalls(int calls) {
		myVisitFileCalls.setExpected(calls);
	}


	public void addExpectedVisitFileValues(java.io.File file){
		myVisitFileParameterFileValues0.addExpected(file);
	}


	// Verify method

	public void verify() {
		myVisitFileCalls.verify();
		myVisitFileParameterFileValues0.verify();
	}

}
