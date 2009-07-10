package org.ooc.frontend.model;

public class MemberCall extends FunctionCall {

	private Expression expression;

	public MemberCall(Expression expression, String name) {
		super(name);
		this.expression = expression;
	}
	
	public MemberCall(Expression expression, FunctionCall call) {
		super(call.getName());
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
		throw new UnsupportedOperationException(this.getClass()+" can't figure out return type just yet.");
	}
	
}
