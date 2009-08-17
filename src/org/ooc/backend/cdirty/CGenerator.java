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
import org.ooc.frontend.model.Cast;
import org.ooc.frontend.model.CharLiteral;
import org.ooc.frontend.model.ClassDecl;
import org.ooc.frontend.model.Comment;
import org.ooc.frontend.model.Compare;
import org.ooc.frontend.model.ControlStatement;
import org.ooc.frontend.model.CoverDecl;
import org.ooc.frontend.model.Div;
import org.ooc.frontend.model.Expression;
import org.ooc.frontend.model.FloatLiteral;
import org.ooc.frontend.model.Foreach;
import org.ooc.frontend.model.FunctionCall;
import org.ooc.frontend.model.FunctionDecl;
import org.ooc.frontend.model.If;
import org.ooc.frontend.model.Import;
import org.ooc.frontend.model.Include;
import org.ooc.frontend.model.Instantiation;
import org.ooc.frontend.model.IntLiteral;
import org.ooc.frontend.model.Line;
import org.ooc.frontend.model.MemberAccess;
import org.ooc.frontend.model.MemberArgument;
import org.ooc.frontend.model.MemberAssignArgument;
import org.ooc.frontend.model.MemberCall;
import org.ooc.frontend.model.Mod;
import org.ooc.frontend.model.Module;
import org.ooc.frontend.model.Mul;
import org.ooc.frontend.model.MultiLineComment;
import org.ooc.frontend.model.Node;
import org.ooc.frontend.model.NodeList;
import org.ooc.frontend.model.Not;
import org.ooc.frontend.model.NullLiteral;
import org.ooc.frontend.model.Parenthesis;
import org.ooc.frontend.model.RangeLiteral;
import org.ooc.frontend.model.RegularArgument;
import org.ooc.frontend.model.Return;
import org.ooc.frontend.model.SingleLineComment;
import org.ooc.frontend.model.StringLiteral;
import org.ooc.frontend.model.Sub;
import org.ooc.frontend.model.Type;
import org.ooc.frontend.model.TypeDecl;
import org.ooc.frontend.model.ValuedReturn;
import org.ooc.frontend.model.VarArg;
import org.ooc.frontend.model.VariableAccess;
import org.ooc.frontend.model.VariableDecl;
import org.ooc.frontend.model.While;
import org.ooc.frontend.model.VariableDecl.VariableDeclAtom;
import org.ooc.frontend.parser.TypeArgument;
import org.ubi.SourceReader;

public class CGenerator extends Generator implements Visitor {

	private TabbedWriter hw;
	private TabbedWriter cw;
	private TabbedWriter current;

	public CGenerator(File outPath, Module module) throws IOException {
		super(outPath, module);
		String basePath = module.getFullName().replace('.', File.separatorChar);
		File hFile = new File(outPath, basePath + ".h");
		hFile.getParentFile().mkdirs();
		this.hw = new TabbedWriter(new FileWriter(hFile));
		File cFile = new File(outPath, basePath + ".c");
		this.cw = new TabbedWriter(new FileWriter(cFile));
		this.current = hw;
	}

	@Override
	public void generate() throws IOException {
		module.accept(this);
		hw.close();
		cw.close();
	}

