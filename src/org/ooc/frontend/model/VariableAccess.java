package org.ooc.frontend.model;

import java.io.IOException;
import java.util.Stack;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.interfaces.MustResolveAccess;
import org.ooc.middle.MultiMap;

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
			MultiMap<Node, VariableDecl> vars,
			MultiMap<Node, FunctionDecl> funcs) {
		
		int index = stack.size() - 1;
		search: while(index >= 0) {
			index = Node.find(Scope.class, stack);
			if(index == -1) break search;

			Node scope = stack.get(index);
			for(VariableDecl decl: vars.get(scope)) {
				if(decl.getName().equals(variable)) {
					setRef(decl);
					break search;
				}
			}
		}
		
		return false;
	}

}
