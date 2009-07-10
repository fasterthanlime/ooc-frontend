package org.ooc.frontend.model;

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
	
}
