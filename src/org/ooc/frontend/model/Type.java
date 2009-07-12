package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;

public class Type implements Visitable {

	private String name;
	private int pointerLevel;
	
	public Type(String name) {
		this(name, 0);
	}
	
	public Type(String name, int pointerLevel) {
		this.name = name;
		this.pointerLevel = pointerLevel;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public void setPointerLevel(int pointerLevel) {
		this.pointerLevel = pointerLevel;
	}

	public int getPointerLevel() {
		return pointerLevel;
	}

	@Override
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}

	@Override
	public void acceptChildren(Visitor visitor) throws IOException {}
	
}
