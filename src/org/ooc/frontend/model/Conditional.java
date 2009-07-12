package org.ooc.frontend.model;

public abstract class Conditional extends ControlStatement {

	private Expression condition;

	public Conditional(Expression condition) {
		super();
		this.condition = condition;
	}
	
	public Expression getCondition() {
		return condition;
	}
	
	public void setCondition(Expression condition) {
		this.condition = condition;
	}
	
}
