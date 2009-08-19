package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;

public class BuiltinType extends TypeDecl {

	private Type type;
	static NodeList<FunctionDecl> EMPTY = new NodeList<FunctionDecl>();
	
	public BuiltinType(String name) {
		super(name);
		type = new Type(name);
		type.setRef(this);
	}

	public BuiltinType(Type fromType) {
		this(fromType.getName());
	}

	@Override
	public Type getType() {
		return type ;
	}

	@Override
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}

	@Override
	public void acceptChildren(Visitor visitor) throws IOException {}

	@Override
	public boolean hasChildren() {
		return false;
	}
	
	@Override
	public boolean replace(Node oldie, Node kiddo) {
		return false;
	}

	@Override
	public NodeList<FunctionDecl> getFunctionsRecursive() {
		return EMPTY;
	}

}
