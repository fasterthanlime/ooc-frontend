package org.ooc.frontend.model;

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
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
	
	@Override
	public void acceptChildren(Visitor visitor) {}

}
