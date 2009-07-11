package org.ooc.frontend.model;

public class Parenthesis extends Expression {

	Expression expression;
	
	public Parenthesis(Expression expression) {
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
		return expression.getType();
	}

}
