package org.ooc.frontend.model;

import java.io.IOException;
import java.util.Stack;

import org.ooc.frontend.Levenshtein;
import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.interfaces.MustBeResolved;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.middle.OocCompilationError;
import org.ooc.middle.hobgoblins.Resolver;
import org.ooc.middle.walkers.Miner;
import org.ooc.middle.walkers.Opportunist;

public class VariableAccess extends Access implements MustBeResolved {

	protected String name;
	protected Declaration ref;
	
	public VariableAccess(String variable, Token startToken) {
		super(startToken);
		this.name = variable;
	}

	public VariableAccess(VariableDecl varDecl, Token startToken) {
		super(startToken);
		assert(varDecl.atoms.size() == 1);
		this.name = varDecl.getName();
		ref = varDecl;
	}

	public String getName() {
		return name;
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
		
		if(ref == null) {
			Miner.mine(Scope.class, new Opportunist<Scope>() {
				public boolean take(Scope node, Stack<Node> stack) throws IOException {
					
					Iterable<VariableDecl> vars = res.vars.get((Node) node);
					for(VariableDecl var: vars) {
						if(var.hasAtom(name)) {
							if(var.isMember()) {
								MemberAccess membAcc =  new MemberAccess(name, startToken);
								membAcc.resolve(mainStack, res, fatal);
								membAcc.setRef(var);
								if(!mainStack.peek().replace(VariableAccess.this, membAcc)) {
									throw new Error("Couldn't replace a VariableAccess with a MemberAccess!");
								}
							}
							ref = var;
							return false;
						}
					}
					
					Iterable<FunctionDecl> funcs = res.funcs.get((Node) node);
					for(FunctionDecl func: funcs) {
						if(func.getName().equals(name)) {
							ref = func;
							return false;
						}
					}
					
					return true;
					
				}
			}, mainStack);
		}
		
		if(ref == null) {
			int typeIndex = Node.find(TypeDecl.class, mainStack);
			if(typeIndex != -1) {
				TypeDecl typeDecl = (TypeDecl) mainStack.get(typeIndex);
				if(name.equals("This")) {
					ref = typeDecl;
					return true;
				}
				VariableDecl varDecl = typeDecl.getVariable(name);
				if(varDecl != null) {
					MemberAccess membAccess = new MemberAccess(name, startToken);
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
				if(decl.getName().equals(name)) {
					ref = decl;
				}
			}
		}

		if(fatal && ref == null) {
			String message = "Couldn't resolve access to variable "+name;
			String guess = guessCorrectName(mainStack, res);
			if(guess != null) {
				message += " Did you mean "+guess+" ?";
			}
			throw new OocCompilationError(this, mainStack, message);
		}
		
		return ref == null;
		
	}

	private String guessCorrectName(Stack<Node> mainStack, Resolver res) {
		
		int bestDistance = Integer.MAX_VALUE;
		String bestMatch = null;
		
		for(int i = mainStack.size() - 1; i >= 0; i--) {
			if(!(mainStack.get(i) instanceof Scope)) continue;
			
			for(VariableDecl decl: res.vars.get(mainStack.get(i))) {
				int distance = Levenshtein.distance(name, decl.getName());
				if(distance < bestDistance) {
					bestDistance = distance;
					bestMatch = decl.getName();
				}
			}
		}
		
		return bestMatch;
		
	}

	@Override
	public String toString() {
		return getClass().getSimpleName()+" : "+name;
	}

}
