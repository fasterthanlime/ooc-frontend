package org.ooc.middle;

import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.Declaration;
import org.ooc.frontend.model.Node;
import org.ooc.frontend.model.Type;

public class BuiltinType extends Declaration {

	private Type type;
	
	public BuiltinType(String name) {
		super(name);
		type = new Type(name);
		type.setRef(this);
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

}
