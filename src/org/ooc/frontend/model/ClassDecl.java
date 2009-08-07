package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.FunctionDecl.FunctionDeclType;

public class ClassDecl extends TypeDeclaration implements Scope {

	private static Type type = new Type("Class");
	private Type instanceType;
	
	private boolean isAbstract;
	
	private OocDocComment comment;
	private String superName;
	private ClassDecl superRef;

	private FunctionDecl initializer;
	
	public ClassDecl(String name, boolean isAbstract) {
		super(name);
		this.isAbstract = isAbstract;
		this.superName = "";
		this.instanceType = new Type(name);
		instanceType.setRef(this);
		this.initializer = new FunctionDecl(FunctionDeclType.FUNC, "initialize", "", false, false, false, false);
		this.initializer.getArguments().add(new RegularArgument(instanceType, "this"));
		this.superRef = null;
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
	
	public FunctionDecl getInitializer() {
		return initializer;
	}
	
	public boolean isAbstract() {
		return isAbstract;
	}
	
	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}
	
	public ClassDecl getSuperRef() {
		return superRef;
	}
	
	public void setSuperRef(ClassDecl superRef) {
		this.superRef = superRef;
	}

	@Override
	public Type getType() {
		return type;
	}
	
	@Override
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
		initializer.accept(visitor);
		instanceType.accept(visitor);
	}
	
	@Override
	public boolean replace(Node oldie, Node kiddo) {
		return false;
	}
	
	@Override
	public FunctionDecl getFunction(FunctionCall call) {
		FunctionDecl function = super.getFunction(call);
		if(function != null) return function;
		if(superRef != null) return superRef.getFunction(call);
		return null;
	}
	
	@Override
	public VariableDecl getVariable(String name) {
		VariableDecl variable = super.getVariable(name);
		if(variable != null) return variable;
		if(superRef != null) return superRef.getVariable(name);
		return null;
	}
	
}
