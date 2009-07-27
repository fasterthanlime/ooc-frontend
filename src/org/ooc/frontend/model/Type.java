package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;

public class Type extends Node {

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
	public boolean hasChildren() {
		return false;
	}

	@Override
	public void acceptChildren(Visitor visitor) throws IOException {}
	
	@Override
	public String toString() {
		
		if(pointerLevel == 0) {
			return name;
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append(name);
		for(int i = 0; i < pointerLevel; i++) {
			sb.append('*');
		}
		return sb.toString();
		
	}

	public boolean isVoid() {
		return (name.equals("void") || name.equals("Void")) && (getPointerLevel() == 0);
	}

	public boolean isFlat() {
		return pointerLevel == 0;
	}
	
}
