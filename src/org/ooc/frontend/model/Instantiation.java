package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;

public class Instantiation extends FunctionCall {

	public Instantiation(FunctionCall call) {
		super(call.getName());
		arguments.setAll(call.arguments);
	}

	public Instantiation(String name) {
		super(name);
	}
	
	public Instantiation() {
		super("");
	}
	
	@Override
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}
	
}
