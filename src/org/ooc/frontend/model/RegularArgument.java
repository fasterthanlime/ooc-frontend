package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;

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

	@Override
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}

	@Override
	public void acceptChildren(Visitor visitor) throws IOException {}
	
}
