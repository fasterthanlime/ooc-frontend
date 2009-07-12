package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;

public class FunctionCall extends Access {

	protected String name;
	protected final NodeList<Expression> arguments;
	
	public FunctionCall(String name) {
		this.name = name;
		this.arguments = new NodeList<Expression>();
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public NodeList<Expression> getArguments() {
		return arguments;
	}

	@Override
	public Type getType() {
		throw new Error("FunctionCall not yet resolves the type");
	}
	
	@Override
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}
	
	@Override
	public void acceptChildren(Visitor visitor) throws IOException {
		for(Expression expression: arguments) {
			expression.accept(visitor);
		}
	}
	
}
