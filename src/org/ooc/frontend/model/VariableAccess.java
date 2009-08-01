package org.ooc.frontend.model;

import java.io.IOException;
import java.util.Stack;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.interfaces.MustResolveAccess;
import org.ooc.middle.structs.MultiMap;
import org.ooc.middle.walkers.Miner;
import org.ooc.middle.walkers.Opportunist;

public class VariableAccess extends Access implements MustResolveAccess {

	protected String variable;
	protected Declaration ref;
	
	public VariableAccess(String variable) {
		super();
		this.variable = variable;
	}

	public String getName() {
		return variable;
	}
	
	public Declaration getRef() {
		return ref;
	}
	
	public void setRef(Declaration ref) {
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
			ref = (Declaration) kiddo;
			return true;
		}
		return false;
	}

	@Override
	public boolean isResolved() {
		return ref != null;
	}

	@Override
	public boolean resolveAccess(Stack<Node> stack,
			final MultiMap<Node, VariableDecl> vars,
			final MultiMap<Node, FunctionDecl> funcs) throws IOException {

		Miner.mine(Scope.class, new Opportunist<Scope>() {
			public boolean take(Scope node, Stack<Node> stack) throws IOException {
				
				for(VariableDecl decl: vars.get((Node) node)) {
					if(decl.getName().equals(variable)) {
						System.out.println("Got it! Returning false.");
						setRef(decl);
						return false;
					}
				}
				System.out.println("Didn't get it, returning true..");
				return true;
			}
		}, stack);
		
		return ref == null;
		
	}

}
