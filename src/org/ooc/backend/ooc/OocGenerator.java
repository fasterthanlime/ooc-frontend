package org.ooc.backend.ooc;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import org.ooc.backend.Generator;
import org.ooc.backend.TabbedWriter;
import org.ooc.frontend.model.Declaration;
import org.ooc.frontend.model.Expression;
import org.ooc.frontend.model.FunctionCall;
import org.ooc.frontend.model.FunctionDecl;
import org.ooc.frontend.model.Node;
import org.ooc.frontend.model.SourceUnit;
import org.ooc.frontend.model.StringLiteral;
import org.ubi.SourceReader;

public class OocGenerator implements Generator {

	@Override
	public void generate(File outPath, SourceUnit unit) throws IOException {

		String fileName = unit.getLocation().getFileName();
		FileWriter w = new FileWriter(new File(outPath, fileName + ".gen"));
		sourceUnit(unit, new TabbedWriter(w));
		w.close();
		
	}

	private void sourceUnit(SourceUnit unit, TabbedWriter w) throws IOException {

		Iterator<Declaration> nodes = unit.getBody().iterator();
		while(nodes.hasNext()) {
			node(nodes.next(), w);
		}
		
	}
	
	private void node(Node node, TabbedWriter w) throws IOException {
		
		if(node instanceof FunctionDecl) {
			functionDecl((FunctionDecl) node, w);
		} else if(node instanceof FunctionCall) {
			functionCall((FunctionCall) node, w);
		} else if(node instanceof StringLiteral) {
			stringLiteral((StringLiteral) node, w);
		} else {
			throw new Error("Don't know how to write node of type "+node.getClass().getSimpleName());
		}
		
	}

	private void functionDecl(FunctionDecl node, TabbedWriter w) throws IOException {
		
		w.append("func ");
		w.append(node.getName());
		w.append(" {");
		w.tab();
		w.newLine();
		
		for(Node child: node.getBody().nodes) {
			node(child, w);
		}
		
		w.untab();
		w.newLine();
		w.append("}");
		w.newLine();
		
	}
	
	private void functionCall(FunctionCall node, TabbedWriter w) throws IOException {
		
		w.append(node.getName());
		w.append('(');
		
		boolean isFirst = true;
		for(Expression arg: node.getArguments().nodes) {
			node(arg, w);
			if(!isFirst) {
				w.append(", ");
			}
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
