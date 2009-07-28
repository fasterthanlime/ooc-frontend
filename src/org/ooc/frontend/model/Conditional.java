package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;

public abstract class Conditional extends ControlStatement {

	protected Expression condition;

	public Conditional(Expression condition) {
		super();
		this.condition = condition;
	}
	
	public Expression getCondition() {
		return condition;
	}
	
	public void setCondition(Expression condition) {
		this.condition = condition;
	}
	
	@Override
	public final void acceptChildren(Visitor visitor) throws IOException {
		condition.accept(visitor);
		body.accept(visitor);
	}
	
	@Override
	public boolean hasChildren() {
		return true;
	}
	
	@Override
	public boolean replace(Node oldie, Node kiddo) {
		
		if(oldie == condition) {
			condition = (Expression) kiddo;
			return true;
		}
		
		return false;
	}
	
}
