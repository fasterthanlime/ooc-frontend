package org.ooc.frontend.model;

import java.io.IOException;
import java.util.Stack;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.interfaces.MustResolveAccess;
import org.ooc.middle.structs.MultiMap;
import org.ooc.middle.walkers.Miner;
import org.ooc.middle.walkers.Opportunist;

public class FunctionCall extends Access implements MustResolveAccess {

	protected String name;
	protected String suffix;
	protected final NodeList<Expression> arguments;
	protected FunctionDecl impl;
	
	public FunctionCall(String name, String suffix) {
		this.name = name;
		this.suffix = suffix;
		this.arguments = new NodeList<Expression>();
		this.impl = null;
	}
	
	public void setImpl(FunctionDecl impl) {
		this.impl = impl;
	}
	
	public FunctionDecl getImpl() {
		return impl;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getSuffix() {
		return suffix;
	}
	
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
	
	public NodeList<Expression> getArguments() {
		return arguments;
	}

	@Override
	public Type getType() {
		if(impl != null) {
			return impl.getReturnType();
		}
		return null;
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
		arguments.accept(visitor);
	}
	
	@Override
	public boolean replace(Node oldie, Node kiddo) {
		if(oldie == impl) {
			impl = (FunctionDecl) kiddo;
			return true;
		}
		return false;
	}

	@Override
	public boolean isResolved() {
		return impl != null;
	}

	@Override
	public boolean resolveAccess(Stack<Node> stack,
			final MultiMap<Node, VariableDecl> vars,
			final MultiMap<Node, FunctionDecl> funcs) throws IOException {
		
		Miner.mine(Scope.class, new Opportunist<Scope>() {
			public boolean take(Scope node, Stack<Node> stack) throws IOException {
				
				for(FunctionDecl decl: funcs.get((Node) node)) {
					if(!decl.getName().equals(name)) continue;
					System.out.println("Same name, comparing suffixes!");
					if(!decl.getSuffix().isEmpty() && !suffix.isEmpty()
							&& !decl.getSuffix().equals(suffix)) continue;
					System.out.println("Same suffixes or no suffixes!");
					int numArgs = decl.getArguments().size();
					
					if(numArgs == arguments.size()
						|| ((decl.getArguments().get(numArgs - 1) instanceof VarArg)
						&& (decl.getArguments().size() - 1 <= arguments.size()))) {
						System.out.println("We have a match.");
						setImpl(decl);
						return false;
					}
					
					return false;
				}
				System.out.println("Didn't get it, returning true..");
				return true;
			}
		}, stack);
		
		return impl == null;
		
	}
	
}
