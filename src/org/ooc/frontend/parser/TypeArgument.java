package org.ooc.frontend.parser;

import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.Argument;
import org.ooc.frontend.model.Type;

public class TypeArgument extends Argument {

	private Type type;

	public TypeArgument(Type type) {
		super("");
		this.type = type;
	}
	
	public Type getType() {
		return type;
	}
	
	@Override
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}

	@Override
	public void acceptChildren(Visitor visitor) throws IOException {}

}
