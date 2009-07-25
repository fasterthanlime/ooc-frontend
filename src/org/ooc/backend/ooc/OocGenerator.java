package org.ooc.backend.ooc;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import org.ooc.backend.Generator;
import org.ooc.backend.TabbedWriter;
import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.Add;
import org.ooc.frontend.model.Argument;
import org.ooc.frontend.model.ArrayAccess;
import org.ooc.frontend.model.Assignment;
import org.ooc.frontend.model.Block;
import org.ooc.frontend.model.BoolLiteral;
import org.ooc.frontend.model.CharLiteral;
import org.ooc.frontend.model.ClassDecl;
import org.ooc.frontend.model.Comment;
import org.ooc.frontend.model.ControlStatement;
import org.ooc.frontend.model.CoverDecl;
import org.ooc.frontend.model.Div;
import org.ooc.frontend.model.Expression;
import org.ooc.frontend.model.Foreach;
import org.ooc.frontend.model.FunctionCall;
import org.ooc.frontend.model.FunctionDecl;
import org.ooc.frontend.model.If;
import org.ooc.frontend.model.Import;
import org.ooc.frontend.model.Include;
import org.ooc.frontend.model.Instantiation;
import org.ooc.frontend.model.Line;
import org.ooc.frontend.model.MemberArgument;
import org.ooc.frontend.model.MemberAssignArgument;
import org.ooc.frontend.model.MemberCall;
import org.ooc.frontend.model.Mul;
import org.ooc.frontend.model.Node;
import org.ooc.frontend.model.NodeList;
import org.ooc.frontend.model.Not;
import org.ooc.frontend.model.NullLiteral;
import org.ooc.frontend.model.NumberLiteral;
import org.ooc.frontend.model.Parenthesis;
import org.ooc.frontend.model.RangeLiteral;
import org.ooc.frontend.model.RegularArgument;
import org.ooc.frontend.model.Return;
import org.ooc.frontend.model.SourceUnit;
import org.ooc.frontend.model.StringLiteral;
import org.ooc.frontend.model.Sub;
import org.ooc.frontend.model.Type;
import org.ooc.frontend.model.VarArg;
import org.ooc.frontend.model.VariableAccess;
import org.ooc.frontend.model.VariableDecl;
import org.ooc.frontend.model.VariableDeclAssigned;
import org.ooc.frontend.model.Visitable;
import org.ooc.frontend.model.While;
import org.ooc.frontend.parser.TypeArgument;
import org.ubi.SourceReader;

public class OocGenerator extends Generator implements Visitor {

	private TabbedWriter w;

	public OocGenerator(File outPath, SourceUnit unit) throws IOException {
		super(outPath, unit);
		this.w = new TabbedWriter(new FileWriter(new File(outPath, unit.getFileName() + ".gen")));
	}
	
	@Override
	public void generate() throws IOException {
		unit.accept(this);
		w.close();
	}
	
	@Override
	public void visit(SourceUnit sourceUnit) throws IOException {
		sourceUnit.acceptChildren(this);
		w.append('\n'); // traditions make me cry a little bit :)
	}

	@Override
	public void visit(Add add) throws IOException {
		add.getLeft().accept(this);
		w.append(" + ");
		add.getRight().accept(this);
	}

	@Override
	public void visit(Mul mul) throws IOException {
		mul.getLeft().accept(this);
		w.append(" * ");
		mul.getRight().accept(this);
	}

	@Override
	public void visit(Sub sub) throws IOException {
		sub.getLeft().accept(this);
		w.append(" - ");
		sub.getRight().accept(this);
	}

	@Override
	public void visit(Div div) throws IOException {
		div.getLeft().accept(this);
		w.append(" / ");
		div.getRight().accept(this);
	}

	@Override
	public void visit(Not not) throws IOException {
		w.append("!");
		not.getExpression().accept(this);
	}

	@Override
	public void visit(Comment comment) throws IOException {
		// ignore for now
	}

	@Override
	public void visit(FunctionCall call) throws IOException {
		
		w.append(call.getName());
		if(call.getArguments().isEmpty()) return;
		
		w.append('(');
		
		boolean isFirst = true;
		for(Expression arg: call.getArguments()) {
			if(!isFirst) {
				w.append(", ");
			}
			arg.accept(this);
			isFirst = false;
		}
		
		w.append(')');
		
	}
	
	@Override
	public void visit(MemberCall call) throws IOException {
		call.getExpression().accept(this);
		w.append('.');
		visit((FunctionCall) call);
	}
	
	@Override
	public void visit(Instantiation inst) throws IOException {
		w.append("new");
		if(!inst.getName().isEmpty()) {
			w.append(' ');
		}
		visit((FunctionCall) inst);
	}

	@Override
	public void visit(Parenthesis parenthesis) throws IOException {
		w.append('(');
		parenthesis.getExpression().accept(this);
		w.append(')');
	}

	@Override
	public void visit(Assignment assignment) throws IOException {
		assignment.getLvalue().accept(this);
		w.append(" = ");
		assignment.getRvalue().accept(this);
	}

	@Override
	public void visit(Return return1) throws IOException {
		w.append("return ");
		return1.getExpression().accept(this);
	}

	@Override
	public void visit(NullLiteral nullLiteral) throws IOException {
		w.append("null");
	}

	@Override
	public void visit(NumberLiteral number) throws IOException {
		switch(number.getFormat()) {
		case DEC: 
			w.append(Long.toString(number.getValue())); break;
		case HEX: 
			w.append("0x");
			w.append(Long.toHexString(number.getValue())); break;
		case OCT:
			w.append("0c");
			w.append(Long.toOctalString(number.getValue())); break;
		case BIN: 
			w.append("0b");
			w.append(Long.toBinaryString(number.getValue())); break;
		}
	}

