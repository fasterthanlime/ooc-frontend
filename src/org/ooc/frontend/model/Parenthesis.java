package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;

public class Parenthesis extends Expression {

	NodeList<Expression> expressions;
	private static Type tupleType = new Type("Tuple");
	
	public Parenthesis(Expression expression) {
		this.expressions = new NodeList<Expression>();
		this.expressions.add(expression);
	}

	public Expression getExpression() {
		if(expressions.size() == 1) return expressions.get(0);
		throw new UnsupportedOperationException(
				"Trying to getExpression on a Parenthesis which has several expressions: "+expressions);
	}
	
	public void setExpression(Expression expression) {
		if(expressions.size() == 1) {
			expressions.set(0, expression);
			return;
		}
		throw new UnsupportedOperationException(
				"Trying to setExpression on a Parenthesis which has several expressions: "+expressions);
	}

	@Override
	public Type getType() {
		if(expressions.size() == 1) return expressions.get(0).getType();
		return tupleType;
	}

	@Override
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}
	
	@Override
	public boolean hasChildren() {
		return true;
	}
	
	@Override
	public void acceptChildren(Visitor visitor) throws IOException {
		expressions.accept(visitor);
		tupleType.accept(visitor);
	}
	
	@Override
	public boolean replace(Node oldie, Node kiddo) {
		return false;
	}
	
}
