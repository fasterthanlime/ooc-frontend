package org.ooc.frontend.model;

public class FunctionDecl extends Declaration {

	private String name;
	private final NodeList<Statement> body; 
	
	public FunctionDecl(String name) {
		
		this.name = name;
		this.body = new NodeList<Statement>();
		
	}
	
	public String getName() {
		
		return name;
		
	}
	
	public NodeList<Statement> getBody() {
		
		return body;
		
	}
	
}
