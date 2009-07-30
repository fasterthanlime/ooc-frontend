package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;

public class MemberArgument extends Argument {

	public MemberArgument(String name) {
		this(name, false);
	}
	
	public MemberArgument(String name, boolean isConst) {
		super(new Type(""), name, isConst);
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
