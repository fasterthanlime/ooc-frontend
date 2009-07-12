package org.ooc.frontend.model;

import org.ooc.frontend.Visitor;

public class Foreach extends ControlStatement {

	private VariableDecl variable;
	private Expression collection; // must be of type Range or Iterable
	
	public Foreach(VariableDecl variable, Expression collection) {
		this.variable = variable;
		this.collection = collection;
	}
	
	public VariableDecl getVariable() {
		return variable;
	}
	
	public void setVariable(VariableDecl variable) {
		this.variable = variable;
	}
	
	public Expression getCollection() {
		return collection;
	}
	
	public void setCollection(Expression range) {
		this.collection = range;
	}
	
	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
	
	@Override
	public void acceptChildren(Visitor visitor) {
		variable.accept(visitor);
		collection.accept(visitor);
	}
	
}
