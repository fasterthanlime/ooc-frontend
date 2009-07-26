package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;

public class Instantiation extends FunctionCall {

	public Instantiation(FunctionCall call) {
		super(call.name, call.suffix);
		arguments.setAll(call.arguments);
	}

	public Instantiation(String name, String suffix) {
		super(name, suffix);
	}
	
	public Instantiation() {
		super("", "");
	}
	
	@Override
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}
	
}
