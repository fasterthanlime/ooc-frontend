package org.ooc.frontend.model;


public abstract class TypeDeclaration extends Declaration {

	protected NodeList<VariableDecl> variables;
	protected NodeList<FunctionDecl> functions;
	protected Type instanceType;
	
	public TypeDeclaration(String name) {
		super(name);
		this.variables = new NodeList<VariableDecl>();
		this.functions = new NodeList<FunctionDecl>();
		this.instanceType = new Type(name);
		instanceType.setRef(this);
	}
	
	public Type getInstanceType() {
		return instanceType;
	}
	
	public NodeList<VariableDecl> getVariables() {
		return variables;
	}
	
	public NodeList<FunctionDecl> getFunctions() {
		return functions;
	}
	
	public FunctionDecl getFunction(FunctionCall call) {
		for(FunctionDecl decl: functions) {
			if(call.matches(decl)) return decl;
		}
		
		return null;
	}

	public VariableDecl getVariable(String name) {
		for(VariableDecl decl: variables) {
			if(decl.getName().equals(name)) return decl;
		}
		
		return null;
	}
	
	public FunctionDecl getNoargFunction(String name) {
		for(FunctionDecl decl: functions) {
			if(name.matches(decl.getName()) && decl.getArguments().size() == 1) return decl;
		}
		
		return null;
	}


}
