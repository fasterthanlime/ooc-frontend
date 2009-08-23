package org.ooc.frontend.model;

import java.io.IOException;
import java.util.Stack;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.interfaces.MustBeResolved;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.middle.hobgoblins.Resolver;

/**
 * Covers can be defined several times, allowing to add functions, e.g.
 * you can add functions to the String cover from ooclib.ooc by redefining
 * a cover named String and adding your own functions.
 * 
 * The compiler handles it like this: it marks the 'redefined' cover as
 * an 'addon' of the original cover. Thus, its struct/typedef are not outputted,
 * only its functions. And, the addon 'absorbs' the original's functions, so
 * that everything is resolved properly.
 * 
 * @author Amos Wenger
 */
public class CoverDecl extends TypeDecl implements MustBeResolved {

	protected OocDocComment comment;
	protected Type type;
	protected Type fromType;
	protected CoverDecl base;
	
	public CoverDecl(String name, Type fromType, Token startToken) {
		super(name, startToken);
		this.fromType = fromType;
		this.type = new Type(name, startToken);
		this.type.setRef(this);
		this.base = null;
	}

	@Override
	public Type getType() {
		assert (type.getName().equals(name));
		return type;
	}
	
	public Type getFromType() {
		return fromType;
	}
	
	@Override
	public NodeList<FunctionDecl> getFunctionsRecursive() {
		System.out.println("Should get functions recursively from "+name+", got "+functions);
		return functions;
	}
	
	@Override
	public FunctionDecl getFunction(FunctionCall call) {
		for(FunctionDecl decl: functions) {
			if(call.matches(decl)) return decl;
		}
		
		if(base != null) return base.getFunction(call);
		return null;
	}
	
	public OocDocComment getComment() {
		return comment;
	}
	
	public void setComment(OocDocComment comment) {
		this.comment = comment;
	}
	
	public boolean isAddon() {
		return base != null;
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
		if(fromType != null) fromType.accept(visitor);
		type.accept(visitor);
		variables.accept(visitor);
		functions.accept(visitor);
	}
	
	@Override
	public boolean replace(Node oldie, Node kiddo) {
		if(oldie == type) {
			type = (Type) kiddo;
			return true;
		}
		
		if(oldie == fromType) {
			fromType = (Type) kiddo;
			return true;
		}
		
		return false;
	}

	public void absorb(CoverDecl node) {
		assert(variables.isEmpty());
		base = node;
	}

	@Override
	public boolean isResolved() {
		return (fromType == null || fromType.getRef() != null);
	}

	/**
	 * There's a trick about CoverDecl.
	 * If the fromType is defined somewhere (e.g. if it's another cover),
	 * then it must be ref'd correctly.
	 * If it's not, then a {@link BuiltinType} must be created
	 * so that it's considered 'resolved' (e.g. it's somewhere in C)
	 */
	@Override
	public boolean resolve(Stack<Node> stack, Resolver res, boolean fatal)
			throws IOException {

		if(fromType == null) return false;

		for(TypeDecl decl: res.types) {
			if(decl.getName().equals(fromType.getName())) {
				fromType.setRef(decl);
			}
		}
		
		if(fromType.getRef() == null) {
			fromType.setRef(new BuiltinType(fromType));
		}
		
		return fromType.getRef() == null;
		
	}

}
