package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;

public class VariableAccess extends Access {

	protected String variable;
	protected Declaration ref;
	
	public VariableAccess(String variable) {
		super();
		this.variable = variable;
	}

	public String getName() {
		return variable;
	}
	
	public Declaration getRef() {
		return ref;
	}
	
	public void setRef(Declaration ref) {
		this.ref = ref;
	}

	@Override
	public Type getType() {
		if(ref != null) {
			return ref.getType();
		}
		throw new UnsupportedOperationException(this.getClass().getSimpleName()+" doesn't resolve the type yet.");
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
	public void acceptChildren(Visitor visitor) throws IOException {}

}
