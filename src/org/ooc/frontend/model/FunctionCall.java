package org.ooc.frontend.model;

import java.io.EOFException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.ooc.frontend.Levenshtein;
import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.interfaces.MustBeResolved;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.middle.OocCompilationError;
import org.ooc.middle.hobgoblins.Resolver;

public class FunctionCall extends Access implements MustBeResolved {

	protected String name;
	protected String suffix;
	protected final NodeList<Expression> arguments;
	protected FunctionDecl impl;
	
	public FunctionCall(String name, String suffix, Token startToken) {
		super(startToken);
		this.name = name;
		this.suffix = suffix;
		this.arguments = new NodeList<Expression>(startToken);
		this.impl = null;
	}
	
	public FunctionCall(FunctionDecl func, Token startToken) {
		this(func.getName(), func.getSuffix(), startToken);
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
	public boolean resolve(final NodeList<Node> mainStack, final Resolver res, final boolean fatal) throws IOException {

		if (name.equals("this")) resolveConstructorCall(mainStack, false);
		else if (name.equals("super")) resolveConstructorCall(mainStack, true);
		else resolveRegular(mainStack, res, fatal);
	
		if(impl != null) {
			List<TypeParam> params = impl.getTypeParams();
			if(!params.isEmpty()) {
				for(TypeParam param: params) {
					NodeList<Argument> implArgs = impl.getArguments();
					for(int i = 0; i < implArgs.size(); i++) {
						Argument arg = implArgs.get(i);
						if(!arg.getType().getName().equals(param.getName())) continue;
						Expression expr = arguments.get(i);
						if(expr instanceof VariableAccess) {
						} else {
							VariableDeclFromExpr vdfe = new VariableDeclFromExpr(
									generateTempName(param.getName()+"param", mainStack), expr, startToken);
							arguments.replace(expr, vdfe);
							NodeList<Node> stack = new NodeList<Node>();
							stack.addAll(mainStack);
							stack.add(arguments);
							vdfe.unwrapToVarAcc(stack);
						}
					}
				}
			}
		}
		
		if(impl == null && fatal) {
			String message = "Couldn't resolve call to function "+name+getArgsRepr()+".";
			String guess = guessCorrectName(mainStack, res);
			if(guess != null) {
				message += " Did you mean "+guess+" ?";
			}
			throw new OocCompilationError(this, mainStack, message);
		}
		
		return impl == null;
		
	}

	protected String guessCorrectName(final NodeList<Node> mainStack, final Resolver res) {
		
		int bestDistance = Integer.MAX_VALUE;
		String bestMatch = null;
		
		NodeList<FunctionDecl> funcs = new NodeList<FunctionDecl>();
		
		for(int i = mainStack.size() - 1; i >= 0; i--) {
			Node node = mainStack.get(i);
			if(!(node instanceof Scope)) continue;
			((Scope) node).getFunctions(funcs);
		}
		
		for(FunctionDecl decl: funcs) {
			int distance = Levenshtein.distance(name, decl.getName());
			if(distance < bestDistance) {
				bestDistance = distance;
				bestMatch = decl.getProtoRepr();
			}
		}
		
		Module module = (Module) mainStack.get(0);
		for(Import imp: module.getImports()) {
			for(Node node: imp.getModule().body) {
				if(node instanceof FunctionDecl) {
					FunctionDecl decl = (FunctionDecl) node;
					int distance = Levenshtein.distance(name, decl.getName());
					if(distance < bestDistance) {
						bestDistance = distance;
						bestMatch = decl.getProtoRepr();
					}
				}
			}
		}
		
		return bestMatch;
		
	}

	protected void resolveConstructorCall(final NodeList<Node> mainStack, final boolean isSuper) throws OocCompilationError, EOFException {
		
		int typeIndex = mainStack.find(TypeDecl.class);
		if(typeIndex == -1) {
			throw new OocCompilationError(this, mainStack, (isSuper ? "super" : "this")
					+getArgsRepr()+" call outside a class declaration, doesn't make sense.");
		}
		TypeDecl typeDecl = (TypeDecl) mainStack.get(typeIndex);
		if(isSuper) {
			if(!(typeDecl instanceof ClassDecl)) {
				throw new OocCompilationError(this, mainStack, "super"+getArgsRepr()+" call in type def "
						+typeDecl.getName()+" which is not a class! wtf?");
			}
			ClassDecl classDecl = ((ClassDecl) typeDecl);
			if(classDecl.getSuperRef() == null) {
				throw new OocCompilationError(this, mainStack, "super"+getArgsRepr()+" call in class "
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
	
	protected void resolveRegular(NodeList<Node> stack, Resolver res, boolean fatal) throws IOException {
		
		impl = getFunction(name, this, stack);

		if(impl == null) {
			Module module = (Module) stack.get(0);
			for(Import imp: module.getImports()) {
				searchIn(imp.getModule());
				if(impl != null) break;
			}
		}
		
		if(impl == null) {
			int typeIndex = stack.find(TypeDecl.class);
			if(typeIndex != -1) {
				TypeDecl typeDeclaration = (TypeDecl) stack.get(typeIndex);
				for(VariableDecl varDecl: typeDeclaration.getVariables()) {
					if(varDecl.getType() instanceof FuncType && varDecl.getName().equals(name)) {
						FuncType funcType = (FuncType) varDecl.getType();
						if(matchesArgs(funcType.getDecl())) {
							impl = funcType.getDecl();
							break;
						}
					}
				}
			}
		}
		
		if(impl.isMember() || impl.isFromPointer()) transformToMemberCall(stack, res);
		
	}

	private void transformToMemberCall(final NodeList<Node> stack,
			final Resolver res) throws IOException {
		VariableAccess thisAccess = new VariableAccess("this", startToken);
		thisAccess.resolve(stack, res, true);
		MemberCall memberCall = new MemberCall(new VariableAccess("this", startToken), this, startToken);
		memberCall.setImpl(impl);
		stack.peek().replace(this, memberCall);
	}
	
	protected void searchIn(Module module) {
		for(Node node: module.getBody()) {
			if(node instanceof FunctionDecl) {
				FunctionDecl decl = (FunctionDecl) node;
				if(matches(decl)) {
					impl = decl;
					return;
				}
			}
		}
	}

	public boolean matches(FunctionDecl decl) {
		
		return matchesName(decl) && matchesArgs(decl);
		
	}

	public boolean matchesArgs(FunctionDecl decl) {
		
		int numArgs = decl.getArguments().size();
		if(decl.isMember() && !decl.isConstructor() && !decl.isStatic()) numArgs--;
		
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
