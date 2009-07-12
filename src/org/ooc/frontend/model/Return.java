package org.ooc.frontend.model;

import org.ooc.frontend.Visitor;

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
	
	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
	
	@Override
	public void acceptChildren(Visitor visitor) {
		expression.accept(visitor);
	}
	
}
