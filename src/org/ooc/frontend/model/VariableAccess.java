package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;

public class VariableAccess extends Access {

	private String variable;
	
	public VariableAccess(String variable) {
		super();
		this.variable = variable;
	}

	public String getVariable() {
		return variable;
	}

	@Override
	public Type getType() {
		throw new UnsupportedOperationException(this.getClass().getSimpleName()+" doesn't resolve the type yet.");
	}
	
	@Override
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}
	
	@Override
	public void acceptChildren(Visitor visitor) throws IOException {}

}
