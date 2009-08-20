package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;

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
public class CoverDecl extends TypeDecl {

	private OocDocComment comment;
	private Type type;
	private Type fromType;
	private CoverDecl base;
	
	public CoverDecl(String name, Type fromType) {
		super(name);
		this.fromType = fromType;
		if(fromType != null) {
			this.fromType.setRef(new BuiltinType(fromType));
		}
		this.type = new Type(name);
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
		if(fromType != null) { fromType.accept(visitor); }
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

}
