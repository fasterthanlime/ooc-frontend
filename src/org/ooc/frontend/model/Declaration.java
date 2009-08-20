package org.ooc.frontend.model;

public abstract class Declaration extends Expression {

	protected String name;
	protected String externName;
	
	public Declaration(String name) {
		this(name, null);
	}
	
	public Declaration(String name, String externName) {
		this.name = name;
		this.externName = externName;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public abstract TypeDecl getTypeDecl();
	
	public boolean isExtern() {
		return externName != null;
	}
	
	public String getExternName() {
		if(externName == null || externName.isEmpty()) return getName();
		return externName;
	}
	
	public void setExternName(String externName) {
		this.externName = externName;
	}
	
	@Override
	public String toString() {
		return super.toString() + ": " + name;
	}
	
}
