package org.ooc.frontend.model;

public abstract class Argument extends VariableDecl {

	public Argument(Type type, String name, boolean isConst) {
		super(type, isConst, false);
		getAtoms().add(new VariableDeclAtom(name, null));
		this.name = name;
	}
	
	@Override
	public void setName(String name) {
		this.name = name;
		getAtoms().get(0).name = name;
	}
	
	@Override
	public String getName() {
		return getAtoms().get(0).name = name;
	}
	
}
