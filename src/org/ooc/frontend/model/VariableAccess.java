package org.ooc.frontend.model;

import java.io.IOException;
import java.util.Stack;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.interfaces.MustResolveAccess;
import org.ooc.middle.hobgoblins.ModularAccessResolver;
import org.ooc.middle.walkers.Miner;
import org.ooc.middle.walkers.Opportunist;

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
		throw new UnsupportedOperationException(this.getClass().getSimpleName()
				+" to "+variable+" has its type yet unresolved.");
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
	public boolean resolveAccess(final Stack<Node> mainStack, final ModularAccessResolver res) throws IOException {

		System.out.println("In a "+getClass().getSimpleName());
		
		Miner.mine(Scope.class, new Opportunist<Scope>() {
			public boolean take(Scope node, Stack<Node> stack) throws IOException {
				
				for(VariableDecl decl: res.vars.get((Node) node)) {
					if(decl.getName().equals(variable)) {
						System.out.println("Got it! It's '"+variable+"'");
						System.out.println("Stack is "+stack);
						if(decl.isMember()) {
							System.out.println("Heck, it's a member variable!");
							VariableAccess thisAccess = new VariableAccess("this");
							thisAccess.resolveAccess(mainStack, res);
							MemberAccess membAcc =  new MemberAccess(thisAccess, variable);
							membAcc.setRef(decl);
							if(!mainStack.peek().replace(VariableAccess.this, membAcc)) {
								throw new Error("Couldn't replace a VariableAccess with a MemberAccess!");
							}
							System.out.println("Replaced it, returning false, it's alright.");
						}
						setRef(decl);
						return false;
					}
				}
				
				for(FunctionDecl decl: res.funcs.get((Node) node)) {
					if(decl.getName().equals(variable) && (decl.getArguments().isEmpty()
							|| (decl.getArguments().size() == 1
									&& decl.getArguments().getLast() instanceof VarArg))) {
						System.out.println("Found it's noarg function call instead, replacing..");
						FunctionCall call = new FunctionCall(variable, "");
						call.setImpl(decl);
						if(!mainStack.peek().replace(VariableAccess.this, call)) {
							throw new Error("Couldn't replace a VariableAccess with a FunctionCall!");
						}
						return false;
					}
				}
				
				System.out.println("Didn't get it, returning true..");
				return true;
				
			}
		}, mainStack);
		
		System.out.println("Solved? "+(ref != null));
		
		if(ref == null) {
			int classIndex = Node.find(ClassDecl.class, mainStack);
			if(classIndex != -1) {
				ClassDecl classDecl = (ClassDecl) mainStack.get(classIndex);
				FunctionDecl func = classDecl.getFunction(variable, "");
				if(func != null) {
					VariableAccess thisAccess = new VariableAccess("this");
					MemberCall membCall = new MemberCall(thisAccess, variable, "");
					membCall.setImpl(func);
					mainStack.peek().replace(this, membCall);
					return true;
				}
			}
		}
		
		return ref == null;
		
	}

}
