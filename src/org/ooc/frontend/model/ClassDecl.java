package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;

public class ClassDecl extends Declaration implements Scope {

	private static Type type = new Type("Class");
	private Type instanceType;
	
	private boolean isAbstract;
	
	private OocDocComment comment;
	private String superName;

	private NodeList<VariableDecl> variables;
	private NodeList<FunctionDecl> functions;
	
	public ClassDecl(String name, boolean isAbstract) {
		super(name);
		this.isAbstract = isAbstract;
		this.superName = "";
		this.variables = new NodeList<VariableDecl>();
		this.functions = new NodeList<FunctionDecl>();
		this.instanceType = new Type(name);
		instanceType.setRef(this);
	}
	
	public OocDocComment getComment() {
		return comment;
	}
	
	public void setComment(OocDocComment comment) {
		this.comment = comment;
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
		return type;
	}
	
	public Type getInstanceType() {
		return instanceType;
	}
	
	@Override
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}
	
	@Override
	public boolean hasChildren() {
		return true;
	}
	
	@Override
	public void acceptChildren(Visitor visitor) throws IOException {
		variables.accept(visitor);
		functions.accept(visitor);
		instanceType.accept(visitor);
	}
	
	@Override
	public boolean replace(Node oldie, Node kiddo) {
		return false;
	}
	
}
