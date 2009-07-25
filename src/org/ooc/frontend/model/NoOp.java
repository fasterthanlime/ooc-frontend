package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;

public class NoOp extends Statement {

	@Override
	public void accept(Visitor visitor) throws IOException {}

	@Override
	public void acceptChildren(Visitor visitor) throws IOException {}

	@Override
	public boolean hasChildren() {
		return false;
	}

}
