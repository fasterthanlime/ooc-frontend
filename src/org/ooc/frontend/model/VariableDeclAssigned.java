package org.ooc.frontend.model;

public class VariableDeclAssigned extends VariableDecl {

	private Expression expression;

	public VariableDeclAssigned(Type type, String name, Expression expression) {
		super(type, name);
		this.expression = expression;
	}
	
	public Expression getExpression() {
		return expression;
	}
	
}
