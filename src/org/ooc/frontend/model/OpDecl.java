package org.ooc.frontend.model;

import java.io.IOException;
import java.util.Iterator;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.tokens.Token;

public class OpDecl extends Declaration {

	public enum OpType {
		ADD,
		SUB,
		MUL,
		DIV,
		EQUALS,
		INDEXING,
		INDEXED_ASSIGN,
	}
	
	protected OpType opType;
	protected FunctionDecl func;
	
	public OpDecl(OpType opType, FunctionDecl func, Token startToken) {
		super("Operator "+opType, startToken);
		this.opType = opType;
		this.func = func;
		String name = "__OP_"+opType;
		Iterator<Argument> iter = func.getArguments().iterator();
		while(iter.hasNext()) {
			name += "_" + iter.next().getType().getMangledName();
		}
		func.setName(name);
	}

	@Override
	public boolean replace(Node oldie, Node kiddo) {
		if(oldie == func) {
			func = (FunctionDecl) kiddo;
			return true;
		}
		
		return false;
	}

	@Override
	public Type getType() {
		return new Type("Operator", Token.defaultToken);
	}
	
	public OpType getOpType() {
		return opType;
	}
	
	public FunctionDecl getFunc() {
		return func;
	}

	@Override
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}

	@Override
	public void acceptChildren(Visitor visitor) throws IOException {
		func.accept(visitor);
	}

	@Override
	public boolean hasChildren() {
		return true;
	}

	public String getOpString() {
		switch(opType) {
		case ADD:
			return "+";
		case DIV:
			return "/";
		case EQUALS:
			return "==";
		case INDEXED_ASSIGN:
			return "[]=";
		case INDEXING:
			return "[]";
		case MUL:
			return "*";
		case SUB:
			return "-";
		default:
			return "?operator?"; // bah
		}
	}
	
	@Override
	public String toString() {
		return "operator "+getOpString()+" "+func.getArgsRepr();
	}
	
	@Override
	public TypeDecl getTypeDecl() {
		throw new Error("getting type decl of an "+getClass().getSimpleName()+", wtf?");
	}

}
