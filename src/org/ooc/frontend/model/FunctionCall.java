package org.ooc.frontend.model;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

import org.ooc.frontend.Levenshtein;
import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.interfaces.MustResolveAccess;
import org.ooc.middle.hobgoblins.ModularAccessResolver;
import org.ooc.middle.walkers.Miner;
import org.ooc.middle.walkers.Opportunist;
import org.ubi.CompilationFailedError;

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
	public boolean resolveAccess(final Stack<Node> mainStack, final ModularAccessResolver res, final boolean fatal) throws IOException {

		if (name.equals("this")) resolveConstructorCall(mainStack, false);
		else if (name.equals("super")) resolveConstructorCall(mainStack, true);
		else resolveRegular(mainStack, res, fatal);
		
		if(impl == null) {
			if(fatal) {
				String message = "Couldn't resolve call to function "+name+getArgsRepr()+".";
				String guess = guessCorrectName(mainStack, res);
				if(guess != null) {
					message += " Did you mean "+guess+" ?";
				} else {
					System.out.println("No guess!");
				}
				throw new CompilationFailedError(null, message);
			}
			
			//if(!tryVarDecl(mainStack)) return false;
			return false;
		}
		
		return impl == null;
		
	}

	private String guessCorrectName(final Stack<Node> mainStack, final ModularAccessResolver res) {
		
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
		
		int classIndex = Node.find(ClassDecl.class, mainStack);
		if(classIndex == -1) {
			throw new CompilationFailedError(null, (isSuper ? "super" : "this")
					+getArgsRepr()+" call outside a class declaration, doesn't make sense.");
		}
		ClassDecl classDecl = (ClassDecl) mainStack.get(classIndex);
		if(isSuper) {
			if(classDecl.getSuperRef() == null) {
				throw new CompilationFailedError(null, "super"+getArgsRepr()+" call in class "
						+classDecl.getName()+" which has no super-class!");
			}
			classDecl = classDecl.getSuperRef();
		}
		
		for(FunctionDecl decl: classDecl.getFunctions()) {
			if(decl.getName().equals("new")) {
				if(matchesArgs(decl)) {
					impl = decl;
					return;
				}
			}
		}
		
	}
	
	private void resolveRegular(final Stack<Node> mainStack,
			final ModularAccessResolver res, final boolean fatal)
			throws IOException {
		
		Miner.mine(Scope.class, new Opportunist<Scope>() {
			public boolean take(Scope node, Stack<Node> stack) throws IOException {
				
				for(FunctionDecl decl: res.funcs.get((Node) node)) {
					if(matches(decl)) {
						impl = decl;
						if(decl.isMember()) {
							VariableAccess thisAccess = new VariableAccess("this");
							thisAccess.resolveAccess(mainStack, res, fatal);
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
		
		if(impl == null) {
			// Still null? Try top-level funcs in dependencies.
			SourceUnit root = (SourceUnit) mainStack.get(0);
			Set<SourceUnit> done = new HashSet<SourceUnit>();
			searchIn(root, done);
		}
		
	}
	
	private void searchIn(SourceUnit unit, Set<SourceUnit> done) {
		
		done.add(unit);
		for(Node node: unit.getBody()) {
			if(node instanceof FunctionDecl) {
				FunctionDecl decl = (FunctionDecl) node;
				if(matches(decl)) {
					System.out.println("Found match in "+unit.getName()+" for "+getName()+getArgsRepr());
					impl = decl;
					return;
				}
			}
		}
		
		for(Import imp: unit.getImports()) {
			if(!done.contains(imp.getUnit())) {
				searchIn(imp.getUnit(), done);
			}
		}
		
	}

	public boolean matches(FunctionDecl decl) {
		
		return matchesName(decl) && matchesArgs(decl);
		
	}

	public boolean matchesArgs(FunctionDecl decl) {
		
		int numArgs = decl.getArguments().size();
		if(decl.isMember()) numArgs--;
		
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
	
}
