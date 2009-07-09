package org.ooc.frontend.model;

public class VariableDecl extends Declaration {

	private Type type;
	private String name;
	
	public VariableDecl(Type type, String name) {
		this.type = type;
		this.name = name;
	}
	
	public Type getType() {
		return type;
	}
	
	public String getName() {
		return name;
	}
	
}
