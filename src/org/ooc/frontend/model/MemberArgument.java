package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;

public class MemberArgument extends Argument {

	public MemberArgument(String name) {
		// TODO add special trickery to resolve the type
		super(new Type(""), name);
	}
	
	@Override
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}
	
	@Override
	public boolean hasChildren() {
		return false;
	}

	@Override
	public void acceptChildren(Visitor visitor) throws IOException {}
	
}
