package org.ooc.backend.ooc;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import org.ooc.backend.Generator;
import org.ooc.backend.TabbedWriter;
import org.ooc.frontend.model.Assignment;
import org.ooc.frontend.model.Expression;
import org.ooc.frontend.model.FunctionCall;
import org.ooc.frontend.model.FunctionDecl;
import org.ooc.frontend.model.Line;
import org.ooc.frontend.model.Node;
import org.ooc.frontend.model.NumberLiteral;
import org.ooc.frontend.model.SourceUnit;
import org.ooc.frontend.model.StringLiteral;
import org.ooc.frontend.model.Type;
import org.ooc.frontend.model.VariableAccess;
import org.ooc.frontend.model.VariableDecl;
import org.ooc.frontend.model.VariableDeclAssigned;
import org.ubi.SourceReader;

public class OocGenerator implements Generator {

	@Override
	public void generate(File outPath, SourceUnit unit) throws IOException {

		String fileName = unit.getFileName();
		FileWriter w = new FileWriter(new File(outPath, fileName + ".gen"));
		sourceUnit(unit, new TabbedWriter(w));
		w.close();
		
	}

	private void sourceUnit(SourceUnit unit, TabbedWriter w) throws IOException {

		Iterator<Node> nodes = unit.getBody().iterator();
		while(nodes.hasNext()) {
			node(nodes.next(), w);
		}
		
	}
	
	private void node(Node node, TabbedWriter w) throws IOException {
		
		if(node instanceof Line) {
			line((Line) node, w);
		} else if(node instanceof FunctionDecl) {
			functionDecl((FunctionDecl) node, w);
		} else if(node instanceof FunctionCall) {
			functionCall((FunctionCall) node, w);
		} else if(node instanceof StringLiteral) {
			stringLiteral((StringLiteral) node, w);
		} else if(node instanceof VariableAccess) {
			variableAccess((VariableAccess) node, w);
		} else if(node instanceof VariableDecl) {
			variableDecl((VariableDecl) node, w);
		} else if(node instanceof Assignment) {
			assignment((Assignment) node, w);
		} else if(node instanceof NumberLiteral) {
			numberLiteral((NumberLiteral) node, w);
		} else {
			throw new Error("Don't know how to write node of type "+node.getClass().getSimpleName());
		}
		
	}

	private void numberLiteral(NumberLiteral node, TabbedWriter w) throws IOException {

		switch(node.getFormat()) {
			case DEC: 
				w.append(Integer.toString(node.getValue())); break;
			case HEX: 
				w.append(Integer.toHexString(node.getValue())); break;
			case OCT: 
				w.append(Integer.toOctalString(node.getValue())); break;
		}
		
	}

	private void assignment(Assignment node, TabbedWriter w) throws IOException {

		node(node.getLvalue(), w);
		w.append(" = ");
		node(node.getRvalue(), w);
		
	}

	private void variableDecl(VariableDecl node, TabbedWriter w) throws IOException {

		type(node.getType(), w);
		w.append(' ');
		w.append(node.getName());
		
		if(node instanceof VariableDeclAssigned) {
			VariableDeclAssigned vdass = (VariableDeclAssigned) node;
			w.append(" = ");
			node(vdass.getExpression(), w);
		}
		
	}
	
	private void type(Type node, TabbedWriter w) throws IOException {
		
		w.append(node.getName());
		
	}

	private void variableAccess(VariableAccess node, TabbedWriter w) throws IOException {

		w.append(node.getVariable());
		
	}

	private void line(Line node, TabbedWriter w) throws IOException {

		w.newLine();
		node(node.getStatement(), w);
		w.append(';');		
		
	}

	private void functionDecl(FunctionDecl node, TabbedWriter w) throws IOException {
		
		w.append("func ");
		w.append(node.getName());
		
		if(!node.getArguments().isEmpty()) {
			w.append('(');
			Iterator<VariableDecl> iter = node.getArguments().iterator();
			while(iter.hasNext()) {
				VariableDecl arg = iter.next();
				node(arg, w);
				if(iter.hasNext()) {
					w.append(", ");
				}
			}
			w.append(')');
		}
		
		w.append(" {");
		w.tab();
		w.newLine();
		
		for(Node child: node.getBody().nodes) {
			node(child, w);
		}
		
		w.untab();
		w.newLine();
		w.newLine();
		w.append("}");
		w.newLine();
		w.newLine();
		
	}
	
	private void functionCall(FunctionCall node, TabbedWriter w) throws IOException {
		
		w.append(node.getName());
		w.append('(');
		
		boolean isFirst = true;
		for(Expression arg: node.getArguments().nodes) {
			if(!isFirst) {
				w.append(", ");
			}
			node(arg, w);
			isFirst = false;
		}
		
		w.append(')');
		
	}
	
	private void stringLiteral(StringLiteral node, TabbedWriter w) throws IOException {

		w.append('"');
		w.append(SourceReader.spelled(node.getValue()));
		w.append('"');
		
	}

}
