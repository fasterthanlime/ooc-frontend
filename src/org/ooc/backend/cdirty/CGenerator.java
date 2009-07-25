package org.ooc.backend.cdirty;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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
import org.ooc.frontend.model.Mod;
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
import org.ooc.frontend.model.ValuedReturn;
import org.ooc.frontend.model.SourceUnit;
import org.ooc.frontend.model.StringLiteral;
import org.ooc.frontend.model.Sub;
import org.ooc.frontend.model.Type;
import org.ooc.frontend.model.VarArg;
import org.ooc.frontend.model.VariableAccess;
import org.ooc.frontend.model.VariableDecl;
import org.ooc.frontend.model.VariableDeclAssigned;
import org.ooc.frontend.model.While;
import org.ooc.frontend.model.FunctionDecl.FunctionDeclType;
import org.ooc.frontend.parser.TypeArgument;
import org.ubi.SourceReader;

public class CGenerator extends Generator implements Visitor {

	private TabbedWriter hw;
	private TabbedWriter cw;
	private TabbedWriter current;

	public CGenerator(File outPath, SourceUnit unit) throws IOException {
		super(outPath, unit);
		this.hw = new TabbedWriter(new FileWriter(new File(outPath, unit.getName() + ".h")));
		this.cw = new TabbedWriter(new FileWriter(new File(outPath, unit.getName() + ".c")));
		this.current = hw;
	}

	@Override
	public void generate() throws IOException {
		unit.accept(this);
		hw.close();
		cw.close();
	}

	@Override
	public void visit(SourceUnit sourceUnit) throws IOException {
		
		current = hw;
		current.append("/* ");
		current.append(unit.getName());
		current.append(" header file, generated with ooc */");
		current.newLine();
		
		for(Include include: sourceUnit.getIncludes()) {
			current.append("#include <");
			current.append(include.getPath());
			current.append(".h>");
			current.newLine();
		}
		
		current.newLine();
		
		current = cw;
		current.append("/* ");
		current.append(unit.getName());
		current.append(" source file, generated with ooc */");
		current.newLine();
		
		current.append("#include \"");
		current.append(unit.getSimpleName());
		current.append(".h\"");
		current.newLine();
		current.newLine();
		
		sourceUnit.acceptChildren(this);
		
	}

	@Override
	public void visit(Add add) throws IOException {
		add.getLeft().accept(this);
		current.append(" + ");
		add.getRight().accept(this);		
	}

	@Override
	public void visit(Mul mul) throws IOException {
		mul.getLeft().accept(this);
		current.append(" * ");
		mul.getRight().accept(this);		
	}

	@Override
	public void visit(Sub sub) throws IOException {
		sub.getLeft().accept(this);
		current.append(" - ");
		sub.getRight().accept(this);		
	}

	@Override
	public void visit(Div div) throws IOException {
		div.getLeft().accept(this);
		current.append(" / ");
		div.getRight().accept(this);
	}

	@Override
	public void visit(Not not) throws IOException {
		current.append('!');
		not.getExpression().accept(this);		
	}
	
	@Override
	public void visit(Mod mod) throws IOException {
		mod.getLeft().accept(this);
		current.append(" % ");
		mod.getRight().accept(this);
	}

	@Override
	public void visit(Comment comment) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(FunctionCall functionCall) throws IOException {

		current.append(functionCall.getName());
		current.append('(');
		
		boolean isFirst = true;
		
		for(Expression expr: functionCall.getArguments()) {
			
			if(!isFirst) {
				current.append(", ");
			}
			expr.accept(this);
			
			isFirst = false;

		}
		
		current.append(')');
		
	}

	@Override
	public void visit(MemberCall memberCall) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(Instantiation inst) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(Parenthesis parenthesis) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(Assignment assignment) throws IOException {

		assignment.getLvalue().accept(this);
		current.append(" = ");
		assignment.getRvalue().accept(this);
		
	}

	@Override
	public void visit(ValuedReturn return1) throws IOException {
		current.append("return ");
		return1.getExpression().accept(this);
	}
	
	@Override
	public void visit(Return return1) throws IOException {
		current.append("return");
	}

	@Override
	public void visit(NullLiteral nullLiteral) throws IOException {
		current.append("NULL");
	}

