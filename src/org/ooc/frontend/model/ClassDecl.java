package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.FunctionDecl.FunctionDeclType;

public class ClassDecl extends Declaration implements Scope {

	private static Type type = new Type("Class");
	private Type instanceType;
	
	private boolean isAbstract;
	
	private OocDocComment comment;
	private String superName;

	private NodeList<VariableDecl> variables;
	private NodeList<FunctionDecl> functions;
	private FunctionDecl initializer;
	
	public ClassDecl(String name, boolean isAbstract) {
		super(name);
		this.isAbstract = isAbstract;
		this.superName = "";
		this.variables = new NodeList<VariableDecl>();
		this.functions = new NodeList<FunctionDecl>();
		this.instanceType = new Type(name);
		instanceType.setRef(this);
		this.initializer = new FunctionDecl(FunctionDeclType.FUNC, "initialize", "", false, false, false, false);
		this.initializer.getArguments().add(new RegularArgument(instanceType, "this"));
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
	
	public FunctionDecl getInitializer() {
		return initializer;
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
		initializer.accept(visitor);
		instanceType.accept(visitor);
	}
	
	@Override
	public boolean replace(Node oldie, Node kiddo) {
		return false;
	}
	
	public FunctionDecl getFunction(String name, String suffix) {
		
		for(FunctionDecl decl: functions) {
			if(decl.getName().equals(name)
				&& (decl.getSuffix().isEmpty() || suffix.isEmpty()
					|| decl.getSuffix().equals(suffix))) {
				return decl;
			}
		}
		return null;
		
	}

	public VariableDecl getVariable(String name) {
		
		for(VariableDecl decl: variables) {
			if(decl.getName().equals(name)) return decl;
		}
		return null;
		
	}
	
}
