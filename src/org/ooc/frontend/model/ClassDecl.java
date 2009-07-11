package org.ooc.frontend.model;

public class ClassDecl extends Declaration {

	private boolean isAbstract;
	
	private OocDocComment comment;
	private String name;
	private String superName;

	private NodeList<VariableDecl> variables;
	private NodeList<FunctionDecl> functions;
	
	public ClassDecl(String name, boolean isAbstract) {
		this.name = name;
		this.isAbstract = isAbstract;
		this.superName = "";
		this.variables = new NodeList<VariableDecl>();
		this.functions = new NodeList<FunctionDecl>();
	}
	
	public OocDocComment getComment() {
		return comment;
	}
	
	public void setComment(OocDocComment comment) {
		this.comment = comment;
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
	
	public boolean isAbstract() {
		return isAbstract;
	}
	
	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}

	@Override
	public Type getType() {
		return new Type("class");
	}
	
}
