package org.ooc.frontend.model;

import java.io.IOException;
import java.util.Stack;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.interfaces.MustResolveAccess;
import org.ooc.middle.hobgoblins.ModularAccessResolver;
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
	public boolean resolveAccess(final Stack<Node> mainStack, final ModularAccessResolver res) throws IOException {
		
		Miner.mine(Scope.class, new Opportunist<Scope>() {
			public boolean take(Scope node, Stack<Node> stack) throws IOException {
				
				for(FunctionDecl decl: res.funcs.get((Node) node)) {
					if(matches(decl)) {
						impl = decl;
						if(decl.isMember()) {
							VariableAccess thisAccess = new VariableAccess("this");
							thisAccess.resolveAccess(mainStack, res);
							MemberCall memberCall = new MemberCall(thisAccess, FunctionCall.this);
							memberCall.setImpl(decl);
							mainStack.peek().replace(FunctionCall.this, memberCall);
						}
						return false;
					}
				}
				return true;
			}
		}, mainStack);
		
		return impl == null;
		
	}
	
	public boolean matches(FunctionDecl decl) {
		
		if(!decl.getName().equals(name)) return false;
		
		if(!decl.getSuffix().isEmpty() && !suffix.isEmpty()
				&& !decl.getSuffix().equals(suffix)) return false;
		
		int numArgs = decl.getArguments().size();
		if(decl.isMember()) numArgs--;
		
		if(numArgs == arguments.size()
			|| ((numArgs > 0 && decl.getArguments().getLast() instanceof VarArg)
			&& (numArgs - 1 <= arguments.size()))) {
			return true;
		}
		
		return false;
		
	}
	
}
