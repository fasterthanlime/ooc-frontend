package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;

public class VarArg extends Argument {

	public VarArg() {
		super("...");
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
