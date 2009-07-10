package org.ooc.frontend.model;

public class ClassDecl extends Declaration {

	private String name;
	private String superName;

	private NodeList<VariableDecl> variables;
	private NodeList<FunctionDecl> functions;
	
	public ClassDecl(String name) {
		this.name = name;
		this.superName = "";
		this.variables = new NodeList<VariableDecl>();
		this.functions = new NodeList<FunctionDecl>();
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getSuperName() {
		return superName;
	}
	
	public void setSuperName(String superName) {
		this.superName = superName;
	}
	
	public NodeList<VariableDecl> getVariables() {
		return variables;
	}
	
	public NodeList<FunctionDecl> getFunctions() {
		return functions;
	}

	@Override
	public Type getType() {
		return new Type("class");
	}
	
}
