package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;

public class CoverDecl extends Declaration {

	private OocDocComment comment;
	private Type type;
	private String name;
	
	private NodeList<VariableDecl> variables;
	private NodeList<FunctionDecl> functions;
	
	public CoverDecl(String name, Type type) {
		this.name = name;
		this.type = type;
		this.variables = new NodeList<VariableDecl>();
		this.functions = new NodeList<FunctionDecl>();
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(Type type) {
		this.type = type;
	}

	@Override
	public Type getType() {
		return type;
	}
	
	public OocDocComment getComment() {
		return comment;
	}
	
	public void setComment(OocDocComment comment) {
		this.comment = comment;
	}
	
	public NodeList<VariableDecl> getVariables() {
		return variables;
	}
	
	public NodeList<FunctionDecl> getFunctions() {
		return functions;
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
		
		for (VariableDecl variable : variables) {
			variable.accept(visitor);
		}
		for (FunctionDecl function: functions) {
			function.accept(visitor);
		}
		
	}

}
