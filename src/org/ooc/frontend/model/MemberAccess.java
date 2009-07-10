package org.ooc.frontend.model;

public class MemberAccess extends VariableAccess {
	
	private Expression expression;

	public MemberAccess(Expression expression, String variable) {
		super(variable);
		this.expression = expression;
	}
	
	public MemberAccess(Expression expression, VariableAccess variableAccess) {
		super(variableAccess.getVariable());
		this.expression = expression;
	}

	public Expression getExpression() {
		return expression;
	}
	
	public void setExpression(Expression expression) {
		this.expression = expression;
	}

}
