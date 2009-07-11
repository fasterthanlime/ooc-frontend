package org.ooc.frontend.model;

public class Import extends Node {

	private String name;

	public Import(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
}
