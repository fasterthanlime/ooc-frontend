package org.ooc.frontend.model;


public abstract class TypeDeclaration extends Declaration {

	public TypeDeclaration(String name) {
		super(name);
	}
	
	public abstract Type getInstanceType();

}
