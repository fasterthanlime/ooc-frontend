package org.ooc.frontend.model;

public class FunctionCall extends Statement {

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
	
}
