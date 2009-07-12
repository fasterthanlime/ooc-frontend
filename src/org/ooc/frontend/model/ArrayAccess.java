package org.ooc.frontend.model;

import org.ooc.frontend.Visitor;

public class ArrayAccess extends Access {

	private Expression variable;
	private Expression index;

	public ArrayAccess(Expression variable, Expression index) {
		this.variable = variable;
		this.index = index;
	}
	
	public Expression getVariable() {
		return variable;
	}
	
	public void setVariable(Expression variable) {
		this.variable = variable;
	}

	public Expression getIndex() {
		return index;
	}
	
	public void setIndex(Expression index) {
		this.index = index;
	}
	
	@Override
	public Type getType() {
		throw new UnsupportedOperationException("ArrayAccess doesn't resolve types just yet ;)");
	}
	
	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
	
	@Override
	public void acceptChildren(Visitor visitor) {
		variable.accept(visitor);
		index.accept(visitor);
	}
	
}
