package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;

public class FunctionDecl extends Declaration {

	public static enum FunctionDeclType {
		FUNC,
		IMPL,
		OVER,
		EXTERN,
	}
	
	private OocDocComment comment;
	
	private boolean isAbstract;

	private FunctionDeclType declType;
	private String name;
	private final NodeList<Line> body;
	private Type returnType;
	private final NodeList<Argument> arguments;
	
	public FunctionDecl(FunctionDeclType declType, String name, boolean isAbstract) {
		
		this.declType = declType;
		this.name = name;
		this.isAbstract = isAbstract;
		this.body = new NodeList<Line>();
		this.returnType = new Type("void");
		this.arguments = new NodeList<Argument>();
		
	}
	
	public void setComment(OocDocComment comment) {
		this.comment = comment;
	}
	
	public OocDocComment getComment() {
		return comment;
	}
	
	public FunctionDeclType getDeclType() {
		return declType;
	}
	
	public void setDeclType(FunctionDeclType declType) {
		this.declType = declType;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public boolean isAbstract() {
		return isAbstract;
	}
	
	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
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
	
	public NodeList<Argument> getArguments() {
		return arguments;
	}
	
	@Override
	public Type getType() {
		return new Type("Func");
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
		arguments.accept(visitor);
		returnType.accept(visitor);
		body.accept(visitor);
	}
	
}
