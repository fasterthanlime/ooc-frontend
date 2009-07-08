package org.ooc.frontend.model;

public class FunctionCall extends Expression {

	private String name;
	private final NodeList<Expression> arguments;
	
	public FunctionCall(String name) {
		
		this.name = name;
		this.arguments = new NodeList<Expression>();
		
	}
	
	public String getName() {
		
		return name;
		
	}
	
	public NodeList<Expression> getArguments() {
		
		return arguments;
		
	}

	@Override
	public Type getType() {

		throw new Error("FunctionCall not yet resolves the type");
		
	}
	
}
