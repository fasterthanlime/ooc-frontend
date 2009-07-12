package org.ooc.frontend.model;

import org.ooc.frontend.Visitor;

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
	
	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
	
	@Override
	public void acceptChildren(Visitor visitor) {}
	
}
