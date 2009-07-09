package org.ooc.frontend.model;

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

}
