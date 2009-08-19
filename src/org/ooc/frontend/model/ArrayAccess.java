package org.ooc.frontend.model;

import java.io.IOException;
import java.util.Stack;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.OpDecl.OpType;
import org.ooc.frontend.model.interfaces.MustBeResolved;
import org.ooc.middle.hobgoblins.Resolver;
import org.ubi.CompilationFailedError;

public class ArrayAccess extends Access implements MustBeResolved {

	private Expression variable;
	private Expression index;

	public ArrayAccess(Expression variable, Expression index) {
		this.variable = variable;
		this.index = index;
	}
	
	public Expression getVariable() {
		return variable;
	}
	
	public void setVariable(Expression variable) {
		this.variable = variable;
	}

	public Expression getIndex() {
		return index;
	}
	
	public void setIndex(Expression index) {
		this.index = index;
	}
	
	@Override
	public Type getType() {
		throw new UnsupportedOperationException("ArrayAccess doesn't resolve types just yet ;)");
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
		variable.accept(visitor);
		index.accept(visitor);
	}
	
	@Override
	public boolean replace(Node oldie, Node kiddo) {
		
		if(oldie == variable) {
			variable = (Expression) kiddo;
			return true;
		}
		
		if(oldie == index) {
			index = (Expression) kiddo;
			return true;
		}
		
		return false;
		
	}

	@Override
	public boolean isResolved() {
		return false;
	}

	@Override
	public boolean resolve(Stack<Node> stack, Resolver res, boolean fatal)
			throws IOException {
		
		int assignIndex = Node.find(Assignment.class, stack);
		
		for(OpDecl op: res.ops) {
			if(assignIndex == -1) {
				if(tryIndexing(op, stack)) break;
			} else {
				if(tryIndexedAssign(op, stack, assignIndex)) break;
			}
		}
		
		return false;
		
	}

	private boolean tryIndexedAssign(OpDecl op, Stack<Node> stack, int assignIndex) {
		
		if(op.getOpType() != OpType.INDEXED_ASSIGN) return false;
		
		Assignment ass = (Assignment) stack.get(assignIndex);
		if(ass.getLvalue() != this) {
			System.out.println(getClass().getSimpleName()+" not the lvalue, not replacing.");
			return false;
		}
		
		if(op.getFunc().getArguments().size() != 3) {
			throw new CompilationFailedError(null,
					"To overload the indexed assign operator, you need exactly three arguments, not "
					+op.getFunc().getArgsRepr());
		}
		NodeList<Argument> args = op.getFunc().getArguments();
		if(args.get(0).getType().equals(variable.getType())
				&& args.get(1).getType().equals(index.getType())) {
			FunctionCall call = new FunctionCall(op.getFunc());
			call.getArguments().add(variable);
			call.getArguments().add(index);
			call.getArguments().add(ass.getRvalue());
			if(!stack.get(assignIndex - 1).replace(ass, call)) {
				throw new CompilationFailedError(null, "Couldn't replace array-access-assign with a function call");
			}
			return true;
		}
		
		return false;
		
	}

	private boolean tryIndexing(OpDecl op, Stack<Node> stack) {
		
		if(op.getOpType() != OpType.INDEXING) return false;
		
		if(op.getFunc().getArguments().size() != 2) {
			throw new CompilationFailedError(null,
					"To overload the indexing operator, you need exactly two arguments, not "
					+op.getFunc().getArgsRepr());
		}
		NodeList<Argument> args = op.getFunc().getArguments();
		if(args.get(0).getType().equals(variable.getType())
				&& args.get(1).getType().equals(index.getType())) {
			FunctionCall call = new FunctionCall(op.getFunc());
			call.getArguments().add(variable);
			call.getArguments().add(index);
			stack.peek().replace(this, call);
			return true;
		}
		
		return false;
		
	}
	
}
