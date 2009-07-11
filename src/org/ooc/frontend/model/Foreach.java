package org.ooc.frontend.model;

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
	
}
