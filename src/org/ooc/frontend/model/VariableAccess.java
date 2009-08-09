package org.ooc.frontend.model;

import java.io.IOException;
import java.util.Stack;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.interfaces.MustResolveAccess;
import org.ooc.middle.hobgoblins.ModularAccessResolver;
import org.ooc.middle.walkers.Miner;
import org.ooc.middle.walkers.Opportunist;
import org.ubi.CompilationFailedError;

public class VariableAccess extends Access implements MustResolveAccess {

	protected String variable;
	protected VariableDecl ref;
	
	public VariableAccess(String variable) {
		super();
		this.variable = variable;
	}

	public String getName() {
		return variable;
	}
	
	public VariableDecl getRef() {
		return ref;
	}
	
	public void setRef(VariableDecl ref) {
		this.ref = ref;
	}

	@Override
	public Type getType() {
		if(ref != null) {
			return ref.getType();
		}
		return null;
		/*
		throw new UnsupportedOperationException(this.getClass().getSimpleName()
				+" to "+variable+" has its type yet unresolved.");
		*/
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
	public void acceptChildren(Visitor visitor) throws IOException {}
	
	@Override
	public boolean replace(Node oldie, Node kiddo) {
		if(oldie == ref) {
			ref = (VariableDecl) kiddo;
			return true;
		}
		return false;
	}

	@Override
	public boolean isResolved() {
		return ref != null;
	}

	@Override
	public boolean resolveAccess(final Stack<Node> mainStack, final ModularAccessResolver res, final boolean fatal) throws IOException {

		Miner.mine(Scope.class, new Opportunist<Scope>() {
			public boolean take(Scope node, Stack<Node> stack) throws IOException {
				
				Iterable<VariableDecl> vars = res.vars.get((Node) node);
				for(VariableDecl decl: vars) {
					if(decl.hasAtom(variable)) {
						if(decl.isMember()) {
							VariableAccess thisAccess = new VariableAccess("this");
							thisAccess.resolveAccess(mainStack, res, fatal);
							MemberAccess membAcc =  new MemberAccess(thisAccess, variable);
							membAcc.setRef(decl);
							if(!mainStack.peek().replace(VariableAccess.this, membAcc)) {
								throw new Error("Couldn't replace a VariableAccess with a MemberAccess!");
							}
						}
						setRef(decl);
						return false;
					}
				}
				
				for(FunctionDecl decl: res.funcs.get((Node) node)) {
					if(decl.getName().equals(variable) && (decl.getArguments().isEmpty()
							|| (decl.getArguments().size() == 1
									&& decl.getArguments().getLast() instanceof VarArg))) {
						FunctionCall call = new FunctionCall(variable, "");
						call.setImpl(decl);
						if(!mainStack.peek().replace(VariableAccess.this, call)) {
							throw new Error("Couldn't replace a VariableAccess with a FunctionCall!");
						}
						return false;
					}
				}
				
				return true;
				
			}
		}, mainStack);
		
		if(ref == null) {
			int typeIndex = Node.find(TypeDecl.class, mainStack);
			if(typeIndex != -1) {
				TypeDecl typeDecl = (TypeDecl) mainStack.get(typeIndex);
				VariableDecl varDecl = typeDecl.getVariable(variable);
				if(varDecl != null) {
					VariableAccess thisAccess = new VariableAccess("this");
					MemberAccess membAccess = new MemberAccess(thisAccess, variable);
					membAccess.setRef(varDecl);
					if(!mainStack.peek().replace(this, membAccess)) {
						throw new Error("Couldn't replace a VariableAccess with a MemberAccess! Stack = "+mainStack);
					}
					return true;
				}
				FunctionDecl funcDecl = typeDecl.getNoargFunction(variable);
				if(funcDecl != null) {
					VariableAccess thisAccess = new VariableAccess("this");
					MemberCall membCall = new MemberCall(thisAccess, variable, "");
					membCall.setImpl(funcDecl);
					if(!mainStack.peek().replace(this, membCall)) {
						throw new Error("Couldn't replace a VariableAccess with a MemberCall! Stack = "+mainStack);
					}
					return true;
				}
			}
		}

		if(fatal && ref == null) {
			throw new CompilationFailedError(null, "Can't resolve variable access to '"+variable+"'. Stack = "+mainStack);
		}
		
		return ref == null;
		
	}

}
