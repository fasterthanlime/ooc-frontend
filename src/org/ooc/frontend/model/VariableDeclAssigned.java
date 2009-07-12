package org.ooc.frontend.model;

import org.ooc.frontend.Visitor;

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
	
	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
	
	@Override
	public void acceptChildren(Visitor visitor) {
		expression.accept(visitor);
	}
	
}

