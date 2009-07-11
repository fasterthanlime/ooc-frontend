package org.ooc.backend.ooc;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import org.ooc.backend.Generator;
import org.ooc.backend.TabbedWriter;
import org.ooc.frontend.model.Argument;
import org.ooc.frontend.model.Assignment;
import org.ooc.frontend.model.CharLiteral;
import org.ooc.frontend.model.ClassDecl;
import org.ooc.frontend.model.Comment;
import org.ooc.frontend.model.ControlStatement;
import org.ooc.frontend.model.Expression;
import org.ooc.frontend.model.Foreach;
import org.ooc.frontend.model.FunctionCall;
import org.ooc.frontend.model.FunctionDecl;
import org.ooc.frontend.model.Include;
import org.ooc.frontend.model.Instantiation;
import org.ooc.frontend.model.Line;
import org.ooc.frontend.model.MemberAccess;
import org.ooc.frontend.model.MemberArgument;
import org.ooc.frontend.model.MemberAssignArgument;
import org.ooc.frontend.model.MemberCall;
import org.ooc.frontend.model.Node;
import org.ooc.frontend.model.NumberLiteral;
import org.ooc.frontend.model.Parenthesis;
import org.ooc.frontend.model.RangeLiteral;
import org.ooc.frontend.model.RegularArgument;
import org.ooc.frontend.model.Return;
import org.ooc.frontend.model.SourceUnit;
import org.ooc.frontend.model.StringLiteral;
import org.ooc.frontend.model.Type;
import org.ooc.frontend.model.VariableAccess;
import org.ooc.frontend.model.VariableDecl;
import org.ooc.frontend.model.VariableDeclAssigned;
import org.ubi.SourceReader;

public class OocGenerator extends Generator {

	private TabbedWriter w;
	private FileWriter fw;
	
	public OocGenerator(File outPath, SourceUnit unit) throws IOException {
		super(outPath, unit);
		this.fw = new FileWriter(new File(outPath, unit.getFileName() + ".gen"));
		this.w = new TabbedWriter(fw);
	}
	
	@Override
	public void generate() throws IOException {

		sourceUnit(unit);
		fw.close();
		
	}

	private void sourceUnit(SourceUnit unit) throws IOException {

		for(Include include: unit.getIncludes()) {
			include(include);
		}
		
		for(Node node: unit.getBody()) {
			node(node);
		}
		
	}
	
	private void include(Include include) throws IOException {

		w.append("include ");
		w.append(include.getInclude());
		w.append(';');
		w.newLine();
		
	}

	private void node(Node node) throws IOException {
		
		if(node instanceof Line) {
			line((Line) node);
		} else if(node instanceof ClassDecl) {
			classDecl((ClassDecl) node);
		} else if(node instanceof FunctionDecl) {
			functionDecl((FunctionDecl) node);
		} else if(node instanceof FunctionCall) {
			functionCall((FunctionCall) node);
		} else if(node instanceof VariableAccess) {
			variableAccess((VariableAccess) node);
		} else if(node instanceof VariableDecl) {
			variableDecl((VariableDecl) node);
		} else if(node instanceof Assignment) {
			assignment((Assignment) node);
		} else if(node instanceof StringLiteral) {
			stringLiteral((StringLiteral) node);
		} else if(node instanceof CharLiteral) {
			charLiteral((CharLiteral) node);
		} else if(node instanceof NumberLiteral) {
			numberLiteral((NumberLiteral) node);
		} else if(node instanceof RangeLiteral) {
			rangeLiteral((RangeLiteral) node);
		} else if(node instanceof Foreach) {
			foreach((Foreach) node);
		} else if(node instanceof Return) {
			returnStatement((Return) node);
		} else if(node instanceof Parenthesis) {
			parenthesis((Parenthesis) node);
		} else if(node instanceof Comment) {
			comment((Comment) node);
		} else {
			throw new Error("Don't know how to write node of type "+node.getClass().getSimpleName());
		}
		
	}

	private void comment(Comment node) throws IOException {
		
		w.append(node.getContent());
		w.newLine();
		
	}

	private void parenthesis(Parenthesis node) throws IOException {
		
		w.append('(');
		node(node.getExpression());
		w.append(')');
		
	}

	private void rangeLiteral(RangeLiteral node) throws IOException {

		node(node.getLower());
		w.append("..");
		node(node.getUpper());
		
		
	}

	private void foreach(Foreach node) throws IOException {
		
		w.newLine();
		w.append("for (");
		variableDecl(node.getVariable());
		w.append(": ");
		node(node.getCollection());
		w.append(") ");
		
		if(node.getBody().size() == 1) {
			w.tab();
			node(node.getBody().get(0));
			w.untab();
			return;
		}
		
		w.append('{');
		w.tab();
		w.newLine();
		w.newLine();
		
		for(Line line: node.getBody()) {
			node(line);
		}
		
		w.untab();
		w.newLine();
		w.append('}');
		w.newLine();
		w.newLine();
		
	}

	private void returnStatement(Return node) throws IOException {

		w.append("return ");
		node(node.getExpression());
		
	}

