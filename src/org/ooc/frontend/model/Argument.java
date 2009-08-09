package org.ooc.frontend.model;

public abstract class Argument extends VariableDecl {

	public Argument(Type type, String name, boolean isConst) {
		super(type, isConst, false);
		this.name = name;
	}
	
	@Override
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
}
