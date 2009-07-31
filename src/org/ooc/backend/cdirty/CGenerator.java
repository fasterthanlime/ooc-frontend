package org.ooc.backend.cdirty;

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
import org.ooc.frontend.model.BuiltinType;
import org.ooc.frontend.model.CharLiteral;
import org.ooc.frontend.model.ClassDecl;
import org.ooc.frontend.model.Comment;
import org.ooc.frontend.model.MultiLineComment;
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
import org.ooc.frontend.model.MemberAccess;
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
import org.ooc.frontend.model.SingleLineComment;
import org.ooc.frontend.model.SourceUnit;
import org.ooc.frontend.model.StringLiteral;
import org.ooc.frontend.model.Sub;
import org.ooc.frontend.model.Type;
import org.ooc.frontend.model.ValuedReturn;
import org.ooc.frontend.model.VarArg;
import org.ooc.frontend.model.VariableAccess;
import org.ooc.frontend.model.VariableDecl;
import org.ooc.frontend.model.VariableDeclAssigned;
import org.ooc.frontend.model.While;
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
		
		String hName = "__" + sourceUnit.getName().toUpperCase().replaceAll("[^a-zA-Z0-9_]", "_") + "__";
		current.append("#ifndef ");
		current.append(hName);
		current.newLine();
		current.append("#define ");
		current.append(hName);
		current.newLine();
		current.newLine();
		
		// FIXME of course this is a dirty hack because this devbranch
		// is missing the whole fancy cmdline frontend with sdk search etc.
		current.append("#include <mango/mangoobject.h>");
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
		
		current = hw;
		current.newLine();
		current.newLine();
		current.append("#endif // ");
		current.append(hName);
		current.newLine();
		current.newLine();
		
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
	public void visit(MultiLineComment comment) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(FunctionCall functionCall) throws IOException {

		FunctionDecl decl = functionCall.getImpl();
		writeSuffixedFuncName(decl);
		current.append('(');
		writeExprList(functionCall.getArguments());
		current.append(')');
		
	}

	private void writeExprList(NodeList<Expression> args) throws IOException {
		boolean isFirst = true;

		for(Expression expr: args) {
			
			if(!isFirst) {
				current.append(", ");
			}
			expr.accept(this);
			
			isFirst = false;

		}
	}

	@Override
	public void visit(MemberCall memberCall) throws IOException {
		
		current.append(memberCall.getExpression().getType().getName());
		current.append('_');
		current.append(memberCall.getName());
		current.append('(');
		memberCall.getExpression().accept(this);
		if(!memberCall.getArguments().isEmpty()) current.append(", ");
		writeExprList(memberCall.getArguments());
		current.append(')');
		
	}

	@Override
	public void visit(Instantiation inst) throws IOException {

		FunctionDecl decl = inst.getImpl();
		current.append(inst.getName()); // Actually the class name
		current.append('_');
		writeSuffixedFuncName(decl);
		current.append('(');
		writeExprList(inst.getArguments());
		current.append(')');
		
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

		switch(numberLiteral.getFormat()) {
			case HEX:
			case BIN: // C has no binary literals, write it has hex
				current.append("0x");
				current.append(Long.toHexString(numberLiteral.getValue()));
				break;
			case OCT:
				current.append('0');
				current.append(Long.toOctalString(numberLiteral.getValue()));
				break;
			default:
				current.append(String.valueOf(numberLiteral.getValue()));
		}
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
		current.append('\'');
		current.append(SourceReader.spelled(charLiteral.getValue()));
		current.append('\'');		
	}

	@Override
	public void visit(Line line) throws IOException {

		current.newLine();
		line.getStatement().accept(this);
		if(!(line.getStatement() instanceof ControlStatement)
				&& !(line instanceof Comment)) {
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
	public void visit(MemberAccess memberAccess) throws IOException {

		memberAccess.getExpression().accept(this);
		current.append("->");
		current.append(memberAccess.getName());
		
	}
	
	@Override
	public void visit(VariableAccess variableAccess) throws IOException {

		current.append(variableAccess.getName());
		
	}

	@Override
	public void visit(ArrayAccess arrayAccess) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(VariableDecl variableDecl) throws IOException {

		variableDecl.getType().accept(this);
		if(variableDecl.getType().isFlat()) current.append(' ');
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
		
		if(!functionDecl.isExtern() && !functionDecl.isAbstract()) {
		
			hw.newLine();
			hw.append("/* Function "+unit.getName()+"."+functionDecl.getName()+" */");
			hw.newLine();
			
			current = hw;
			writeFuncPrototype(functionDecl);
			hw.append(';');
		
			current = cw;
			writeFuncPrototype(functionDecl);
			openBlock();
			
			for(Line line: functionDecl.getBody()) {
				line.accept(this);
			}
			
			closeSpacedBlock();
			
		}
		
	}

	private void writeFuncPrototype(FunctionDecl functionDecl) throws IOException {
		
		functionDecl.getReturnType().accept(this);
		if(functionDecl.getReturnType().getPointerLevel() == 0) current.append(' ');
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
		
		// TODO modularize the heck out of this function: it's a mess.
		current = hw;
		
		String className = classDecl.getName();
		String initializeName = className+"_initialize";
		String destroyName = className+"_destroy";
		
		writeStructTypedef(className);
		writeStructTypedef(className+"Class");
		writeObjectStruct(classDecl, className);
		writeClassStruct(classDecl, className);
		writeMemberFuncPrototypes(classDecl, className);
		
		/* Now implementations */
		current = cw;
		current.newLine();
		
		writeInitializeFunc(classDecl, className);
		writeDestroyFunc(classDecl, className);
		writeInstanceImplFuncs(classDecl, className);
		writeClassGettingFunction(classDecl, className, initializeName, destroyName);
		writeVirtualNonImplFuncs(classDecl, className);
		writeStaticFuncs(classDecl, className);
		
		current.newLine();
		
	}
	
	private void writeMemberFuncPrototype(String className,
			FunctionDecl decl) throws IOException {
		
		writeSpacedType(decl.getReturnType());
		current.append(className);
		current.append('_');
		current.append(decl.getName());
		writeFuncArgs(decl);
		
	}

	private void writeFuncArgs(FunctionDecl decl)
			throws IOException {
		
		current.append('(');
		Iterator<Argument> iter = decl.getArguments().iterator();
		while(iter.hasNext()) {
			iter.next().accept(this);
			if(iter.hasNext()) current.append(", ");
		}
		current.append(')');
		
	}
	
	private void writeTypelessFuncArgs(FunctionDecl decl)
	throws IOException {

		current.append('(');
		Iterator<Argument> iter = decl.getArguments().iterator();
		while(iter.hasNext()) {
			current.append(iter.next().getName());
			if(iter.hasNext()) current.append(", ");
		}
		current.append(')');

	}

	private void writeTypelessFuncArgsPlusThis(FunctionDecl decl)
	throws IOException {

		current.append('(');
		Iterator<Argument> iter = decl.getArguments().iterator();
		current.append("this");
		if(iter.hasNext()) current.append(", ");
		while(iter.hasNext()) {
			current.append(iter.next().getName());
			if(iter.hasNext()) current.append(", ");
		}
		current.append(')');

	}
	
	private void writeFuncArgsPlusThis(String className, FunctionDecl decl)
	throws IOException {

		current.append('(');
		Iterator<Argument> iter = decl.getArguments().iterator();
		current.append(className);
		current.append(" *this");
		if(iter.hasNext()) current.append(", ");
		while(iter.hasNext()) {
			iter.next().accept(this);
			if(iter.hasNext()) current.append(", ");
		}
		current.append(')');

	}

	private void writeStaticFuncs(ClassDecl classDecl, String className)
			throws IOException {
		
		for(FunctionDecl decl: classDecl.getFunctions()) {
			
			if(!decl.isStatic()) continue;
			
			current.newLine();
			writeMemberFuncPrototype(className, decl);
			openBlock();
			
			/* Special case: constructor */
			if(decl.isConstructor()) {
				current.newLine();
				current.append(className);
				current.append(" *this = (");
				current.append(className);
				current.append(" *) mango_object_new(");
				current.append(className);
				current.append("_class());");
				current.newLine();
				current.append(className);
				current.append("_construct");
				if(!decl.getSuffix().isEmpty()) {
					current.append('_');
					current.append(decl.getSuffix());
				}
				writeTypelessFuncArgsPlusThis(decl);
				current.append(";");
				current.newLine();
				current.append("return this;");
			} else {
				decl.getBody().accept(this);
			}
			
			closeSpacedBlock();
			
			if(decl.isConstructor()) {
				// And now, write the corresponding construct function
				current.append("void ");
				current.append(className);
				current.append("_construct");
				if(!decl.getSuffix().isEmpty()) {
					current.append('_');
					current.append(decl.getSuffix());
				}
				writeFuncArgsPlusThis(className, decl);
				openBlock();
				for(Line line: decl.getBody()) {
					line.accept(this);
				}
				closeSpacedBlock();
			}
			
		}
	}

	private void writeVirtualNonImplFuncs(ClassDecl classDecl, String className)
			throws IOException {
		
		for(FunctionDecl decl: classDecl.getFunctions()) {
			
			if(decl.isStatic()) continue;
			
			current.newLine();
			writeMemberFuncPrototype(className, decl);
			openSpacedBlock();
			
			if(!decl.getReturnType().isVoid()) current.append("return ");
			current.append("((");
			current.append(className);
			current.append("Class *)((MangoObject *)this)->class)->");
			writeSuffixedFuncName(decl);
			
			writeTypelessFuncArgs(decl);
			current.append(";");
			
			closeSpacedBlock();
			
		}
	}

	private void writeSuffixedFuncName(FunctionDecl decl) throws IOException {
		current.append(decl.getName());
		if(!decl.getSuffix().isEmpty()) {
			current.append('_');
			current.append(decl.getSuffix());
		}
	}
	
	private void writeSuffixedMemberFuncName(String className, FunctionDecl decl) throws IOException {
		current.append(className);
		current.append('_');
		writeSuffixedFuncName(decl);
	}
	
	private void writeBuiltinClassFuncName(String className, String returnType, String name)
		throws IOException {
		current.newLine();
		current.append("static ");
		current.append(returnType);
		current.append(' ');
		current.append(className);
		current.append('_');
		current.append(name);
		current.append('(');
		current.append(className);
		current.append(" *this)");
	}

	private void writeInitializeFunc(ClassDecl classDecl, String className)
			throws IOException {
		
		writeBuiltinClassFuncName(className, "void", "initialize");
		openBlock();
		
		if(!classDecl.getSuperName().isEmpty()) {
			current.newLine();
			current.append(classDecl.getSuperName());
			current.append("_class()->initialize(this);");
		}
		
		for(Line line: classDecl.getInitializer().getBody()) {
			line.accept(this);
		}
		
		closeSpacedBlock();
		
	}

	private void writeDestroyFunc(ClassDecl classDecl, String className)
			throws IOException {
		
		current.newLine();
		writeBuiltinClassFuncName(className, "void", "destroy");
		openBlock();
		
		openSpacedBlock();
		current.append("const MangoClass * super = ((MangoObject *) this)->class->super;");
		current.newLine();
		current.append("if(super) super->destroy((MangoObject *) this);");
		closeSpacedBlock();
		
		/*
		if(!classDecl.getSuperName().isEmpty()) {
			current.newLine();
			current.append(classDecl.getSuperName());
			current.append("_class()->destroy(this);");
		}
		*/
		
		closeSpacedBlock();
	}

	private void closeSpacedBlock() throws IOException {
		closeBlock();
		current.newLine();
		current.newLine();
	}

	private void writeInstanceImplFuncs(ClassDecl classDecl, String className)
			throws IOException {
		/* Non-static (ie. instance) functions */
		for(FunctionDecl decl: classDecl.getFunctions()) {
			if(decl.isStatic()) continue;
			
			current.newLine();
			current.append("static ");
			writeSpacedType(decl.getReturnType());
			current.append(className);
			current.append('_');
			writeSuffixedFuncName(decl);
			current.append("_impl");
			
			writeFuncArgs(decl);
			
			openBlock();
			decl.getBody().accept(this);
			closeSpacedBlock();
		}
	}

	private void writeClassGettingFunction(ClassDecl classDecl,
			String className, String initializeName, String destroyName) throws IOException {
		
		current.append("const MangoClass *");
		current.append(className);
		current.append("_class()");
		openSpacedBlock();
		
		current.append("static const ");
		current.append(className);
		current.append("Class class = ");
		openBlock();
		
		/* class attributes */
		openBlock();
		
		/* size of class */
		current.newLine();
		current.append(".size = ");
		current.append("sizeof(");
		current.append(className);
		current.append("),");
		
		/* name of class */
		current.newLine();
		current.append(".name = ");
		current.append('"');
		current.append(className);
		current.append("\",");
		
		/* initialize, destroy, copy */
		writeDesignatedInit("initialize", "(void (*)(MangoObject *))"+ initializeName);
		writeDesignatedInit("destroy", "(void (*)(MangoObject *))"+destroyName);
		
		closeBlock();
		current.append(',');
		
		for(FunctionDecl decl: classDecl.getFunctions()) {
			if(decl.isStatic()) continue;
			
			if(decl.isFinal()) writeDesignatedInit(decl.getName(), className + "_" + decl.getName());
			else writeDesignatedInit(decl.getName(), className + "_" + decl.getName() + "_impl");
			
		}
		
		closeBlock();
		current.append(';');
		
		current.newLine();
		current.append("return (const MangoClass *) &class;");
		closeSpacedBlock();
	}

	private void writeMemberFuncPrototypes(ClassDecl classDecl, String className)
			throws IOException {
		current.newLine();
		current.append("const MangoClass *");
		current.append(className);
		current.append("_class();");
		current.newLine();
		
		/* Now all other functions' prototypes */
		for(FunctionDecl decl: classDecl.getFunctions()) {
			current.newLine();
			writeSpacedType(decl.getReturnType());
			writeSuffixedMemberFuncName(className, decl);
			writeFuncArgs(decl);
			current.append(';');
			
			if(decl.getName().equals("new")) {
				current.newLine();
				current.append("void ");
				current.append(className);
				current.append("_construct");
				if(!decl.getSuffix().isEmpty()) {
					current.append('_');
					current.append(decl.getSuffix());
				}
				writeFuncArgsPlusThis(classDecl.getName(), decl);
				current.append(';');
			}
		}
		
		current.newLine();
	}

	private void writeSpacedType(Type type) throws IOException {
		type.accept(this);
		if(type.isFlat()) current.append(' ');
	}

	private Iterator<Argument> writeFuncPointer(FunctionDecl decl)
			throws IOException {
		decl.getReturnType().accept(this);
		current.append(" (*");
		current.append(decl.getName());
		current.append(")(");
		Iterator<Argument> iter = decl.getArguments().iterator();
		while(iter.hasNext()) {
			Argument arg = iter.next();
			arg.accept(this);
			if(iter.hasNext()) current.append(", ");
		}
		current.append(')');
		return iter;
	}
	
	private void writeClassStruct(ClassDecl classDecl, String className)
		throws IOException {
		current.newLine();
		current.append("struct _");
		current.append(className);
		current.append("Class");
		openSpacedBlock();
		
		if(classDecl.getSuperName().isEmpty()) {
			// FIXME oooh hardcoded MangoClass, that is baad.
			current.append("MangoClass super;");
		} else {
			current.append(classDecl.getSuperName());
			current.append("Class super;");
		}
		
		/* Now write all virtual functions prototypes in the class struct */
		for(FunctionDecl decl: classDecl.getFunctions()) {
			if(decl.isStatic()) continue;
			current.newLine();
			writeFuncPointer(decl);
			current.append(';');
		}
		
		closeBlock();
		current.append(';');
		current.newLine();
		current.newLine();
	}

	private void writeObjectStruct(ClassDecl classDecl, String className)
			throws IOException {
		current.newLine();
		current.append("struct _");
		current.append(className);
		openSpacedBlock();
		
		if(classDecl.getSuperName().isEmpty()) {
			current.append("MangoObject super;"); // FIXME oooh hardcoded MangoObject, that is baad.
		} else {
			current.append(classDecl.getSuperName());
			current.append(" super;");
		}
		
		for(VariableDecl decl: classDecl.getVariables()) {
			current.newLine();
			decl.accept(this);
			current.append(';');
		}
		
		closeBlock();
		current.append(';');
		current.newLine();
		current.newLine();
	}

	private void writeStructTypedef(String structName) throws IOException {
		current.append("typedef struct _");
		current.append(structName);
		current.append(" ");
		current.append(structName);
		current.append(";");
		current.newLine();
	}

	private void closeBlock() throws IOException {
		current.untab();
		current.newLine();
		current.append("}");
	}

	private void openBlock() throws IOException {
		current.newLine();
		current.append('{');
		current.tab();
	}
	
	private void openSpacedBlock() throws IOException {
		openBlock();
		current.newLine();
	}

	private void writeDesignatedInit(String contract, String implementation)
			throws IOException {
		current.newLine();
		current.append('.');
		current.append(contract);
		current.append(" = ");
		current.append(implementation);
		current.append(',');
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

		if(regularArgument.isConst()) {
			current.append("const ");
		}
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
		if(!type.isFlat()) {
			current.append(' ');
		}
		
		if(type.getRef() == null) {
			throw new Error("Unresolved Type "+type.getName()+" !!");
		}
		
		if(type.getRef() instanceof ClassDecl) {
			current.append('*');
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
		openBlock();
		block.acceptChildren(this);
		closeBlock();
	}

	@Override
	public void visit(BuiltinType builtinType) throws IOException {
		// nothing to do.
	}
	


	@Override
	public void visit(SingleLineComment slComment) throws IOException {
		current.append(" // ");
		current.append(slComment.getContent().trim());
	}

}
