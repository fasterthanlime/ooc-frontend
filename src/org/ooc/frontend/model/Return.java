package org.ooc.frontend.model;

public class Return extends Statement {

	private Expression expression;

	public Return(Expression expression) {
		this.expression = expression;
	}
	
	public Expression getExpression() {
		return expression;
	}
	
	public void setExpression(Expression expression) {
		this.expression = expression;
	}
	
}
