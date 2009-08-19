package org.ooc.frontend.model;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

import org.ooc.frontend.Levenshtein;
import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.interfaces.MustBeResolved;
import org.ooc.middle.hobgoblins.Resolver;
import org.ooc.middle.walkers.Miner;
import org.ooc.middle.walkers.Opportunist;
import org.ubi.CompilationFailedError;

public class FunctionCall extends Access implements MustBeResolved {

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
	
	public FunctionCall(FunctionDecl func) {
		this(func.getName(), func.getSuffix());
		setImpl(func);
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
	public boolean resolve(final Stack<Node> mainStack, final Resolver res, final boolean fatal) throws IOException {

		if (name.equals("this")) resolveConstructorCall(mainStack, false);
		else if (name.equals("super")) resolveConstructorCall(mainStack, true);
		else resolveRegular(mainStack, res, fatal);
		
		if(impl == null) {
			if(fatal) {
				String message = "Couldn't resolve call to function "+name+getArgsRepr()+".";
				String guess = guessCorrectName(mainStack, res);
				if(guess != null) {
					message += " Did you mean "+guess+" ?";
				}
				throw new CompilationFailedError(null, message);
			}
		}
		
		return impl == null;
		
	}

	private String guessCorrectName(final Stack<Node> mainStack, final Resolver res) {
		
		int bestDistance = Integer.MAX_VALUE;
		String bestMatch = null;
		
		for(int i = mainStack.size() - 1; i >= 0; i--) {
			if(!(mainStack.get(i) instanceof Scope)) continue;
			
			for(FunctionDecl decl: res.funcs.get(mainStack.get(i))) {
				int distance = Levenshtein.distance(name, decl.getName());
				if(distance < bestDistance) {
					bestDistance = distance;
					bestMatch = decl.getProtoRepr();
				}
			}
		}
		
		return bestMatch;
		
	}

	private void resolveConstructorCall(final Stack<Node> mainStack, final boolean isSuper) {
		
		int typeIndex = Node.find(TypeDecl.class, mainStack);
		if(typeIndex == -1) {
			throw new CompilationFailedError(null, (isSuper ? "super" : "this")
					+getArgsRepr()+" call outside a class declaration, doesn't make sense.");
		}
		TypeDecl typeDecl = (TypeDecl) mainStack.get(typeIndex);
		if(isSuper) {
			if(!(typeDecl instanceof ClassDecl)) {
				throw new CompilationFailedError(null, "super"+getArgsRepr()+" call in type def "
						+typeDecl.getName()+" which is not a class! wtf?");
			}
			ClassDecl classDecl = ((ClassDecl) typeDecl);
			if(classDecl.getSuperRef() == null) {
				throw new CompilationFailedError(null, "super"+getArgsRepr()+" call in class "
						+typeDecl.getName()+" which has no super-class!");
			}
			typeDecl = classDecl.getSuperRef();
		}
		
		for(FunctionDecl decl: typeDecl.getFunctions()) {
			if(decl.isConstructor()) {
				if(matchesArgs(decl)) {
					impl = decl;
					return;
				}
			}
		}
		
	}
	
	private void resolveRegular(final Stack<Node> mainStack,
			final Resolver res, final boolean fatal)
			throws IOException {
		
		int index = Node.find(TypeDecl.class, mainStack);
		if(index != -1) {
			TypeDecl decl = (TypeDecl) mainStack.get(index);
			impl = decl.getFunction(this);
		}
		
		if(impl == null) {
			Miner.mine(Scope.class, new Opportunist<Scope>() {
				public boolean take(Scope node, Stack<Node> stack) throws IOException {
					
					for(FunctionDecl decl: res.funcs.get((Node) node)) {
						if(matches(decl)) {
							impl = decl;
							return false;
						}
					}
					return true;
				}
			}, mainStack);
		}
		
		if(impl == null) {
			// Still null? Try top-level funcs in dependencies.
			Module root = (Module) mainStack.get(0);
			Set<Module> done = new HashSet<Module>();
			searchIn(root, done);
		}
		
		if(impl != null) {
			if(impl.isMember()) {
				VariableAccess thisAccess = new VariableAccess("this");
				thisAccess.resolve(mainStack, res, fatal);
				MemberCall memberCall = new MemberCall(thisAccess, FunctionCall.this);
				memberCall.setImpl(impl);
				mainStack.peek().replace(FunctionCall.this, memberCall);
			}
		}
		
	}
	
	// FIXME must simplify
	private void searchIn(Module module, Set<Module> done) {
		done.add(module);
		for(Node node: module.getBody()) {
			if(node instanceof FunctionDecl) {
				FunctionDecl decl = (FunctionDecl) node;
				if(matches(decl)) {
					impl = decl;
					return;
				}
			}
		}
		
		for(Import imp: module.getImports()) {
			if(!done.contains(imp.getModule())) {
				searchIn(imp.getModule(), done);
			}
		}
	}

	public boolean matches(FunctionDecl decl) {
		
		return matchesName(decl) && matchesArgs(decl);
		
	}

	public boolean matchesArgs(FunctionDecl decl) {
		
		int numArgs = decl.getArguments().size();
		if(decl.isMember() && !decl.isConstructor()) numArgs--;
		
		if(numArgs == arguments.size()
			|| ((numArgs > 0 && decl.getArguments().getLast() instanceof VarArg)
			&& (numArgs - 1 <= arguments.size()))) {
			return true;
		}
		
		return false;
		
	}

	public boolean matchesName(FunctionDecl decl) {
		
		if(!decl.getName().equals(name)) return false;
		
		if(!decl.getSuffix().isEmpty() && !suffix.isEmpty()
				&& !decl.getSuffix().equals(suffix)) return false;
		
		return true;
		
	}
	
	public String getArgsRepr() {
		StringBuilder sB = new StringBuilder();
		sB.append('(');
		Iterator<Expression> iter = arguments.iterator();
		while(iter.hasNext()) {
			sB.append(iter.next().getType());
			if(iter.hasNext()) sB.append(", ");
		}
		sB.append(')');
		
		return sB.toString();
	}

	public boolean isConstructorCall() {
		return name.equals("this") || name.equals("super");
	}
	
	public String getProtoRepr() {
		return name+getArgsRepr();
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName()+": "+getProtoRepr();
	}
	
}
