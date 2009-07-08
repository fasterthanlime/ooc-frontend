package org.ooc.frontend.model;

public class FunctionDecl extends Declaration {

	private String name;
	private final NodeList<Line> body; 
	
	public FunctionDecl(String name) {
		
		this.name = name;
		this.body = new NodeList<Line>();
		
	}
	
	public String getName() {
		
		return name;
		
	}
	
	public NodeList<Line> getBody() {
		
		return body;
		
	}
	
}
