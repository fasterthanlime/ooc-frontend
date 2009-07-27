package org.ooc.frontend.model;

public abstract class Argument extends VariableDecl {

	public Argument(Type type, String name) {
		super(type, name, false, false);
	}
	
}
