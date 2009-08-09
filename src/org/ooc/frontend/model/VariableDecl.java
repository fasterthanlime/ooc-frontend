package org.ooc.frontend.model;

import java.io.IOException;
import java.util.Stack;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.interfaces.MustBeUnwrapped;

public class VariableDecl extends Declaration implements MustBeUnwrapped {

	public static class VariableDeclAtom extends Node {
		String name;
		Expression expression;
		
		public VariableDeclAtom(String name, Expression expression) {
			this.name = name;
			this.expression = expression;
		}

		@Override
		public boolean replace(Node oldie, Node kiddo) {
			if(oldie == expression) {
				expression = (Expression) kiddo;
				return true;
			}
			return false;
		}

		@Override
		public void accept(Visitor visitor) throws IOException {
			visitor.visit(this);
		}

		@Override
		public void acceptChildren(Visitor visitor) throws IOException {
			if(expression != null) expression.accept(visitor);
		}

		@Override
		public boolean hasChildren() {
			return expression != null;
		}
		
		public String getName() {
			return name;
		}
		
		public Expression getExpression() {
			return expression;
		}
		
		@Override
		public String toString() {
			return super.toString()+": "+name;
		}
	}
	
	protected boolean isConst;
	protected boolean isStatic;
	
	protected Type type;
	protected TypeDecl typeDecl;
	
	protected NodeList<VariableDeclAtom> atoms;
	
	public VariableDecl(Type type, boolean isConst, boolean isStatic) {
		super(null);
		this.type = type;
		this.isConst = isConst;
		this.isStatic = isStatic;
		this.atoms = new NodeList<VariableDeclAtom>();
	}
	
	@Override
	public String getName() {
		if(atoms.size() == 1) return atoms.get(0).name;
		throw new UnsupportedOperationException("Can't getName on a VariableDeclaration with multiple variables "+atoms);
	}
	
	public boolean hasAtom(String name) {
		for(VariableDeclAtom atom: atoms) {
			if(atom.name.equals(name)) return true;
		}
		return false;
	}
	
	public NodeList<VariableDeclAtom> getAtoms() {
		return atoms;
	}
	
	@Override
	public void setName(String name) {
		throw new UnsupportedOperationException("Can't setName on a VariableDeclaration, because it has several atoms, so we don't know which one to adjust: e.g. it doesn't make sense.");
	}
	
	public Type getType() {
		return type;
	}
	
	public void setType(Type type) {
		this.type = type;
	}
	
	public TypeDecl getTypeDecl() {
		return typeDecl;
	}
	
	public void setTypeDecl(TypeDecl typeDecl) {
		this.typeDecl = typeDecl;
	}
	
	public boolean isMember() {
		return typeDecl != null;
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
		atoms.accept(visitor);
	}

	@Override
	public boolean unwrap(Stack<Node> hierarchy) {

		System.out.println("Should unwrap variable decls "+atoms);
		
		int index = Node.find(ClassDecl.class, hierarchy);
		if(index != -1) {
			unwrapToClassInitializers(hierarchy, (ClassDecl) hierarchy.get(index));
			return false;
		}
		
		return unwrapToVarAcc(hierarchy);
		
	}

	@SuppressWarnings("unchecked")
	private boolean unwrapToVarAcc(Stack<Node> hierarchy) throws Error {

		if(hierarchy.peek() instanceof Line
		|| hierarchy.peek() instanceof ControlStatement
		|| hierarchy.get(hierarchy.size() - 2) instanceof FunctionDecl
		|| hierarchy.get(hierarchy.size() - 2) instanceof TypeDecl
		) {
			return false;
		}
		
		hierarchy.peek().replace(this, new VariableAccess(name));
		
		int lineIndex = find(Line.class, hierarchy);
		if(lineIndex == -1) {
			throw new Error("Not in a line! How are we supposed to add one? Stack = "+hierarchy);
		}
		Line line = (Line) hierarchy.get(lineIndex);
		int bodyIndex = find(NodeList.class, hierarchy, lineIndex - 1);
		if(bodyIndex == -1) {
			throw new Error("Didn't find a nodelist containing the line! How are we suppoed to add one? Stack = "+hierarchy);
		}
		
		NodeList<Line> body = (NodeList<Line>) hierarchy.get(bodyIndex);
		Block block = new Block();
		block.getBody().add(line);
		block.getBody().add(new Line(this));
		body.replace(line, block);
		
		return true;
		
	}

	private void unwrapToClassInitializers(Stack<Node> hierarchy, ClassDecl classDecl) {		
		
		for(VariableDeclAtom atom: atoms) {

			if(atom.getExpression() == null) continue;
			classDecl.getInitializer().getBody().add(
				new Line(
					new Assignment(
						new MemberAccess(name), atom.expression
					)
				)
			);
			atom.expression = null;
		
		}
		
	}

	@Override
	public boolean replace(Node oldie, Node kiddo) {
		
		if(oldie == type) {
			type = (Type) kiddo;
			return true;
		}
		return false;
		
	}
	
	@Override
	public String toString() {
		return type+": "+atoms.toString();
	}
	
}
