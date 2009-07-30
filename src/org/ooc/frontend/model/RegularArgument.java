package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;

public class RegularArgument extends Argument {

	public RegularArgument(Type type, String name) {
		this(type, name, false);
	}
	
	public RegularArgument(Type type, String name, boolean isConst) {
		super(type, name, isConst);
	}

	@Override
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}
	
}
