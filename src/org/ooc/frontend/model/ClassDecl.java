package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;

public class ClassDecl extends TypeDecl implements Scope {

	private static Type type = new Type("Class");
	
	private boolean isAbstract;
	
	private OocDocComment comment;
	private String superName;
	private ClassDecl superRef;

	private FunctionDecl initializer;
	
	public ClassDecl(String name, boolean isAbstract) {
		super(name);
		this.isAbstract = isAbstract;
		this.superName = "";
		this.initializer = new FunctionDecl("initialize", "", false, false, false, false);
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
	public NodeList<FunctionDecl> getFunctionsRecursive() {
		NodeList<FunctionDecl> allFuncs = new NodeList<FunctionDecl>();
		getFunctionsRecursive(allFuncs);
		return allFuncs;
	}
	
	private void getFunctionsRecursive(NodeList<FunctionDecl> allFuncs) {
		for(FunctionDecl decl: functions) {
			boolean already = false;
			for(FunctionDecl decl2: allFuncs) {
				if(decl.sameProto(decl2)) {
					already = true;
					break;
				}
			}
			if(!already) {
				allFuncs.add(decl);
			}
		}
		if(superRef != null) superRef.getFunctionsRecursive(allFuncs);
	}

	@Override
	public Type getType() {
		return type;
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