	private void classDecl(ClassDecl node) throws IOException {

		w.newLine();
		if(node.getComment() != null) {
			node(node.getComment());
		}
		if(node.isAbstract()) {
			w.append("abstract ");
		}
		w.append("class ");
		w.append(node.getName());
		if(!node.getSuperName().isEmpty()) {
			w.append("from ");
			w.append(node.getSuperName());
		}
		w.append(" {");
		
		if(node.getVariables().isEmpty() && node.getFunctions().isEmpty()) {
			w.append('}');
			w.newLine();
			w.newLine();
			return;
		}
		
		w.tab();
		w.newLine();
		w.newLine();
		
		for(VariableDecl variable: node.getVariables()) {
			variableDecl(variable);
			w.append(';');
			w.newLine();
		}
		
		for(FunctionDecl function: node.getFunctions()) {
			functionDecl(function);
			w.newLine();
		}
		
		w.untab();
		w.newLine();
		w.append('}');
		w.newLine();
		
	}

	private void numberLiteral(NumberLiteral node) throws IOException {

		switch(node.getFormat()) {
			case DEC: 
				w.append(Long.toString(node.getValue())); break;
			case HEX: 
				w.append("0x");
				w.append(Long.toHexString(node.getValue())); break;
			case OCT:
				w.append("0c");
				w.append(Long.toOctalString(node.getValue())); break;
			case BIN: 
				w.append("0b");
				w.append(Long.toBinaryString(node.getValue())); break;
		}
		
	}

	private void assignment(Assignment node) throws IOException {

		node(node.getLvalue());
		w.append(" = ");
		node(node.getRvalue());
		
	}

	private void variableDecl(VariableDecl node) throws IOException {

		type(node.getType());
		w.append(' ');
		w.append(node.getName());
		
		if(node instanceof VariableDeclAssigned) {
			VariableDeclAssigned vdass = (VariableDeclAssigned) node;
			w.append(" = ");
			node(vdass.getExpression());
		}
		
	}
	
	private void type(Type node) throws IOException {
		
		w.append(node.getName());
		for(int i = 0; i < node.getPointerLevel(); i++) {
			w.append("*");
		}
		
	}

	private void variableAccess(VariableAccess node) throws IOException {

		if(node instanceof MemberAccess) {
			MemberAccess mAccess = (MemberAccess) node;
			node(mAccess.getExpression());
			w.append('.');
		}
		
		w.append(node.getVariable());
		
	}

	private void line(Line node) throws IOException {

		w.newLine();
		node(node.getStatement());
		if(!(node.getStatement() instanceof ControlStatement)) {
			w.append(';');
		}
		
	}

	private void functionDecl(FunctionDecl node) throws IOException {
		
		w.newLine();
		if(node.isAbstract()) {
			w.append("abstract ");
		}
		w.append("func ");
		w.append(node.getName());
		
		if(!node.getArguments().isEmpty()) {
			w.append('(');
			Iterator<Argument> iter = node.getArguments().iterator();
			while(iter.hasNext()) {
				Argument arg = iter.next();
				if(arg instanceof RegularArgument) {
					RegularArgument regArg = (RegularArgument) arg;
					type(regArg.getType());
					w.append(' ');
					w.append(regArg.getName());
				} else if(arg instanceof MemberAssignArgument) {
					w.append('=');
					w.append(arg.getName());
				} else if(arg instanceof MemberArgument) {
					w.append(arg.getName());
				} 
				if(iter.hasNext()) {
					w.append(", ");
				}
			}
			w.append(')');
		}
		
		if(node.getReturnType() != null) {
			w.append(" -> ");
			type(node.getReturnType());
		}
		
		if(node.getBody().isEmpty()) {
		
			w.append(';');
			//w.newLine();
			//w.newLine();
			
		} else {
			
			w.append(" {");
			w.tab();
			//w.newLine();
			
			for(Node child: node.getBody()) {
				node(child);
			}
			
			w.untab();
			//w.newLine();
			w.newLine();
			w.append("}");
			//w.newLine();
			//w.newLine();
		
		}
		
	}
	
	private void functionCall(FunctionCall node) throws IOException {
		
		if(node instanceof MemberCall) {
			MemberCall mfCall = (MemberCall) node;
			node(mfCall.getExpression());
			w.append('.');
		}
		
		if(node instanceof Instantiation) {
			w.append("new");
			if(!node.getName().isEmpty()) {
				w.append(' ');
			}
		}
		
		w.append(node.getName());
		
		if(node.getArguments().isEmpty()) return;
		
		w.append('(');
		
		boolean isFirst = true;
		for(Expression arg: node.getArguments()) {
			if(!isFirst) {
				w.append(", ");
			}
			node(arg);
			isFirst = false;
		}
		
		w.append(')');
		
	}
	
	private void stringLiteral(StringLiteral node) throws IOException {

		w.append('"');
		w.append(SourceReader.spelled(node.getValue()));
		w.append('"');
		
	}
	
	private void charLiteral(CharLiteral node) throws IOException {
		
		w.append('\'');
		w.append(SourceReader.spelled(node.getValue()));
		w.append('\'');
		
	}

}
