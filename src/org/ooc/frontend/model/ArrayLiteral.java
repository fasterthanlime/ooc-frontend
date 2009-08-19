package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;

public class ArrayLiteral extends Literal {

	private Type type = new Type("Pointer");
	private NodeList<Expression> elements;
	
	public ArrayLiteral() {
		elements = new NodeList<Expression>();
	}
	
	@Override
	public boolean replace(Node oldie, Node kiddo) {
		if(oldie == type) {
			type = (Type) kiddo;
			return true;
		}
		
		return false;
	}

	@Override
	public Type getType() {
		return type;
	}
	
	public NodeList<Expression> getElements() {
		return elements;
	}

	@Override
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}

	@Override
	public void acceptChildren(Visitor visitor) throws IOException {
		type.accept(visitor);
		elements.accept(visitor);
	}

	@Override
	public boolean hasChildren() {
		return true;
	}

}
