package org.ooc.frontend.model;

public class RegularArgument extends Argument {

	private Type type;

	public RegularArgument(Type type, String name) {
		super(name);
		this.type = type;
	}
	
	public Type getType() {
		return type;
	}
	
	public void setType(Type type) {
		this.type = type;
	}
	
}
