package com.thoughtworks.qdox.ant;

import com.thoughtworks.qdox.model.JavaClass;

public class ConsoleLoggingQdoxTask extends AbstractQdoxTask {

	public void execute() {
		super.execute();
		printClassNames();
	}

	protected void printClassNames() {
		for (int i = 0; i < allClasses.size(); i++) {
			JavaClass javaClass = (JavaClass) allClasses.get(i);
			System.out.println("Class:" + javaClass.getName());
		}
	}

}
