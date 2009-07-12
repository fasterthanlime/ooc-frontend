package org.ooc.frontend.model;

public class Not extends Expression {

	private Expression expression;
	
	public Not(Expression expression) {
		this.expression = expression;
	}

	public Expression getExpression() {
		return expression;
	}
	
	public void setExpression(Expression expression) {
		this.expression = expression;
	}

	@Override
	public Type getType() {
		return new Type("Bool");
	}

}
