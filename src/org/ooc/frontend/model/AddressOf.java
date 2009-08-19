package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;

public class AddressOf extends Access {

	Type type;
	Expression expression;
	
	public AddressOf(Expression expression) {
		setExpression(expression);
	}

	@Override
	public boolean replace(Node oldie, Node kiddo) {
		if(expression == oldie) {
			expression = (Expression) kiddo;
			return true;
		}
		
		return false;
	}

	@Override
	public Type getType() {
		if(type == null) {
			Type exprType = expression.getType();
			if(exprType != null) {
				this.type = new Type(exprType.getName(), exprType.getPointerLevel() + 1);
			}
		}
		return type;
	}
	
	public Expression getExpression() {
		return expression;
	}
	
	public void setExpression(Expression expression) {
		this.expression = expression;
	}

	@Override
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}

	@Override
	public void acceptChildren(Visitor visitor) throws IOException {
		expression.accept(visitor);
	}

	@Override
	public boolean hasChildren() {
		return true;
	}

}
