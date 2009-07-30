package org.ooc.frontend.parser;

import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.Argument;
import org.ooc.frontend.model.Type;

public class TypeArgument extends Argument {

	public TypeArgument(Type type) {
		this(type, false);
	}
	
	public TypeArgument(Type type, boolean isConst) {
		// TODO check if empty name isn't a problem
		super(type, "", isConst);
	}
	
	@Override
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}

}
