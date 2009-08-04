package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;

public class FunctionDecl extends Declaration implements Scope {

	public static enum FunctionDeclType {
		FUNC,
		IMPL,
		OVER,
	}
	
	private OocDocComment comment;
	
	private boolean isFinal;
	private boolean isStatic;
	private boolean isAbstract;
	private boolean isExtern;
	
	private boolean isMember;

	private FunctionDeclType declType;
	private String suffix;
	private final NodeList<Line> body;
	private Type returnType;
	private final NodeList<Argument> arguments;
	
	public FunctionDecl(FunctionDeclType declType, String name, String suffix,
			boolean isFinal, boolean isStatic, boolean isAbstract, boolean isExtern) {
		
		super(name);
		this.declType = declType;
		this.suffix = suffix;
		this.isFinal = isFinal;
		this.isStatic = isStatic;
		this.isAbstract = isAbstract;
		this.isExtern = isExtern;
		this.isMember = false;
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
	
	public String getSuffix() {
		return suffix;
	}
	
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
	
	public boolean isAbstract() {
		return isAbstract;
	}
	
	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}
	
	public boolean isStatic() {
		return isStatic;
	}
	
	public void setStatic(boolean isStatic) {
		this.isStatic = isStatic;
	}
	
	public boolean isFinal() {
		return isFinal;
	}
	
	public void setFinal(boolean isFinal) {
		this.isFinal = isFinal;
	}
	
	public boolean isExtern() {
		return isExtern;
	}
	
	public void setExtern(boolean isExtern) {
		this.isExtern = isExtern;
	}
	
	public boolean isMember() {
		return isMember;
	}
	
	public void setMember(boolean isMember) {
		this.isMember = isMember;
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

	public boolean isConstructor() {
		return name.equals("new");
	}
	
	@Override
	public boolean replace(Node oldie, Node kiddo) {
		
		if(oldie == returnType) {
			returnType = (Type) kiddo;
			return true;
		}
		
		return false;
		
	}
	
}
