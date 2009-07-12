package org.ooc.frontend.model;

import org.ooc.frontend.Visitor;

public class RangeLiteral extends Literal {

	private Expression lower;
	private Expression upper;
	
	public RangeLiteral(Expression lower, Expression upper) {
		this.lower = lower;
		this.upper = upper;
	}
	
	public Expression getLower() {
		return lower;
	}
	
	public void setLower(Expression lower) {
		this.lower = lower;
	}
	
	public Expression getUpper() {
		return upper;
	}
	
	public void setUpper(Expression upper) {
		this.upper = upper;
	}

	@Override
	public Type getType() {
		return new Type("Range");
	}
	
	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
	
	@Override
	public void acceptChildren(Visitor visitor) {}

}
