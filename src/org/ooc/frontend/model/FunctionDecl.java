package org.ooc.frontend.model;

import java.util.ArrayList;
import java.util.List;

public class FunctionDecl extends Declaration {

	private String name;
	private final NodeList<Line> body;
	private Type returnType;
	private List<VariableDecl> arguments;
	
	public FunctionDecl(String name) {
		
		this.name = name;
		this.body = new NodeList<Line>();
		this.returnType = null;
		this.arguments = new ArrayList<VariableDecl>();
		
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public NodeList<Line> getBody() {
		return body;
	}
	
	public Type getReturnType() {
		return returnType;
	}
	
	public void setReturnType(Type returnType) {
		this.returnType = returnType;
	}
	
	public List<VariableDecl> getArguments() {
		return arguments;
	}
	
	@Override
	public Type getType() {
		return new Type("Func");
	}
	
}
