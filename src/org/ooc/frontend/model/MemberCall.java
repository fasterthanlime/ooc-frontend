package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;

public class MemberCall extends FunctionCall {

	private Expression expression;

	public MemberCall(Expression expression, String name, String suffix) {
		super(name, suffix);
		this.expression = expression;
	}
	
	public MemberCall(Expression expression, FunctionCall call) {
		super(call.name, call.suffix);
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
	
	@Override
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}
	
}
