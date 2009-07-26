package org.ooc.frontend.model;

public abstract class Declaration extends Expression {

	protected String name;
	
	public Declaration(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
}
