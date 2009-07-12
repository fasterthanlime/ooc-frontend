package org.ooc.frontend.model;

import org.ooc.frontend.Visitor;

public class CharLiteral extends Literal {

	private char value;
	
	public CharLiteral(char value) {
		this.value = value;
	}
	
	public char getValue() {
		return value;
	}
	
	public void setValue(char value) {
		this.value = value;
	}
	
	@Override
	public Type getType() {
		return new Type("Char");
	}
	
	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
	
	@Override
	public void acceptChildren(Visitor visitor) {}

}
