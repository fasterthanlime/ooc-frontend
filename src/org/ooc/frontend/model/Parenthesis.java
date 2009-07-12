package org.ooc.frontend.model;

import org.ooc.frontend.Visitor;

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

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
	
	@Override
	public void acceptChildren(Visitor visitor) {
		expression.accept(visitor);
	}
	
}
