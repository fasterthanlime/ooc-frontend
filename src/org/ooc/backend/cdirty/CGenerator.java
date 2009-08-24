package org.ooc.backend.cdirty;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import org.ooc.backend.Generator;
import org.ooc.backend.TabbedWriter;
import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.Add;
import org.ooc.frontend.model.AddressOf;
import org.ooc.frontend.model.Argument;
import org.ooc.frontend.model.ArrayAccess;
import org.ooc.frontend.model.ArrayLiteral;
import org.ooc.frontend.model.Assignment;
import org.ooc.frontend.model.BinaryCombination;
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
import org.ooc.frontend.model.Dereference;
import org.ooc.frontend.model.Div;
import org.ooc.frontend.model.Else;
import org.ooc.frontend.model.Expression;
import org.ooc.frontend.model.FloatLiteral;
import org.ooc.frontend.model.Foreach;
import org.ooc.frontend.model.FuncType;
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
import org.ooc.frontend.model.Node;
import org.ooc.frontend.model.NodeList;
import org.ooc.frontend.model.Not;
import org.ooc.frontend.model.NullLiteral;
import org.ooc.frontend.model.OpDecl;
import org.ooc.frontend.model.Parenthesis;
import org.ooc.frontend.model.RangeLiteral;
import org.ooc.frontend.model.RegularArgument;
import org.ooc.frontend.model.Return;
import org.ooc.frontend.model.StringLiteral;
import org.ooc.frontend.model.Sub;
import org.ooc.frontend.model.Type;
import org.ooc.frontend.model.TypeDecl;
import org.ooc.frontend.model.Use;
import org.ooc.frontend.model.ValuedReturn;
import org.ooc.frontend.model.VarArg;
import org.ooc.frontend.model.VariableAccess;
import org.ooc.frontend.model.VariableDecl;
import org.ooc.frontend.model.While;
import org.ooc.frontend.model.Assignment.Mode;
import org.ooc.frontend.model.Include.Define;
import org.ooc.frontend.model.VariableDecl.VariableDeclAtom;
import org.ooc.frontend.parser.TypeArgument;
import org.ubi.SourceReader;

public class CGenerator extends Generator implements Visitor {

	protected TabbedWriter hw;
	protected TabbedWriter cw;
	protected TabbedWriter current;

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
		
		String hName = "__" + module.getUnderName() + "__";
		current.append("#ifndef ");
		current.append(hName);
		current.newLine();
		current.append("#define ");
		current.append(hName);
		current.newLine();
		current.newLine();

		for(Include include: module.getIncludes()) {
			writeInclude(include);
		}
		
		for(Node node: module.getBody()) {
			if(node instanceof ClassDecl) {
				ClassDecl classDecl = (ClassDecl) node;
				writeStructTypedef(classDecl.getName());
				writeStructTypedef(classDecl.getName()+"Class");
			} else if(node instanceof CoverDecl) {
				writeCoverTypedef((CoverDecl) node);
			}
		}
		current.newLine();
		
		current.newLine();
		for(Import imp: module.getImports()) {
			current.append("#include <");
			current.append(imp.getModule().getFullName().replace('.', File.separatorChar));
			current.append(".h>");
			current.newLine();
		}
		
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
		
		writeInitializeModuleFunc();
		if(module.isMain()) writeDefaultMain();
		
