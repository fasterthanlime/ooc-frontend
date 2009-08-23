package org.ooc.frontend.model;

import java.io.IOException;
import java.util.Iterator;
import java.util.Stack;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.interfaces.MustBeResolved;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.middle.OocCompilationError;
import org.ooc.middle.hobgoblins.Resolver;

public class ArrayLiteral extends Literal implements MustBeResolved {

	private static Type defaultType = new Type("Pointer", Token.defaultToken);
	protected Type type = defaultType;
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
		return type != defaultType;
	}

	@Override
	public boolean resolve(Stack<Node> stack, Resolver res, boolean fatal)
			throws IOException {
		
		if(!elements.isEmpty()) {
			Iterator<Expression> iter = elements.iterator();
			Type innerType = iter.next().getType();
			
			while(iter.hasNext()) {
				Expression element = iter.next();
				if(!element.getType().fitsIn(innerType)) {
					throw new OocCompilationError(element, stack, "Encountered a "
							+element.getType()+" in a "+innerType+"* array literal.");
				}
			}
			
			this.type = new Type(innerType.name, innerType.pointerLevel + 1, startToken);
			type.isArray = true;
			type.resolve(stack, res, fatal);
		}
		
		return type == defaultType;
		
	}

}
