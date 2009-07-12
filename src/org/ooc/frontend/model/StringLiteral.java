package org.ooc.frontend.model;

import org.ooc.frontend.Visitor;

public class StringLiteral extends Literal {

	private String value;
	
	public StringLiteral(String value) {
		this.value = value;
	}
	
	@Override
	public Type getType() {
		return new Type("String");
	}
	
	public String getValue() {
		return value;
	}
	
	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
	
	@Override
	public void acceptChildren(Visitor visitor) {}

}
