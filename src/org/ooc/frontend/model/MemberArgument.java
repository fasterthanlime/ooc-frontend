package org.ooc.frontend.model;

import org.ooc.frontend.Visitor;

public class MemberArgument extends Argument {

	public MemberArgument(String name) {
		super(name);
	}
	
	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}

	@Override
	public void acceptChildren(Visitor visitor) {}
	
}
