package org.ooc.frontend.model;

public abstract class Argument extends VariableDecl {

	public Argument(Type type, String name, boolean isConst) {
		super(type, name, isConst, false);
	}
	
}
