package org.ooc.frontend.model;

import java.io.IOException;

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
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}
	
	@Override
	public boolean hasChildren() {
		return true;
	}
	
	@Override
	public void acceptChildren(Visitor visitor) throws IOException {
		variable.accept(visitor);
		index.accept(visitor);
	}
	
}
