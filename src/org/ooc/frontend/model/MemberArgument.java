package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;

public class MemberArgument extends Argument {

	public MemberArgument(String name) {
		super(name);
	}
	
	@Override
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}

	@Override
	public void acceptChildren(Visitor visitor) throws IOException {}
	
}
