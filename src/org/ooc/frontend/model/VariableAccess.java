package org.ooc.frontend.model;

import java.io.IOException;
import java.util.Stack;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.interfaces.MustBeResolved;
import org.ooc.middle.hobgoblins.Resolver;
import org.ooc.middle.walkers.Miner;
import org.ooc.middle.walkers.Opportunist;
import org.ubi.CompilationFailedError;

public class VariableAccess extends Access implements MustBeResolved {

	protected String variable;
	protected Declaration ref;
	
	public VariableAccess(String variable) {
		this.variable = variable;
	}

	public VariableAccess(VariableDecl varDecl) {
		assert(varDecl.atoms.size() == 1);
		this.variable = varDecl.getName();
		ref = varDecl;
	}

	public String getName() {
		return variable;
	}
	
	public Declaration getRef() {
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
	public boolean resolve(final Stack<Node> mainStack, final Resolver res, final boolean fatal) throws IOException {

		Miner.mine(Scope.class, new Opportunist<Scope>() {
			public boolean take(Scope node, Stack<Node> stack) throws IOException {
				
				Iterable<VariableDecl> vars = res.vars.get((Node) node);
				for(VariableDecl decl: vars) {
					if(decl.hasAtom(variable)) {
						if(decl.isMember()) {
							VariableAccess thisAccess = new VariableAccess("this");
							thisAccess.resolve(mainStack, res, fatal);
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
				return true;
				
			}
		}, mainStack);
		
		if(ref == null) {
			int typeIndex = Node.find(TypeDecl.class, mainStack);
			if(typeIndex != -1) {
				TypeDecl typeDecl = (TypeDecl) mainStack.get(typeIndex);
				VariableDecl varDecl = typeDecl.getVariable(variable);
				if(varDecl != null) {
					MemberAccess membAccess = new MemberAccess(variable);
					membAccess.setRef(varDecl);
					if(!mainStack.peek().replace(this, membAccess)) {
						throw new Error("Couldn't replace a VariableAccess with a MemberAccess! Stack = "+mainStack);
					}
					return true;
				}
			}
		}
		
		if(ref == null) {
			for(TypeDecl decl: res.types) {
				if(decl.getName().equals(variable)) {
					ref = decl;
				}
			}
		}

		if(fatal && ref == null) {
			throw new CompilationFailedError(null, "Can't resolve variable access to '"
					+variable+"'. Stack = "+mainStack);
		}
		
		return ref == null;
		
	}

	@Override
	public String toString() {
		return getClass().getSimpleName()+" : "+variable;
	}

}
