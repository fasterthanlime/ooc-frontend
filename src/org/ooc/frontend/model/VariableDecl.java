package org.ooc.frontend.model;

import java.io.IOException;
import java.util.Stack;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.interfaces.MustBeUnwrapped;

public class VariableDecl extends Declaration implements MustBeUnwrapped {

	protected boolean isConst;
	protected boolean isStatic;
	
	protected boolean isMember;
	
	protected Type type;
	
	public VariableDecl(Type type, String name, boolean isConst, boolean isStatic) {
		super(name);
		this.isConst = isConst;
		this.isStatic = isStatic;
		this.isMember = false;
		this.type = type;
	}
	
	public Type getType() {
		return type;
	}
	
	public void setType(Type type) {
		this.type = type;
	}
	
	public boolean isConst() {
		return isConst;
	}
	
	public void setConst(boolean isConst) {
		this.isConst = isConst;
	}
	
	public boolean isStatic() {
		return isStatic;
	}
	
	public void setStatic(boolean isStatic) {
		this.isStatic = isStatic;
	}
	
	public boolean isMember() {
		return isMember;
	}
	
	public void setMember(boolean isMember) {
		this.isMember = isMember;
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
	public void acceptChildren(Visitor visitor) throws IOException {
		type.accept(visitor);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean unwrap(Stack<Node> hierarchy) {

		// TODO wrap the two lines into a new Block
		
		if(hierarchy.peek() instanceof Line
		|| hierarchy.get(hierarchy.size() - 2) instanceof FunctionDecl
		|| hierarchy.get(hierarchy.size() - 2) instanceof ClassDecl)
			return false;
		
		int listIndex = find(NodeList.class, hierarchy);
		if(listIndex == -1) {
			throw new Error("Couldn't find list in which to replace VariableDecl expression. Stack = "+hierarchy);
		}
		NodeList<Node> list = (NodeList<Node>) hierarchy.get(listIndex);
		list.replace(this, new VariableAccess(name));
		System.out.println("Replaced variable decl with variable access! =)");
		
		int lineIndex = find(Line.class, hierarchy, listIndex - 1);
		if(lineIndex == -1) {
			throw new Error("Not in a line! How are we supposed to add one? Stack = "+hierarchy);
		}
		Line line = (Line) hierarchy.get(lineIndex);
		int bodyIndex = find(NodeList.class, hierarchy, lineIndex - 1);
		if(bodyIndex == -1) {
			throw new Error("Didn't find a nodelist containing the line! How are we suppoed to add one? Stack = "+hierarchy);
		}
		NodeList<Line> body = (NodeList<Line>) hierarchy.get(bodyIndex);
		body.addBefore(line, new Line(this));
		
		return true;
		
	}

	@Override
	public boolean replace(Node oldie, Node kiddo) {
		
		if(oldie == type) {
			type = (Type) kiddo;
			return true;
		}
		return false;
		
	}
	
}
