package org.ooc.frontend.model;

import org.ooc.frontend.Visitor;

public class If extends Conditional {

	public If(Expression condition) {
		super(condition);
	}
	
	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
	
	@Override
	public void acceptChildren(Visitor visitor) {}
	
}