	@Override
	public void visit(NumberLiteral numberLiteral) throws IOException {

		current.append(String.valueOf(numberLiteral.getValue()));
	}

	@Override
	public void visit(StringLiteral stringLiteral) throws IOException {
		current.append('"');
		current.append(SourceReader.spelled(stringLiteral.getValue()));
		current.append('"');
	}

	@Override
	public void visit(RangeLiteral rangeLiteral) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(BoolLiteral boolLiteral) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(CharLiteral charLiteral) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(Line line) throws IOException {

		current.newLine();
		line.getStatement().accept(this);
		if(!(line.getStatement() instanceof ControlStatement)) {
			current.append(';');
		}
		
	}

	@Override
	public void visit(Include include) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(Import import1) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(If if1) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(While while1) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(Foreach foreach) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(VariableAccess variableAccess) throws IOException {

		current.append(variableAccess.getVariable());
		
	}

	@Override
	public void visit(ArrayAccess arrayAccess) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(VariableDecl variableDecl) throws IOException {

		variableDecl.getType().accept(this);
		current.append(' ');
		current.append(variableDecl.getName());
		
	}

	@Override
	public void visit(VariableDeclAssigned variableDeclAssigned)
			throws IOException {
		
		visit((VariableDecl) variableDeclAssigned);
		current.append(" = ");
		variableDeclAssigned.getExpression().accept(this);
	}

	@Override
	public void visit(FunctionDecl functionDecl) throws IOException {
		
		if(functionDecl.getDeclType() != FunctionDeclType.EXTERN && !functionDecl.isAbstract()) {
		
			hw.newLine();
			hw.append("/* Function "+unit.getName()+"."+functionDecl.getName()+" */");
			hw.newLine();
			
			current = hw;
			writeFuncPrototype(functionDecl);
			hw.append(';');
		
			current = cw;
			writeFuncPrototype(functionDecl);
			cw.append(" {");
			cw.tab();
			cw.newLine();
			
			for(Line line: functionDecl.getBody()) {
				line.accept(this);
			}
			
			cw.untab();
			cw.newLine();
			cw.newLine();
			cw.append("}");
			cw.newLine();
			
		}
		
	}

	private void writeFuncPrototype(FunctionDecl functionDecl) throws IOException {
		
		functionDecl.getReturnType().accept(this);
		current.append(' ');
		current.append(functionDecl.getName());
		current.append('(');
		
		boolean isFirst = true;
		for(Argument arg: functionDecl.getArguments()) {
			
			if(!isFirst) {
				current.append(", ");
			}
			
			isFirst = false;
			arg.accept(this);
			
		}
		
		current.append(')');
		
	}

	@Override
	public void visit(ClassDecl classDecl) throws IOException {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void visit(CoverDecl cover) throws IOException {

		current = hw;
		
		Type fromType = cover.getFromType();
		if(fromType == null) {
			throw new UnsupportedOperationException("Doesn't support compound covers yet.");
		}
		current.append("typedef ");
		fromType.accept(this);
		current.append(' ');
		current.append(cover.getName());
		current.append(';');
		current.newLine();
		
	}
	
	@Override
	public void visit(TypeArgument typeArgument) throws IOException {
		typeArgument.getType().accept(this);
	}

	@Override
	public void visit(RegularArgument regularArgument) throws IOException {

		regularArgument.getType().accept(this);
		current.append(' ');
		current.append(regularArgument.getName());
		
	}

	@Override
	public void visit(MemberArgument memberArgument) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(MemberAssignArgument memberArgument) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(Type type) throws IOException {
		
		current.append(type.getName());
		if(type.getPointerLevel() > 0) {
			current.append(' ');
		}
		for(int i = 0; i < type.getPointerLevel(); i++) {
			current.append('*');
		}
		
	}

	@Override
	public void visit(VarArg varArg) throws IOException {
		
		current.append("...");
		
	}
	
	@Override
	public void visit(NodeList<? extends Node> list) throws IOException {
		list.acceptChildren(this);
	}
	
	@Override
	public void visit(Block block) throws IOException {
		current.append('{');
		current.tab();
		current.newLine();
		block.acceptChildren(this);
		current.untab();
		current.newLine();
		current.append('}');
		current.newLine();
	}

}
