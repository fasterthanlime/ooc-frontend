package org.ooc.frontend.model;

import java.io.IOException;
import java.util.Stack;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.interfaces.MustBeUnwrapped;
import org.ubi.CompilationFailedError;

public class Assignment extends Expression implements MustBeUnwrapped {

	public static enum Mode {
		REGULAR,
		DECLARATION,
		ADD,
		SUB,
		DIV,
		MUL,
	}
	
	private Mode mode;
	private Access lvalue;
	private Expression rvalue;	
	
	public Assignment(Access lvalue, Expression rvalue) {
		this(Mode.REGULAR, lvalue, rvalue);
	}
	
	public Assignment(Mode mode, Access lvalue, Expression rvalue) {
		this.mode = mode;
		this.lvalue = lvalue;
		this.rvalue = rvalue;
	}
	
	public Access getLvalue() {
		return lvalue;
	}
	
	public Expression getRvalue() {
		return rvalue;
	}
	
	public Mode getMode() {
		return mode;
	}

	@Override
	public Type getType() {
		return lvalue.getType();
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
		lvalue.accept(visitor);
		rvalue.accept(visitor);
	}

	@Override
	public boolean replace(Node oldie, Node kiddo) {
		
		if(oldie == lvalue) {
			lvalue = (Access) kiddo;
			return true;
		}
		
		if(oldie == rvalue) {
			rvalue = (Expression) kiddo;
			return true;
		}
		
		return false;
		
	}

	@Override
	public boolean unwrap(Stack<Node> stack) {
		
		if(mode == Mode.DECLARATION) {
			if(lvalue instanceof VariableAccess) {
				VariableAccess varAcc = (VariableAccess) lvalue;
				stack.peek().replace(this, new VariableDeclFromExpr(varAcc.getName(), rvalue));
			} else {
				throw new CompilationFailedError(null, "Decl-assign to a "
						+lvalue.getClass().getSimpleName()+" where it should be a VariableAccess");
			}
			return true;
		}
		
		return false;
		
	}

	public String getSymbol() {
		
		switch(mode) {
			case ADD:
				return "+=";
			case DECLARATION:
				return ":=";
			case DIV:
				return "/=";
			case MUL:
				return "*=";
			case SUB:
				return "-=";
			default:
				return "=";
		}
		
	}
	
}
