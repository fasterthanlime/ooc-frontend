package org.ooc.frontend.model;

public class Assignment extends Expression {

	private Access lvalue;
	private Expression rvalue;
	
	public Assignment(Access lvalue, Expression rvalue) {
		this.lvalue = lvalue;
		this.rvalue = rvalue;
	}
	
	public Access getLvalue() {
		return lvalue;
	}
	
	public Expression getRvalue() {
		return rvalue;
	}

	@Override
	public Type getType() {
		
		return lvalue.getType();
		
	}
	
}
