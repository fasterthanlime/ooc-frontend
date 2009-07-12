package org.ooc.frontend.model;

import org.ooc.frontend.Visitor;

public class Include extends Node {

	private String include;

	public Include(String include) {
		this.include = include;
	}
	
	public String getInclude() {
		return include;
	}
	
	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
	
	@Override
	public void acceptChildren(Visitor visitor) {}
	
}
