package org.ooc.frontend.model;

import java.util.ArrayList;
import java.util.List;

public class FunctionDecl extends Declaration {

	public static enum FunctionDeclType {
		FUNC,
		IMPL,
		OVER,
	}
	
	private OocDocComment comment;
	
	private boolean isAbstract;

	private FunctionDeclType declType;
	private String name;
	private final NodeList<Line> body;
	private Type returnType;
	private List<Argument> arguments;
	
	public FunctionDecl(FunctionDeclType declType, String name, boolean isAbstract) {
		
		this.declType = declType;
		this.name = name;
		this.isAbstract = isAbstract;
		this.body = new NodeList<Line>();
		this.returnType = null;
		this.arguments = new ArrayList<Argument>();
		
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
	
	public List<Argument> getArguments() {
		return arguments;
	}
	
	@Override
	public Type getType() {
		return new Type("Func");
	}
	
}
