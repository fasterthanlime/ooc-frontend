package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;

public class CoverDecl extends TypeDecl {

	private OocDocComment comment;
	private Type type;
	private Type fromType;
	
	public CoverDecl(String name, Type fromType) {
		super(name);
		this.fromType = fromType;
		this.type = new Type(name);
		this.type.setRef(this);
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
		return functions;
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
		this.functions.addAll(node.functions);
	}

}