	@Override
	public void visit(StringLiteral stringLiteral) throws IOException {
		w.append('"');
		w.append(SourceReader.spelled(stringLiteral.getValue()));
		w.append('"');
	}

	@Override
	public void visit(RangeLiteral rangeLiteral) throws IOException {
		rangeLiteral.getLower().accept(this);
		w.append("..");
		rangeLiteral.getUpper().accept(this);
	}

	@Override
	public void visit(BoolLiteral boolLiteral) throws IOException {
		w.append(Boolean.toString(boolLiteral.getValue()));
	}

	@Override
	public void visit(CharLiteral charLiteral) throws IOException {
		w.append('\'');
		w.append(SourceReader.spelled(charLiteral.getValue()));
		w.append('\'');
	}

	@Override
	public void visit(Line line) throws IOException {
		w.newLine();
		line.getStatement().accept(this);
		if(!(line.getStatement() instanceof ControlStatement)) {
			w.append(';');
		}
	}

	@Override
	public void visit(Include include) throws IOException {
		w.append("include ");
		w.append(include.getPath());
		w.append(";");
		w.newLine();
	}

	@Override
	public void visit(Import import1) throws IOException {
		w.append("import ");
		w.append(import1.getName());
		w.append(";");
		w.newLine();
	}

	@Override
	public void visit(If if1) throws IOException {
		
		w.append("if (");
		if1.getCondition().accept(this);
		w.append(") {");
		w.tab();
		w.newLine();
		for(Line line: if1.getBody()) {
			line.accept(this);
		}
		w.untab();
		w.newLine();
		w.append('}');
		w.newLine();
		
	}

	@Override
	public void visit(While while1) throws IOException {
		
		w.append("while (");
		while1.getCondition().accept(this);
		w.append(") {");
		w.tab();
		w.newLine();
		for(Line line: while1.getBody()) {
			line.accept(this);
		}
		w.untab();
		w.newLine();
		w.append('}');
		w.newLine();
		
	}

	@Override
	public void visit(Foreach foreach) throws IOException {

		w.append("for (");
		foreach.getVariable().accept(this);
		w.append(": ");
		foreach.getCollection().accept(this);
		w.append(") {");
		w.tab();
		w.newLine();
		for(Line line: foreach.getBody()) {
			line.accept(this);
		}
		w.untab();
		w.newLine();
		w.append('}');
		w.newLine();
		
	}

	@Override
	public void visit(VariableAccess access) throws IOException {
		w.append(access.getVariable());
	}

	@Override
	public void visit(ArrayAccess access) throws IOException {
		access.getVariable().accept(this);
		w.append('[');
		access.getIndex().accept(this);
		w.append(']');
	}

	@Override
	public void visit(VariableDecl decl) throws IOException {
		decl.getType().accept(this);
		w.append(' ');
		w.append(decl.getName());
	}

	@Override
	public void visit(VariableDeclAssigned decl)
			throws IOException {
		visit((VariableDecl) decl);
		w.append(" = ");
		decl.getExpression().accept(this);
	}

	@Override
	public void visit(FunctionDecl node) throws IOException {
	
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
				arg.accept(this); 
				if(iter.hasNext()) w.append(", ");
			}
			w.append(')');
		}
		
		if(node.getReturnType() != null) {
			w.append(" -> ");
			node.getReturnType().accept(this);
		}
		
		if(node.getBody().isEmpty()) {
		
			w.append(';');
			
		} else {
			
			w.append(" {");
			w.tab();
			
			for(Visitable child: node.getBody()) {
				child.accept(this);
			}
			
			w.untab();
			w.newLine();
			w.append("}");
		
		}
		
	}

	@Override
	public void visit(ClassDecl node) throws IOException {

		w.newLine();
		if(node.getComment() != null) {
			node.getComment().accept(this);
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
			variable.accept(this);
			w.append(';');
			w.newLine();
		}
		
		for(FunctionDecl function: node.getFunctions()) {
			function.accept(this);
			w.newLine();
		}
		
		w.untab();
		w.newLine();
		w.append('}');
		w.newLine();
		
	}
	
	@Override
	public void visit(CoverDecl cover) throws IOException {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void visit(TypeArgument typeArgument) throws IOException {
		typeArgument.getType().accept(this);
	}

	@Override
	public void visit(RegularArgument arg) throws IOException {
		arg.getType().accept(this);
		w.append(' ');
		w.append(arg.getName());
	}

	@Override
	public void visit(MemberArgument arg) throws IOException {
		w.append('=');
		w.append(arg.getName());
	}

	@Override
	public void visit(MemberAssignArgument arg) throws IOException {
		w.append(arg.getName());		
	}
	
	@Override
	public void visit(VarArg varArg) throws IOException {
		w.append("...");
	}

	@Override
	public void visit(Type type) throws IOException {
		w.append(type.getName());
		for(int i = 0; i < type.getPointerLevel(); i++) {
			w.append('*');
		}
	}

	@Override
	public void visit(NodeList<? extends Node> list) throws IOException {
		list.acceptChildren(this);
	}

	@Override
	public void visit(Block block) throws IOException {
		w.append('{');
		w.tab();
		w.newLine();
		block.acceptChildren(this);
		w.untab();
		w.newLine();
		w.append('}');
		w.newLine();
	}
	
}