		current = hw;
		current.newLine();
		current.newLine();
		current.append("#endif // ");
		current.append(hName);
		current.newLine();
		current.newLine();
		
	}

	private void writeDefaultMain() throws IOException {
		
		boolean got = false;
		for(Node node: module.getBody()) {
			if(!(node instanceof FunctionDecl)) continue;
			FunctionDecl decl = (FunctionDecl) node;
			if(decl.isEntryPoint()) {
				got = true;
			}
		}
		if(!got) {
			current.newLine().append("int main()");
			openBlock();
			current.newLine().append(module.getLoadFuncName()).append("();");
			closeBlock();
			current.newLine().newLine();
		}
		
	}

	protected void writeInclude(Include include) throws IOException {
		for(Define define: include.getDefines()) {
			current.newLine().append("#define ").append(define.name);
			if(define.value != null) current.append(' ').append(define.value);
		}
		current.newLine().append("#include <").append(include.getPath()).append(".h>");
		for(Define define: include.getDefines()) {
			current.newLine().append("#undef ").append(define.name).append(' ');
		}
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
	public void visit(FunctionCall functionCall) throws IOException {

		FunctionDecl decl = functionCall.getImpl();
		if(functionCall.isConstructorCall()) {
			current.append(decl.getTypeDecl().getName());
			if(functionCall.getImpl().getTypeDecl() instanceof ClassDecl) {
				current.append("_construct");
			} else{
				current.append("_new");
			}
			if(!decl.getSuffix().isEmpty()) {
				current.append('_');
				current.append(decl.getSuffix());
			}
		} else if(decl.isFromPointer()) {
			current.append(functionCall.getName());
		} else {
			decl.writeFullName(current);
		}
		
		current.append('(');
		if(functionCall.isConstructorCall() && functionCall.getImpl().getTypeDecl() instanceof ClassDecl) {
			current.append('(');
			decl.getTypeDecl().getInstanceType().accept(this);
			current.append(')');
			current.append(" this");
			if(!functionCall.getArguments().isEmpty()) current.append(", ");
		}
		writeExprList(functionCall.getArguments());
		current.append(')');
		
	}

	protected void writeExprList(NodeList<Expression> args) throws IOException {
		Iterator<Expression> iter = args.iterator();
		while(iter.hasNext()) {
			iter.next().accept(this);
			if(iter.hasNext()) current.append(", ");
		}
		
	}

	@Override
	public void visit(MemberCall memberCall) throws IOException {
		
		FunctionDecl impl = memberCall.getImpl();
		if(impl.isFromPointer()) {
			boolean isArrow = memberCall.getExpression().getType().getRef() instanceof ClassDecl;
			
			Expression expression = memberCall.getExpression();
			if(!isArrow && expression instanceof Dereference) {
				Dereference deref = (Dereference) expression;
				expression = deref.getExpression();
				isArrow = true;
			}
			expression.accept(this);
			
			if(isArrow) {
				current.append("->");
			} else {
				current.append(".");
			}
			current.append(memberCall.getName());
		} else {
			impl.writeFullName(current);
		}
		
		current.append('(');
		
		TypeDecl typeDecl = impl.getTypeDecl();
		if(!typeDecl.getInstanceType().equals(memberCall.getExpression().getType())) {
			current.append('(');
			typeDecl.getInstanceType().accept(this);
			current.append(") ");
		}
		if(!impl.isStatic() && !impl.isFromPointer()) {
			memberCall.getExpression().accept(this);
			if(!memberCall.getArguments().isEmpty()) current.append(", ");
		}
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
		assert(assignment.getMode() != Mode.DECLARATION);
		assignment.getLvalue().accept(this);
		current.append(' ');
		current.append(assignment.getSymbol());
		current.append(' ');
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
			case BIN: // C has no binary literals, write it as hex
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
	public void visit(Include include) throws IOException {}

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
	public void visit(Else else1) throws IOException {
		current.append("else ");
		openBlock();
		else1.getBody().accept(this);
		closeBlock();
	}

	@Override
	public void visit(While while1) throws IOException {
		current.append("while (");
		while1.getCondition().accept(this);
		current.append(")");
		openBlock();
		while1.getBody().accept(this);
		closeBlock();
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
		boolean isStatic = ((VariableDecl) memberAccess.getRef()).isStatic();
		
		if(isStatic) {
		
			current.append(typeDecl.getType().getMangledName()).append('_').append(memberAccess.getName());
			
		} else {
		
			boolean isArrow = (typeDecl instanceof ClassDecl);
			
			Expression expression = memberAccess.getExpression();
			if(!isArrow && expression instanceof Dereference) {
				Dereference deref = (Dereference) expression;
				expression = deref.getExpression();
				isArrow = true;
			}
			
			if(typeDecl.getType().equals(memberAccess.getExpression().getType())) {		
				expression.accept(this);
			} else {
				current.append("((");
				typeDecl.getInstanceType().accept(this);
				current.append(')');
				expression.accept(this);
				current.append(')');
			}
			
			if(isArrow) {
				current.append("->");
			} else {
				current.append('.');
			}
			
			visit((VariableAccess) memberAccess);
		
		}
		
	}
	
	@Override
	public void visit(VariableAccess variableAccess) throws IOException {

		int refLevel = variableAccess.getRef().getType().getReferenceLevel();
		//System.out.println("writing access to "+variableAccess+" ref level = "+refLevel);
		if(refLevel > 0) {
			current.append('(');
			for(int i = 0; i < refLevel; i++) {
				current.append('*');
			}
		}
		current.append(variableAccess.getRef().getExternName(variableAccess));
		if(refLevel > 0) {
			current.append(')');
		}
		
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

		if(variableDecl.isExtern()) return;
		
		
		// FIXME add const checking from the ooc side of things. Disabled C's
		// const keyword because it causes problems with class initializations
		//if(variableDecl.isConst()) current.append("const ");
		
		if(variableDecl.getType() instanceof FuncType) {
			
			FuncType funcType = (FuncType) variableDecl.getType();
			FunctionDecl funcDecl = funcType.getDecl();
			Iterator<VariableDeclAtom> iter = variableDecl.getAtoms().iterator();
			while(iter.hasNext()) {
				VariableDeclAtom atom = iter.next();
				writeSpacedType(funcDecl.getReturnType());
				current.append("(*").append(atom.getName()).append(")");
				writeFuncArgs(funcDecl);
			}
			
		} else {
		
			Type type = variableDecl.getType();
			if(!type.isArray()) {
				writeSpacedType(type);
			} else {
				current.append(type.getName()).append(' ');
			}
			
			boolean isStatic = variableDecl.isStatic();
			TypeDecl typeDecl = variableDecl.getTypeDecl();
			String typePrefix = isStatic ? typeDecl.getType().getMangledName() + "_" : "";
			
			Iterator<VariableDeclAtom> iter = variableDecl.getAtoms().iterator();
			while(iter.hasNext()) {
				VariableDeclAtom atom = iter.next();
				current.append(typePrefix).append(atom.getName());
				if(type.isArray()) for(int i = 0; i < type.getPointerLevel(); i++) {
					current.append("[]");
				}
				if(atom.getExpression() != null) {
					current.append(" = ");
					atom.getExpression().accept(this);
				}
				if(iter.hasNext()) current.append(", ");
			}
			
		}
		
	}

	@Override
	public void visit(FunctionDecl functionDecl) throws IOException {
		
		if(functionDecl.isProto()) {
			
			current = hw;
			current.newLine().append("extern ");
			writeFuncPrototype(functionDecl);
			current.append(';');
			
		} else if(!functionDecl.isExtern() && !functionDecl.isAbstract()) {
			
			current = hw;
			current.newLine();
			writeFuncPrototype(functionDecl);
			current.append(';');
		
			current = cw;
			writeFuncPrototype(functionDecl);
			openBlock();
			
			if(functionDecl.isEntryPoint()) {
				current.newLine().append(module.getLoadFuncName()).append("();");
			}
			
			for(Line line: functionDecl.getBody()) {
				line.accept(this);
			}
			
			closeSpacedBlock();
			
		}
		
	}

	protected void writeFuncPrototype(FunctionDecl functionDecl) throws IOException {
		
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
		
		current = hw;
		
		String className = classDecl.getName();
		
		writeObjectStruct(classDecl, className);
		writeClassStruct(classDecl, className);
		writeMemberFuncPrototypes(classDecl, className);
		
		/* Now implementations */
		current = cw;
		current.newLine();
		
		writeInitializeClassFunc(classDecl, className);
		writeDestroyFunc(classDecl, className);
		writeInstanceImplFuncs(classDecl, className);
		writeClassGettingFunction(classDecl);
		writeInstanceVirtualFuncs(classDecl, className);
		writeStaticFuncs(classDecl, className);
		
		current.newLine();
		
	}
	
	protected void writeMemberFuncPrototype(String className,
			FunctionDecl decl) throws IOException {
		
		writeSpacedType(decl.getReturnType());
		current.append(className).append('_').append(decl.getName());
		writeFuncArgs(decl);
		
	}

	protected void writeFuncArgs(FunctionDecl decl) throws IOException {
		writeFuncArgs(decl, false);
	}
	
	protected void writeFuncArgs(FunctionDecl decl, boolean skipFirst) throws IOException {
		
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
	
	protected void writeTypelessFuncArgs(FunctionDecl decl)
	throws IOException {

		current.append('(');
		Iterator<Argument> iter = decl.getArguments().iterator();
		while(iter.hasNext()) {
			current.append(iter.next().getName());
			if(iter.hasNext()) current.append(", ");
		}
		current.append(')');

	}

	protected void writeStaticFuncs(ClassDecl classDecl, String className)
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

	protected void writeInstanceVirtualFuncs(ClassDecl classDecl, String className)
			throws IOException {
		
		for(FunctionDecl decl: classDecl.getFunctions()) {
			
			if(decl.isStatic() || decl.isFinal()) continue;
			
			current.newLine();
			writeMemberFuncPrototype(className, decl);
			openSpacedBlock();
			
			if(!decl.getReturnType().isVoid()) current.append("return ");
			current.append("((").append(className).append("Class *)((Object *)this)->class)->");
			decl.writeSuffixedName(current);
			
			writeTypelessFuncArgs(decl);
			current.append(";");
			
			closeSpacedBlock();
			
		}
	}
	
	protected void writeBuiltinClassFuncName(String className, String returnType, String name)
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
	
	protected void writeInitializeModuleFunc() throws IOException {

		current = hw;
		current.newLine().append("void ").append(module.getLoadFuncName()).append("();");
		
		current = cw;
		current.newLine().append("void ").append(module.getLoadFuncName()).append("()");
		openBlock();
		
		current.newLine().append("static bool __done__ = false;").newLine().append("if (!__done__)");
		openBlock();
		current.newLine().append("__done__ = true;");
		
		for(Node node: module.getBody()) {
			if(node instanceof ClassDecl) {
				ClassDecl classDecl = (ClassDecl) node;
				current.newLine().append(classDecl.getInstanceType().getMangledName());
				current.append("_load();");
			}
		}
		for(Node node: module.getLoadFunc().getBody()) {
			node.accept(this);
		}
		for(Import imp: module.getImports()) {
			current.newLine().append(imp.getModule().getLoadFuncName()).append("();");
		}
		
		closeBlock();
		closeSpacedBlock();
		
	}

	protected void writeInitializeClassFunc(ClassDecl classDecl, String className)
			throws IOException {
		
		writeBuiltinClassFuncName(className, "void", "initialize");
		openBlock();
		
		if(!classDecl.getSuperName().isEmpty()) {
			current.newLine();
			current.append(classDecl.getSuperName());
			current.append("_class()->initialize((Object *) this);");
		}
		
		for(Line line: classDecl.getInitializeFunc().getBody()) {
			line.accept(this);
		}
		
		closeSpacedBlock();
		
		current.newLine().append("void ").append(className).append("_load()");
		openBlock();
		
		current.newLine().append("static bool __done__ = false;").newLine().append("if (!__done__)");
		openBlock();
		current.newLine().append("__done__ = true;");
		
		for(Line line: classDecl.getLoadFunc().getBody()) {
			line.accept(this);
		}
		
		closeBlock();
		closeSpacedBlock();
		
	}

	protected void writeDestroyFunc(ClassDecl classDecl, String className)
			throws IOException {
		
		current.newLine();
		writeBuiltinClassFuncName(className, "void", "destroy");
		openBlock();
		
		openSpacedBlock();
		current.append("const Class *super = ((Object *) this)->class->super;");
		current.newLine();
		current.append("if(super) super->destroy((Object *) this);");
		closeSpacedBlock();
		
		closeSpacedBlock();
	}

	protected void closeSpacedBlock() throws IOException {
		closeBlock();
		current.newLine();
		current.newLine();
	}

	protected void writeInstanceImplFuncs(ClassDecl classDecl, String className)
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
				current.append(" *) Class_newInstance((Class *)");
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

	protected void writeClassGettingFunction(ClassDecl classDecl) throws IOException {
		
		current.append("const Class *");
		current.append(classDecl.getName());
		current.append("_class()");
		openSpacedBlock();
		
		current.append("static const ");
		current.append(classDecl.getName());
		current.append("Class class = ");
		
		writeFuncPointers(classDecl, classDecl);
		current.append(';');
		
		current.newLine();
		current.append("return (const Class *) &class;");
		closeSpacedBlock();
	}

	protected void writeFuncPointers(ClassDecl currentClass, ClassDecl coreClass) throws IOException {
		
		openBlock();
		
		if(!currentClass.isRootClass() && !currentClass.getSuperName().isEmpty()) {
			
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
			writeDesignatedInit("initialize", "(void (*)(Object *))"+coreClass.getName()+"_initialize");
			writeDesignatedInit("destroy", "(void (*)(Object *))"+coreClass.getName()+"_destroy");
			
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

	protected void writeMemberFuncPrototypes(ClassDecl classDecl, String className)
			throws IOException {
		current.newLine();
		current.append("const Class *");
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

	protected void writeSpacedType(Type type) throws IOException {
		type.accept(this);
		if(type.isFlat()) current.append(' ');
	}

	protected Iterator<Argument> writeFuncPointer(FunctionDecl decl)
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
	
	protected void writeClassStruct(ClassDecl classDecl, String className)
		throws IOException {
		current.newLine();
		current.append("struct _");
		current.append(className);
		current.append("Class");
		openSpacedBlock();
		
		if(classDecl.isRootClass()) {
			current.append("struct _Class __super__;");
		} else {
			current.append("struct _").append(classDecl.getSuperName()).append("Class __super__;");
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

	protected void writeObjectStruct(ClassDecl classDecl, String className)
			throws IOException {
		current.newLine();
		current.append("struct _");
		current.append(className);
		openSpacedBlock();
		
		if(!classDecl.isObjectClass()) {
			current.append("struct _").append(classDecl.getSuperName()).append(" *__super__;");
		}
		
		for(VariableDecl decl: classDecl.getVariables()) {
			if(decl.isStatic()) continue;
			current.newLine();
			decl.accept(this);
			current.append(';');
		}
		
		closeBlock();
		current.append(';');
		current.newLine();
		current.newLine();
		
		for(VariableDecl decl: classDecl.getVariables()) {
			if(!decl.isStatic()) continue;
			current.newLine();
			decl.accept(this);
			current.append(';');
		}
	}

	protected void writeStructTypedef(String structName) throws IOException {
		current.newLine().append("struct _").append(structName).append(";");
		current.newLine().append("typedef struct _").append(structName)
			.append(" ").append(structName).append(";");
	}

	protected void closeBlock() throws IOException {
		current.untab();
		current.newLine();
		current.append("}");
	}

	protected void openBlock() throws IOException {
		current.newLine();
		current.append('{');
		current.tab();
	}
	
	protected void openSpacedBlock() throws IOException {
		openBlock();
		current.newLine();
	}

	protected void writeDesignatedInit(String contract, String implementation)
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
		// we don't need to struct it again, it would confuse the C compiler
		if(!cover.isAddon() && !cover.isExtern() && cover.getFromType() == null) {
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
		}
		
		for(FunctionDecl decl: cover.getFunctions()) {
			decl.accept(this);
			current.newLine();
		}
	}

	protected void writeCoverTypedef(CoverDecl cover) throws IOException {
		
		if(!cover.isAddon() && !cover.isExtern()) {
			Type fromType = cover.getFromType();
			if(fromType == null) {
				current.newLine();
				current.append("typedef struct _");
				current.append(cover.getName());
				current.append(' ');
				current.append(cover.getName());
				current.append(';');
			} else {
				current.newLine();
				current.append("typedef ");
				writeSpacedType(fromType.getGroundType());
				current.append(cover.getName());
				current.append(';');
			}
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
		Type type = regularArgument.getType();
		if(type.isArray()) {
			current.append(type.getName()).append(' ').append(regularArgument.getName());
			for(int i = 0; i < type.getPointerLevel(); i++) {
				current.append("[]");
			}
		} else {
			writeSpacedType(type);
			current.append(regularArgument.getName());
		}
		
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
		
		int level = type.getPointerLevel() + type.getReferenceLevel();
		for(int i = 0; i < level; i++) {
			if(type.isArray()) current.append("[]");
			else current.append('*');
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

	@Override
	public void visit(AddressOf addressOf) throws IOException {
		current.append("&(");
		addressOf.getExpression().accept(this);
		current.append(')');
	}

	@Override
	public void visit(Dereference dereference) throws IOException {
		current.append("(*");
		dereference.getExpression().accept(this);
		current.append(')');
	}

	@Override
	public void visit(OpDecl opDecl) throws IOException {
		opDecl.getFunc().accept(this);
	}

	@Override
	public void visit(Import import1) throws IOException {
		
	}
	
	@Override
	public void visit(ArrayLiteral arrayLiteral) throws IOException {
		current.append('{');
		Iterator<Expression> iter = arrayLiteral.getElements().iterator();
		while(iter.hasNext()) {
			iter.next().accept(this);
			if(iter.hasNext()) current.append(", ");
		}
		current.append('}');
	}

	@Override
	public void visit(Use use) throws IOException {
		
	}

	@Override
	public void visit(BinaryCombination binaryCombination) throws IOException {
		binaryCombination.getLeft().accept(this);
		current.append(' ').append(binaryCombination.getOpString()).append(' ');
		binaryCombination.getRight().accept(this);
	}

}
