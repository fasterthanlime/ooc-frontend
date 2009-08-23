package org.ooc.frontend.model;

import org.ooc.frontend.model.tokens.Token;


public abstract class TypeDecl extends Declaration {

	protected NodeList<VariableDecl> variables;
	protected NodeList<FunctionDecl> functions;
	protected Type instanceType;
	
	public TypeDecl(String name, Token startToken) {
		super(name, startToken);
		this.variables = new NodeList<VariableDecl>(startToken);
		this.functions = new NodeList<FunctionDecl>(startToken);
		this.instanceType = new Type(name, startToken);
		instanceType.setRef(this);
	}
	
	public Type getInstanceType() {
		return instanceType;
	}
	
	public boolean hasVariables() {
		return !variables.isEmpty();
	}
	
	public boolean hasFunctions() {
		return !functions.isEmpty();
	}
	
	public Iterable<VariableDecl> getVariables() {
		return variables;
	}
	
	public void addVariable(VariableDecl decl) {
		decl.setTypeDecl(this);
		
		variables.add(decl);
	}
	
	public Iterable<FunctionDecl> getFunctions() {
		return functions;
	}
	
	public void addFunction(FunctionDecl decl) {
		decl.setTypeDecl(this);
		
		if(!decl.isStatic()) {
			if(decl.isConstructor()) {
				decl.setFinal(true);
				decl.setReturnType(getInstanceType());
			}
			// FIXME this is ugly.
			if(!decl.isStatic() && (!decl.isConstructor() || !(this instanceof CoverDecl))) {
				Token tok = decl.getArguments().isEmpty() ? startToken : decl.getArguments().getFirst().startToken;
				decl.getArguments().add(0, new RegularArgument(getInstanceType(), "this",
						tok));
			}
		}
		functions.add(decl);
	}
	
	public abstract NodeList<FunctionDecl> getFunctionsRecursive();

	public FunctionDecl getFunction(FunctionCall call) {
		for(FunctionDecl decl: functions) {
			if(call.matches(decl)) return decl;
		}
		return null;
	}

	public VariableDecl getVariable(String name) {
		for(VariableDecl decl: variables) {
			if(decl.hasAtom(name)) return decl;
		}
		return null;
	}
	
	public FunctionDecl getNoargFunction(String name) {
		for(FunctionDecl decl: functions) {
			if(name.matches(decl.getName()) && decl.getArguments().size() == 1) return decl;
		}
		
		return null;
	}
	
	@Override
	public TypeDecl getTypeDecl() {
		return this;
	}

}
