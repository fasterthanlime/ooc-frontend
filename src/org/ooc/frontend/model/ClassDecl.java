package org.ooc.frontend.model;

import java.io.IOException;
import java.util.Stack;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.interfaces.MustBeResolved;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.middle.OocCompilationError;
import org.ooc.middle.hobgoblins.Resolver;

public class ClassDecl extends TypeDecl implements Scope, MustBeResolved {

	protected boolean isAbstract;
	
	protected OocDocComment comment;
	protected String superName;
	protected ClassDecl superRef;

	protected FunctionDecl initialize;
	protected FunctionDecl load;
	
	public ClassDecl(String name, String superName, boolean isAbstract, Token startToken) {
		super(name, startToken);
		this.superName = (superName.isEmpty() && !isObjectClass()) ? "Object" : superName;
		this.isAbstract = isAbstract;
		this.initialize = new FunctionDecl("initialize", "", false, false, false, false, startToken);
		this.initialize.getArguments().add(new RegularArgument(instanceType, "this", startToken));
		this.initialize.setTypeDecl(this);
		this.load = new FunctionDecl("load", "", false, false, false, false, startToken);
		this.load.setStatic(true);
		this.load.setTypeDecl(this);
		this.superRef = null;
	}

	public boolean isObjectClass() {
		return name.equals("Object");
	}
	
	public boolean isClassClass() {
		return name.equals("Class");
	}
	
	public boolean isRootClass() {
		return isObjectClass() || isClassClass();
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
	
	public FunctionDecl getInitializeFunc() {
		return initialize;
	}
	
	public FunctionDecl getLoadFunc() {
		return load;
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
		NodeList<FunctionDecl> allFuncs = new NodeList<FunctionDecl>(startToken);
		getFunctionsRecursive(allFuncs);
		return allFuncs;
	}
	
	protected void getFunctionsRecursive(NodeList<FunctionDecl> allFuncs) {
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
		return getInstanceType();
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
		initialize.accept(visitor);
		load.accept(visitor);
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

	@Override
	public boolean hasVariable(String name) {
		for(VariableDecl variable: variables) {
			if(variable.getName().equals(name)) return true;
		}
		
		return false;
	}

	@Override
	public boolean isResolved() {
		return superName.isEmpty() || superRef != null;
	}

	@Override
	public boolean resolve(Stack<Node> stack, Resolver res, boolean fatal)
			throws IOException {
		
		if(isResolved()) return false;
		
		for(TypeDecl candidate: res.types) {
			if(superName.equals(candidate.getName())) {
				if(!(candidate instanceof ClassDecl)) {
					throw new OocCompilationError(this, stack, "Trying to extends a "
							+candidate.getClass().getSimpleName()+". You can only extend classes.");
				}
				superRef = (ClassDecl) candidate;
				return true;
			}
		}
		
		if(superRef == null && fatal) {
			throw new OocCompilationError(this, stack, "Couldn't resolve super-class "
					+superName+" of class "+name);
		}
		
		return superRef == null;
		
	}
	
}