	@Override
	public void visit(Module module) throws IOException {
		
		current = hw;
		current.append("/* ");
		current.append(module.getFullName());
		current.append(" header file, generated with ooc */");
		current.newLine();
		
		String hName = "__" + module.getFullName().replaceAll("[^a-zA-Z0-9_]", "_") + "__";
		current.append("#ifndef ");
		current.append(hName);
		current.newLine();
		current.append("#define ");
		current.append(hName);
		current.newLine();
		current.newLine();
		
		for(Node node: module.getBody()) {
			if(!(node instanceof ClassDecl)) continue;
			ClassDecl classDecl = (ClassDecl) node;
			writeStructTypedef(classDecl.getName());
			writeStructTypedef(classDecl.getName()+"Class");
		}
		current.newLine();
		
		// FIXME of course this is a dirty hack because this devbranch
		// is missing the whole fancy cmdline frontend with sdk search etc.
		current.append("#include <mango/mangoobject.h>\n");
		for(Include include: module.getIncludes()) {
			current.append("#include <");
			current.append(include.getPath());
			current.append(".h>");
			current.newLine();
		}
		for(Import imp: module.getImports()) {
			current.append("#include \"");
			current.append(imp.getModule().getFullName().replace('.', File.separatorChar));
			current.append(".h\"");
			current.newLine();
		}
		current.newLine();
		
		current = cw;
		current.append("/* ");
		current.append(module.getFullName());
		current.append(" source file, generated with ooc */");
		current.newLine();
		
		current.append("#include \"");
		current.append(module.getSimpleName());
		current.append(".h\"");
		current.newLine();
		
		current = cw;
		module.acceptChildren(this);
		
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
	public void visit(Compare compare) throws IOException {

		compare.getLeft().accept(this);
		switch(compare.getCompareType()) {
			case GREATER: current.append(" > "); break;
			case GREATER_OR_EQUAL: current.append(" >= "); break;
			case LESSER: current.append(" < "); break;
			case LESSER_OR_EQUAL: current.append(" <= "); break;
			case EQUAL: current.append(" == "); break;
			case NOT_EQUAL: current.append(" != "); break;
		}
		compare.getRight().accept(this);
		
	}

	@Override
	public void visit(MultiLineComment comment) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(FunctionCall functionCall) throws IOException {

		FunctionDecl decl = functionCall.getImpl();
		if(functionCall.isConstructorCall()) {
			current.append(decl.getTypeDecl().getName());
			current.append("_construct");
			if(!decl.getSuffix().isEmpty()) {
				current.append('_');
				current.append(decl.getSuffix());
			}
		} else {
			decl.writeFullName(current);
		}
		
		current.append('(');
		if(functionCall.isConstructorCall()) {
			current.append('(');
			decl.getTypeDecl().getInstanceType().accept(this);
			current.append(')');
			current.append(" this");
			if(!functionCall.getArguments().isEmpty()) current.append(", ");
		}
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
		
		current.append(memberCall.getImpl().getTypeDecl().getName());
		current.append('_');
		current.append(memberCall.getName());
		current.append('(');
		
		TypeDecl typeDecl = memberCall.getImpl().getTypeDecl();
		if(!typeDecl.getInstanceType().equals(memberCall.getExpression().getType())) {
			current.append('(');
			typeDecl.getInstanceType().accept(this);
			current.append(") ");
		}
		memberCall.getExpression().accept(this);
		if(!memberCall.getArguments().isEmpty()) current.append(", ");
		writeExprList(memberCall.getArguments());
		
		current.append(')');
		
	}

	@Override
	public void visit(Instantiation inst) throws IOException {

		FunctionDecl decl = inst.getImpl();
		decl.writeFullName(current);
		current.append('(');
		writeExprList(inst.getArguments());
		current.append(')');
		
	}

	@Override
	public void visit(Parenthesis parenthesis) throws IOException {

		current.append('(');
		parenthesis.getExpression().accept(this);
		current.append(')');
		
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
	public void visit(IntLiteral numberLiteral) throws IOException {

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
		current.append(boolLiteral.getValue() ? "true" : "false");
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
		
		current.append("if");
		if(if1.getCondition() instanceof Parenthesis) {
			if1.getCondition().accept(this);
		} else {
			current.append('(');
			if1.getCondition().accept(this);
			current.append(')');
		}
		
		openBlock();
		if1.getBody().accept(this);
		closeBlock();
		
	}

	@Override
	public void visit(While while1) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(Foreach foreach) throws IOException {

		if(foreach.getCollection() instanceof RangeLiteral) {
			RangeLiteral range = (RangeLiteral) foreach.getCollection();
			current.append("for (");
			foreach.getVariable().accept(this);
			current.append(" = ");
			range.getLower().accept(this);
			current.append("; ").append(foreach.getVariable().getName()).append(" < ");
			range.getUpper().accept(this);
			current.append("; ").append(foreach.getVariable().getName()).append("++");
			current.append(")");
			openBlock();
			foreach.getBody().accept(this);
			closeBlock();
		} else { 
			throw new UnsupportedOperationException("Iterating over.. not a Range but a "
					+foreach.getCollection().getType());
		}
		
	}

	@Override
	public void visit(MemberAccess memberAccess) throws IOException {

		TypeDecl typeDecl = memberAccess.getRef().getTypeDecl();
		if(typeDecl.getType().equals(memberAccess.getExpression().getType())) {		
			memberAccess.getExpression().accept(this);
		} else {
			current.append("((");
			typeDecl.getInstanceType().accept(this);
			current.append(')');
			memberAccess.getExpression().accept(this);
			current.append(')');
		}
		if(typeDecl instanceof ClassDecl) {
			current.append("->");
		} else {
			current.append('.');
		}
		current.append(memberAccess.getName());
		
	}
	
	@Override
	public void visit(VariableAccess variableAccess) throws IOException {
		current.append(variableAccess.getName());
	}

	@Override
	public void visit(ArrayAccess arrayAccess) throws IOException {
		arrayAccess.getVariable().accept(this);
		current.append('[');
		arrayAccess.getIndex().accept(this);
		current.append(']');
	}

	@Override
	public void visit(VariableDecl variableDecl) throws IOException {

		System.out.println("Writing variable decl "+variableDecl);
		
		if(variableDecl.isExtern()) return;
		writeSpacedType(variableDecl.getType());
		
		Iterator<VariableDeclAtom> iter = variableDecl.getAtoms().iterator();
		while(iter.hasNext()) {
			VariableDeclAtom atom = iter.next();
			current.append(atom.getName());
			if(atom.getExpression() != null) {
				current.append(" = ");
				atom.getExpression().accept(this);
			}
			if(iter.hasNext()) current.append(", ");
		}
		
	}

	@Override
	public void visit(FunctionDecl functionDecl) throws IOException {
		
		if(!functionDecl.isExtern() && !functionDecl.isAbstract()) {
			
			System.out.println("Writing function "+functionDecl.getProtoRepr());
		
			current = hw;
			current.newLine();
			writeFuncPrototype(functionDecl);
			current.append(';');
		
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
		
		writeSpacedType(functionDecl.getReturnType());
		functionDecl.writeFullName(current);
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
		
		writeObjectStruct(classDecl, className);
		writeClassStruct(classDecl, className);
		writeMemberFuncPrototypes(classDecl, className);
		
		/* Now implementations */
		current = cw;
		current.newLine();
		
		writeInitializeFunc(classDecl, className);
		writeDestroyFunc(classDecl, className);
		writeInstanceImplFuncs(classDecl, className);
		writeClassGettingFunction(classDecl);
		writeInstanceVirtualFuncs(classDecl, className);
		writeStaticFuncs(classDecl, className);
		
		current.newLine();
		
	}
	
	private void writeMemberFuncPrototype(String className,
			FunctionDecl decl) throws IOException {
		
		writeSpacedType(decl.getReturnType());
		current.append(className).append('_').append(decl.getName());
		writeFuncArgs(decl);
		
	}

	private void writeFuncArgs(FunctionDecl decl) throws IOException {
		writeFuncArgs(decl, false);
	}
	
	private void writeFuncArgs(FunctionDecl decl, boolean skipFirst) throws IOException {
		
		current.append('(');
		Iterator<Argument> iter = decl.getArguments().iterator();
		if(iter.hasNext()) { // of course, no point of doing all this if we have no arguments
			if(skipFirst) iter.next(); // especially that.
			while(iter.hasNext()) {
				iter.next().accept(this);
				if(iter.hasNext()) current.append(", ");
			}
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

	private void writeStaticFuncs(ClassDecl classDecl, String className)
			throws IOException {
		
		for(FunctionDecl decl: classDecl.getFunctions()) {
			
			if(!decl.isStatic()) continue;
			
			current.newLine();
			writeMemberFuncPrototype(className, decl);
			openBlock();
			decl.getBody().accept(this);
			closeSpacedBlock();
			
		}
	}

	private void writeInstanceVirtualFuncs(ClassDecl classDecl, String className)
			throws IOException {
		
		for(FunctionDecl decl: classDecl.getFunctions()) {
			
			if(decl.isStatic() || decl.isFinal()) continue;
			
			current.newLine();
			writeMemberFuncPrototype(className, decl);
			openSpacedBlock();
			
			if(!decl.getReturnType().isVoid()) current.append("return ");
			current.append("((").append(className).append("Class *)((MangoObject *)this)->class)->");
			decl.writeSuffixedName(current);
			
			writeTypelessFuncArgs(decl);
			current.append(";");
			
			closeSpacedBlock();
			
		}
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
			current.append("_class()->initialize((MangoObject *) this);");
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
			if(!decl.isFinal()) current.append("static ");
			writeSpacedType(decl.getReturnType());
			decl.writeFullName(current);
			if(!decl.isFinal()) current.append("_impl");
		
			writeFuncArgs(decl, decl.isConstructor()); // if is constuctor, don't write the first arg
			
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
				writeTypelessFuncArgs(decl);
				current.append(";");
				current.newLine();
				current.append("return this;");
			} else {
				decl.getBody().accept(this);
			}			
			closeSpacedBlock();
			
			/* Special case: constructor, now write the corresponding construct function */
			if(decl.isConstructor()) {
				// And now, write the corresponding construct function
				current.append("void ");
				current.append(className);
				current.append("_construct");
				if(!decl.getSuffix().isEmpty()) {
					current.append('_');
					current.append(decl.getSuffix());
				}
				writeFuncArgs(decl);
				openBlock();
				for(Line line: decl.getBody()) {
					line.accept(this);
				}
				closeSpacedBlock();
			}
		}
		
	}

	private void writeClassGettingFunction(ClassDecl classDecl) throws IOException {
		
		current.append("const MangoClass *");
		current.append(classDecl.getName());
		current.append("_class()");
		openSpacedBlock();
		
		current.append("static const ");
		current.append(classDecl.getName());
		current.append("Class class = ");
		
		writeFuncPointers(classDecl, classDecl);
		current.append(';');
		
		current.newLine();
		current.append("return (const MangoClass *) &class;");
		closeSpacedBlock();
	}

	private void writeFuncPointers(ClassDecl currentClass, ClassDecl coreClass) throws IOException {
		
		openBlock();
		
		if(currentClass.getSuperRef() != null) {
			
			writeFuncPointers(currentClass.getSuperRef(), coreClass);
			
		} else {
		
			/* class attributes */
			openBlock();
			
			/* size of class */
			current.newLine();
			current.append(".size = ");
			current.append("sizeof(");
			current.append(coreClass.getName());
			current.append("),");
			
			/* name of class */
			current.newLine();
			current.append(".name = ");
			current.append('"');
			current.append(coreClass.getName());
			current.append("\",");
			
			/* initialize, destroy */
			writeDesignatedInit("initialize", "(void (*)(MangoObject *))"+coreClass.getName()+"_initialize");
			writeDesignatedInit("destroy", "(void (*)(MangoObject *))"+coreClass.getName()+"_destroy");
			
			closeBlock();
			current.append(',');
		
		}
		
		for(FunctionDecl decl: currentClass.getFunctions()) {
			if(decl.isStatic()) continue;
			if(decl.isConstructor()) continue;
			
			if(decl.isFinal()) writeDesignatedInit(decl.getName(), currentClass.getName() + "_" + decl.getName());
			else writeDesignatedInit(decl.getName(), currentClass.getName() + "_" + decl.getName() + "_impl");
			
		}
		
		closeBlock();
		if(coreClass != currentClass) current.append(',');
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
			decl.writeFullName(current);
			writeFuncArgs(decl, decl.isConstructor());
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
				writeFuncArgs(decl);
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
			if(decl.isStatic() || decl.isConstructor()) continue;
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

		// addons only add functions to an already imported cover, so
		// we don't need to struct/typedef' it again, it would confuse
		// the C compiler
		if(!cover.isAddon()) {
			Type fromType = cover.getFromType();
			if(fromType == null) {
				current.append("typedef struct _");
				current.append(cover.getName());
				current.append(' ');
				current.append(cover.getName());
				current.append(';');
				current.newLine();
				current.newLine();
				
				current.append("struct _");
				current.append(cover.getName());
				current.append(' ');
				openBlock();
				for(VariableDecl decl: cover.getVariables()) {
					current.newLine();
					decl.accept(this);
					current.append(';');
				}
				closeBlock();
				current.append(';');
				current.newLine();
			} else {
				current.append("typedef ");
				writeSpacedType(fromType);
				current.append(cover.getName());
				current.append(';');
				current.newLine();
			}
		}
		
		for(FunctionDecl decl: cover.getFunctions()) {
			decl.accept(this);
		}
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

	@Override
	public void visit(FloatLiteral floatLiteral) throws IOException {
		current.append(Double.toString(floatLiteral.getValue()));
	}

	@Override
	public void visit(VariableDeclAtom variableDeclAtom) throws IOException {
		// do nothing.
	}
	
	@Override
	public void visit(Cast cast) throws IOException {
		current.append("((");
		cast.getType().accept(this);
		current.append(") ");
		cast.getExpression().accept(this);
		current.append(")");
	}

}
