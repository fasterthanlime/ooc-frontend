package org.ooc.frontend.model;

import java.io.IOException;
import java.util.Stack;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.interfaces.MustBeResolved;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.middle.hobgoblins.Resolver;

public class ArrayLiteral extends Literal implements MustBeResolved {

	protected Type type = new Type("Pointer", Token.defaultToken);
	protected NodeList<Expression> elements;
	
	public ArrayLiteral(Token startToken) {
		super(startToken);
		elements = new NodeList<Expression>(startToken);
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

	@Override
	public boolean isResolved() {
		return false;
	}

	@Override
	public boolean resolve(Stack<Node> stack, Resolver res, boolean fatal)
			throws IOException {
		
		
		
		return false;
		
	}

}
