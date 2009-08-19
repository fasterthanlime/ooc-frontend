package org.ooc.frontend.model;


public abstract class TypeDecl extends Declaration {

	protected NodeList<VariableDecl> variables;
	protected NodeList<FunctionDecl> functions;
	protected Type instanceType;
	
	public TypeDecl(String name) {
		super(name);
		this.variables = new NodeList<VariableDecl>();
		this.functions = new NodeList<FunctionDecl>();
		this.instanceType = new Type(name);
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
			if(!decl.isConstructor() || !(this instanceof CoverDecl)) {
				decl.getArguments().add(0, new RegularArgument(getInstanceType(), "this"));
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
