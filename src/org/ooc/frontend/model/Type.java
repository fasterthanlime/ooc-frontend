package org.ooc.frontend.model;

import java.io.IOException;
import java.util.Stack;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.interfaces.MustBeResolved;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.middle.OocCompilationError;
import org.ooc.middle.hobgoblins.Resolver;

public class Type extends Node implements MustBeResolved {

	protected String name;
	protected int pointerLevel;
	protected int referenceLevel;
	protected Declaration ref;
	
	public Type(String name, Token startToken) {
		this(name, 0, startToken);
	}
	
	public Type(String name, int pointerLevel, Token startToken) {
		this(name, pointerLevel, 0, startToken);
	}
	
	public Type(String name, int pointerLevel, int referenceLevel, Token startToken) {
		super(startToken);
		this.name = name;
		this.pointerLevel = pointerLevel;
		this.referenceLevel = referenceLevel;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public void setPointerLevel(int pointerLevel) {
		this.pointerLevel = pointerLevel;
	}

	public int getPointerLevel() {
		return pointerLevel;
	}
	
	public void setReferenceLevel(int referenceLevel) {
		this.referenceLevel = referenceLevel;
	}
	
	public int getReferenceLevel() {
		return referenceLevel;
	}
	
	public Declaration getRef() {
		return ref;
	}
	
	public void setRef(Declaration ref) {
		this.ref = ref;
	}

	@Override
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}
	
	@Override
	public boolean hasChildren() {
		return false;
	}

	@Override
	public void acceptChildren(Visitor visitor) throws IOException {}
	
	@Override
	public String toString() {
		
		if(pointerLevel == 0) {
			return name;
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append(name);
		for(int i = 0; i < pointerLevel; i++) {
			sb.append('*');
		}
		for(int i = 0; i < referenceLevel; i++) {
			sb.append('@');
		}
		return sb.toString();
		
	}
	
	public String getMangledName() {
		
		if(pointerLevel == 0) {
			return name;
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append(name);
		for(int i = 0; i < pointerLevel + referenceLevel; i++) {
			sb.append("__star");
		}
		return sb.toString();
		
	}

	public boolean isVoid() {
		return (name.equals("void") || name.equals("Void")) && (getPointerLevel() == 0);
	}

	public boolean isFlat() {
		return pointerLevel == 0 && !(ref instanceof ClassDecl);
	}
	
	@Override
	public boolean replace(Node oldie, Node kiddo) {
		if(oldie == ref) {
			ref = (Declaration) kiddo;
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Type) {
			Type type = (Type) obj;
			return name.equals(type.name) && pointerLevel == type.getPointerLevel();
		}
		return super.equals(obj);
	}
	
	public boolean resolve(Stack<Node> stack, Resolver res, boolean fatal) throws IOException {
		
		for(TypeDecl decl: res.types) {
			if(decl.getName().equals(name)) {
				ref = decl;
				break;
			}
		}

		if(ref == null && name.equals("This")) {
			int index = Node.find(TypeDecl.class, stack);
			if(index == -1) {
				throw new OocCompilationError(this, stack, "Using 'This' outside a type definition. Wtf?");
			}
			TypeDecl typeDecl = (TypeDecl) stack.get(index);
			name = typeDecl.getName();
			ref = typeDecl;
		}
		
		if(ref == null && fatal) {
			throw new OocCompilationError(this, stack, "Couldn't resolve type "+getName());
		}
		
		return ref == null;
		
	}
	
	@Override
	public boolean isResolved() {
		return ref != null;
	}
	
	public Type getGroundType() {
		if(ref instanceof CoverDecl) {
			CoverDecl coverDecl = (CoverDecl) ref;
			if(coverDecl.getFromType() != null) {
				Type rawType = coverDecl.getFromType().getGroundType();
				Type groundType = new Type(rawType.name, pointerLevel, referenceLevel, rawType.startToken);
				groundType.ref = ref;
				return groundType;
			}
		}
		return this;
	}
	
}
