package org.ooc.frontend.model;

public abstract class Argument {

	private String name;

	public Argument(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
}
