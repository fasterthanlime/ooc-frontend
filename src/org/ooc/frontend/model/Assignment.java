package org.ooc.frontend.model;

import org.ooc.frontend.Visitor;

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
	
	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
	
	@Override
	public void acceptChildren(Visitor visitor) {
		lvalue.accept(visitor);
		rvalue.accept(visitor);
	}
	
}
