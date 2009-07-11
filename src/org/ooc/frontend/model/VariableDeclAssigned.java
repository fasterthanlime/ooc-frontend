package org.ooc.frontend.model;

public class VariableDeclAssigned extends VariableDecl {

	private Expression expression;

	public VariableDeclAssigned(Type type, String name, Expression expression,
			boolean isConst, boolean isStatic) {
		super(type, name, isConst, isStatic);
		this.expression = expression;
	}
	
	public Expression getExpression() {
		return expression;
	}
	
}
