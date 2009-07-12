package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;


public class BoolLiteral extends Literal {

	private boolean value;
	
	public BoolLiteral(boolean value) {
		this.value = value;
	}
	
	public boolean getValue() {
		return value;
	}
	
	public void setValue(boolean value) {
		this.value = value;
	}

	@Override
	public Type getType() {
		return new Type("Bool");
	}
	
	@Override
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}
	
	@Override
	public void acceptChildren(Visitor visitor) throws IOException {}

}
