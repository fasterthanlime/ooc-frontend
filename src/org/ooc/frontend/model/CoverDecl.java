package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;

public class CoverDecl extends TypeDeclaration {

	private OocDocComment comment;
	private Type fromType;
	
	public CoverDecl(String name, Type fromType) {
		super(name);
		this.fromType = fromType;
	}

	@Override
	public Type getType() {
		if(fromType == null) {
			return new Type(name);
		}
		return fromType;
	}
	
	@Override
	public Type getInstanceType() {
		throw new UnsupportedOperationException();
	}
	
	public Type getFromType() {
		return fromType;
	}
	
	public OocDocComment getComment() {
		return comment;
	}
	
	public void setComment(OocDocComment comment) {
		this.comment = comment;
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
		variables.accept(visitor);
		functions.accept(visitor);
	}
	
	@Override
	public boolean replace(Node oldie, Node kiddo) {
		
		if(oldie == fromType) {
			fromType = (Type) kiddo;
			return true;
		}
		
		return false;
		
	}

}
