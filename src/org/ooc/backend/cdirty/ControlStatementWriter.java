package org.ooc.backend.cdirty;

import java.io.IOException;

import org.ooc.frontend.model.*;

public class ControlStatementWriter {

	public static void writeIf(If if1, CGenerator cgen) throws IOException {
		cgen.current.app("if (");
		if1.getCondition().accept(cgen);
		cgen.current.app(")");
		NodeList<Line> body = if1.getBody();
		cgen.current.openBlock();
		body.accept(cgen);
		cgen.current.closeBlock();
	}
	
	public static void writeElse(Else else1, CGenerator cgen) throws IOException {
		cgen.current.app("else ");
		NodeList<Line> body = else1.getBody();
		if(body.size() == 1 && (body.get(0).getStatement() instanceof If)) {
			body.get(0).getStatement().accept(cgen);
		} else {
			cgen.current.openBlock();
			body.accept(cgen);
			cgen.current.closeBlock();
		}
	}

	public static void writeWhile(While while1, CGenerator cgen) throws IOException {
		cgen.current.app("while (");
		while1.getCondition().accept(cgen);
		cgen.current.app(")").openBlock();
		while1.getBody().accept(cgen);
		cgen.current.closeBlock();
	}
	
	public static void writeForeach(Foreach foreach, CGenerator cgen) throws IOException {
		if(foreach.getCollection() instanceof RangeLiteral) {
			RangeLiteral range = (RangeLiteral) foreach.getCollection();
			cgen.current.app("for (");
			foreach.getVariable().accept(cgen);
			cgen.current.app(" = ");
			range.getLower().accept(cgen);
			cgen.current.app("; ");
			foreach.getVariable().accept(cgen);
			cgen.current.app(" < ");
			range.getUpper().accept(cgen);
			cgen.current.app("; ");
			foreach.getVariable().accept(cgen);
			cgen.current.app("++").app(")").openBlock();
			foreach.getBody().accept(cgen);
			cgen.current.closeBlock();
		} else { 
			throw new UnsupportedOperationException("Iterating over.. not a Range but a "
					+foreach.getCollection().getType());
		}
	}
	
}
