package org.ooc.frontend.model;

import org.ooc.frontend.Visitor;

public class MemberAssignArgument extends MemberArgument {

	public MemberAssignArgument(String name) {
		super(name);
	}
	
	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}

	@Override
	public void acceptChildren(Visitor visitor) {}
	
}
