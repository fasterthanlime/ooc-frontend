package org.ooc.backend.cdirty;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.ooc.backend.Generator;
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
import org.ooc.frontend.model.Declaration;
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
import org.ooc.frontend.model.TypeParam;
import org.ooc.frontend.model.Use;
import org.ooc.frontend.model.ValuedReturn;
import org.ooc.frontend.model.VarArg;
import org.ooc.frontend.model.VariableAccess;
import org.ooc.frontend.model.VariableDecl;
import org.ooc.frontend.model.While;
import org.ooc.frontend.model.Include.Define;
import org.ooc.frontend.model.VariableDecl.VariableDeclAtom;
import org.ooc.frontend.parser.TypeArgument;
import org.ooc.middle.OocCompilationError;
import org.ubi.SourceReader;

public class CGenerator extends Generator implements Visitor {

	protected AwesomeWriter hw;
	protected AwesomeWriter cw;
	protected AwesomeWriter current;

	public CGenerator(File outPath, Module module) throws IOException {
		super(outPath, module);
		String basePath = module.getFullName().replace('.', File.separatorChar);
		File hFile = new File(outPath, basePath + ".h");
		hFile.getParentFile().mkdirs();
		this.hw = new AwesomeWriter(new FileWriter(hFile));
		File cFile = new File(outPath, basePath + ".c");
		this.cw = new AwesomeWriter(new FileWriter(cFile));
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
		current.app("/* ");
		current.app(module.getFullName());
		current.app(" header file, generated with ooc */");
		current.nl();
		
		String hName = "__" + module.getUnderName() + "__";
		current.app("#ifndef ");
		current.app(hName);
		current.nl();
		current.app("#define ");
		current.app(hName);
		current.nl();
		current.nl();

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
		current.nl();
		
		current.nl();
		for(Import imp: module.getImports()) {
			current.app("#include <");
			current.app(imp.getModule().getFullName().replace('.', File.separatorChar));
			current.app(".h>");
			current.nl();
		}
		
		current = cw;
		current.app("/* ");
		current.app(module.getFullName());
		current.app(" source file, generated with ooc */");
		current.nl();
		
		current.app("#include \"");
		current.app(module.getSimpleName());
		current.app(".h\"");
		current.nl();
		
		current = cw;
		module.acceptChildren(this);
		
		writeInitializeModuleFunc();
		if(module.isMain()) writeDefaultMain();
		
		current = hw;
		current.nl().nl().app("#endif // ").app(hName).nl().nl();
		
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
			current.nl().app("int main()").openBlock();
			current.nl().app(module.getLoadFuncName()).app("();");
			current.closeBlock().nl().nl();
		}
		
	}

	protected void writeInclude(Include include) throws IOException {
		for(Define define: include.getDefines()) {
			current.nl().app("#define ").app(define.name);
			if(define.value != null) current.app(' ').app(define.value);
		}
		current.nl().app("#include <").app(include.getPath()).app(".h>");
		for(Define define: include.getDefines()) {
			current.nl().app("#undef ").app(define.name).app(' ');
		}
	}

	@Override
	public void visit(Add add) throws IOException {
		add.getLeft().accept(this);
		current.app(" + ");
		add.getRight().accept(this);		
	}

	@Override
	public void visit(Mul mul) throws IOException {
		mul.getLeft().accept(this);
		current.app(" * ");
		mul.getRight().accept(this);		
	}

	@Override
	public void visit(Sub sub) throws IOException {
		sub.getLeft().accept(this);
		current.app(" - ");
		sub.getRight().accept(this);		
	}

	@Override
	public void visit(Div div) throws IOException {
		div.getLeft().accept(this);
		current.app(" / ");
		div.getRight().accept(this);
	}

	@Override
	public void visit(Not not) throws IOException {
		current.app('!');
		not.getExpression().accept(this);		
	}
	
	@Override
	public void visit(Mod mod) throws IOException {
		mod.getLeft().accept(this);
		current.app(" % ");
		mod.getRight().accept(this);
	}
	
	@Override
	public void visit(Compare compare) throws IOException {

		compare.getLeft().accept(this);
		switch(compare.getCompareType()) {
			case GREATER: current.app(" > "); break;
			case GREATER_OR_EQUAL: current.app(" >= "); break;
			case LESSER: current.app(" < "); break;
			case LESSER_OR_EQUAL: current.app(" <= "); break;
			case EQUAL: current.app(" == "); break;
			case NOT_EQUAL: current.app(" != "); break;
		}
		compare.getRight().accept(this);
		
	}

	@Override
	public void visit(FunctionCall functionCall) throws IOException {

		FunctionDecl decl = functionCall.getImpl();
		if(functionCall.isConstructorCall()) {
			current.app(decl.getTypeDecl().getName());
			if(functionCall.getImpl().getTypeDecl() instanceof ClassDecl) {
				current.app("_construct");
			} else{
				current.app("_new");
			}
			if(!decl.getSuffix().isEmpty()) {
				current.app('_');
				current.app(decl.getSuffix());
			}
		} else if(decl.isFromPointer()) {
			current.app(functionCall.getName());
		} else {
			decl.writeFullName(current);
		}
		
		FunctionDecl impl = functionCall.getImpl();
		NodeList<Expression> args = functionCall.getArguments();
		
		current.app('(');
		if(functionCall.isConstructorCall() && impl.getTypeDecl() instanceof ClassDecl) {
			current.app('(');
			decl.getTypeDecl().getInstanceType().accept(this);
			current.app(')');
			current.app(" this");
			if(!args.isEmpty()) current.app(", ");
		}
		writeCallArgs(args, impl);
		current.app(')');
		
	}

	protected void writeCallArgs(NodeList<Expression> callArgs, FunctionDecl impl) throws IOException {
		List<TypeParam> typeParams = impl.getTypeParams();
		if(typeParams.isEmpty()) {
			Iterator<Expression> iter = callArgs.iterator();
			while(iter.hasNext()) {
				iter.next().accept(this);
				if(iter.hasNext()) current.app(", ");
			}
		} else {
			writeGenericCallArgs(callArgs, impl, typeParams);
		}
	}

	private void writeGenericCallArgs(NodeList<Expression> callArgs,
			FunctionDecl impl, List<TypeParam> typeParams) throws IOException {
		
		int i = 0;
		// FIXME must write a list of expressions given by FunctionCall instead
		for(TypeParam param: typeParams) {
			current.append(callArgs.get(i).getType().getName()+"_class(), ");
			i++;
		}
		
		i = 0;
		Iterator<Expression> iter = callArgs.iterator();
		NodeList<Argument> implArgs = impl.getArguments();
		while(iter.hasNext()) {
			Expression expr = iter.next();
			Argument arg = implArgs.get(i);
			for(TypeParam param: typeParams) {
				System.out.println("arg.getType().getName() = "+arg.getType().getName()
					+", param.getName() = "+param.getName());
				if(arg.getType().getName().equals(param.getName())) {
					current.app("&");
				}
			}
			expr.accept(this);
			if(iter.hasNext()) current.app(", ");
			i++;
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
				current.app("->");
			} else {
				current.app(".");
			}
			current.app(memberCall.getName());
		} else {
			impl.writeFullName(current);
		}
		
		current.app('(');
		
		TypeDecl typeDecl = impl.getTypeDecl();
		if(!typeDecl.getInstanceType().equals(memberCall.getExpression().getType())) {
			current.app('(');
			typeDecl.getInstanceType().accept(this);
			current.app(") ");
		}
		if(!impl.isStatic() && !impl.isFromPointer()) {
			memberCall.getExpression().accept(this);
			if(!memberCall.getArguments().isEmpty()) current.app(", ");
		}
		writeCallArgs(memberCall.getArguments(), impl);
		
		current.app(')');
		
	}

	@Override
	public void visit(Instantiation inst) throws IOException {
		FunctionDecl impl = inst.getImpl();
		impl.writeFullName(current);
		current.app('(');
		writeCallArgs(inst.getArguments(), impl);
		current.app(')');
	}

	@Override
	public void visit(Parenthesis parenthesis) throws IOException {
		current.app('(');
		parenthesis.getExpression().accept(this);
		current.app(')');
	}

	@Override
	public void visit(Assignment assignment) throws IOException {
		assignment.getLvalue().accept(this);
		current.app(' ').app(assignment.getSymbol()).app(' ');
		assignment.getRvalue().accept(this);
	}

	@Override
	public void visit(ValuedReturn return1) throws IOException {
		current.app("return ");
		return1.getExpression().accept(this);
	}
	
	@Override
	public void visit(Return return1) throws IOException {
		current.app("return");
	}

	@Override
	public void visit(NullLiteral nullLiteral) throws IOException {
		current.app("NULL");
	}

	@Override
	public void visit(IntLiteral numberLiteral) throws IOException {

		switch(numberLiteral.getFormat()) {
			case HEX:
			case BIN: // C has no binary literals, write it as hex
				current.app("0x");
				current.app(Long.toHexString(numberLiteral.getValue()));
				break;
			case OCT:
				current.app('0');
				current.app(Long.toOctalString(numberLiteral.getValue()));
				break;
			default:
				current.app(String.valueOf(numberLiteral.getValue()));
		}
	}

	@Override
	public void visit(StringLiteral stringLiteral) throws IOException {
		current.app('"').app(SourceReader.spelled(stringLiteral.getValue())).app('"');
	}

	@Override
	public void visit(RangeLiteral rangeLiteral) throws IOException {
		throw new OocCompilationError(rangeLiteral, module,
				"Using a range literal outside a foreach is not supported yet.");
	}

	@Override
	public void visit(BoolLiteral boolLiteral) throws IOException {
		current.app(boolLiteral.getValue() ? "true" : "false");
	}

	@Override
	public void visit(CharLiteral charLiteral) throws IOException {
		current.app('\'').app(SourceReader.spelled(charLiteral.getValue())).app('\'');		
	}

	@Override
	public void visit(Line line) throws IOException {
		current.nl();
		line.getStatement().accept(this);
		if(!(line.getStatement() instanceof ControlStatement)
				&& !(line instanceof Comment)) {
			current.app(';');
		}
	}

	@Override
	public void visit(Include include) throws IOException {}

	@Override
	public void visit(If if1) throws IOException {
		current.app("if (");
		if1.getCondition().accept(this);
		current.app(")");
		NodeList<Line> body = if1.getBody();
		current.openBlock();
		body.accept(this);
		current.closeBlock();
	}
	
	@Override
	public void visit(Else else1) throws IOException {
		current.app("else ");
		NodeList<Line> body = else1.getBody();
		if(body.size() == 1 && (body.get(0).getStatement() instanceof If)) {
			body.get(0).getStatement().accept(this);
		} else {
			current.openBlock();
			body.accept(this);
			current.closeBlock();
		}
	}

	@Override
	public void visit(While while1) throws IOException {
		current.app("while (");
		while1.getCondition().accept(this);
		current.app(")").openBlock();
		while1.getBody().accept(this);
		current.closeBlock();
	}

	@Override
	public void visit(Foreach foreach) throws IOException {

		if(foreach.getCollection() instanceof RangeLiteral) {
			RangeLiteral range = (RangeLiteral) foreach.getCollection();
			current.app("for (");
			foreach.getVariable().accept(this);
			current.app(" = ");
			range.getLower().accept(this);
			current.app("; ");
			foreach.getVariable().accept(this);
			current.app(" < ");
			range.getUpper().accept(this);
			current.app("; ");
			foreach.getVariable().accept(this);
			current.app("++").app(")").openBlock();
			foreach.getBody().accept(this);
			current.closeBlock();
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
		
			current.app(typeDecl.getType().getMangledName()).app('_').app(memberAccess.getName());
			
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
				current.app("((");
				typeDecl.getInstanceType().accept(this);
				current.app(')');
				expression.accept(this);
				current.app(')');
			}
			
			if(isArrow) {
				current.app("->");
			} else {
				current.app('.');
			}
			
			visit((VariableAccess) memberAccess);
		
		}
		
	}
	
	@Override
	public void visit(VariableAccess variableAccess) throws IOException {
		writeVarAccess(variableAccess, true);
	}

	private void writeVarAccess(VariableAccess variableAccess,
			boolean doTypeParams) throws IOException {
		int refLevel = variableAccess.getRef().getType().getReferenceLevel();
		//System.out.println("writing access to "+variableAccess+" ref level = "+refLevel);
		
		if(doTypeParams) {
			Declaration ref = variableAccess.getRef().getType().getRef();
			if(ref instanceof TypeParam) refLevel++;
		}
		
		if(refLevel > 0) {
			current.app('(');
			for(int i = 0; i < refLevel; i++) {
				current.app('*');
			}
		}
		current.app(variableAccess.getRef().getExternName(variableAccess));
		if(refLevel > 0) {
			current.app(')');
		}
	}

	@Override
	public void visit(ArrayAccess arrayAccess) throws IOException {
		arrayAccess.getVariable().accept(this);
		current.app('[');
		arrayAccess.getIndex().accept(this);
		current.app(']');
	}

	@Override
	public void visit(VariableDecl variableDecl) throws IOException {

		if(variableDecl.isExtern()) return;
		
		
		// FIXME add const checking from the ooc side of things. Disabled C's
		// const keyword because it causes problems with class initializations
		//if(variableDecl.isConst()) current.app("const ");
		
		if(variableDecl.getType() instanceof FuncType) {
			
			FuncType funcType = (FuncType) variableDecl.getType();
			FunctionDecl funcDecl = funcType.getDecl();
			Iterator<VariableDeclAtom> iter = variableDecl.getAtoms().iterator();
			while(iter.hasNext()) {
				VariableDeclAtom atom = iter.next();
				writeSpacedType(funcDecl.getReturnType());
				current.app("(*").app(atom.getName()).app(")");
				writeFuncArgs(funcDecl);
			}
			
		} else {
			
			boolean isStatic = variableDecl.isStatic();
			TypeDecl typeDecl = variableDecl.getTypeDecl();
			if(isStatic && (typeDecl == null)) current.append("static ");
			
			Type type = variableDecl.getType();
			if(!type.isArray()) {
				writeSpacedType(type);
			} else {
				current.app(type.getName()).app(' ');
			}

			String typePrefix = isStatic && (typeDecl != null) ?
					(typeDecl.getType().getMangledName()) + "_" : "";
			
			Iterator<VariableDeclAtom> iter = variableDecl.getAtoms().iterator();
			while(iter.hasNext()) {
				VariableDeclAtom atom = iter.next();
				current.app(typePrefix).app(atom.getName());
				if(type.isArray()) for(int i = 0; i < type.getPointerLevel(); i++) {
					current.app("[]");
				}
				if(atom.getExpression() != null) {
					current.app(" = ");
					atom.getExpression().accept(this);
				}
				if(iter.hasNext()) current.app(", ");
			}
			
		}
		
	}

	@Override
	public void visit(FunctionDecl functionDecl) throws IOException {
		
		if(functionDecl.isProto()) {
			current = hw;
			current.nl().app("extern ");
			writeFuncPrototype(functionDecl);
			current.app(';');
		} else if(!functionDecl.isExtern() && !functionDecl.isAbstract()) {
			current = hw;
			current.nl();
			writeFuncPrototype(functionDecl);
			current.app(';');
		
			current = cw;
			writeFuncPrototype(functionDecl);
			current.openBlock();
			
			if(functionDecl.isEntryPoint()) {
				current.nl().app(module.getLoadFuncName()).app("();");
			}
			
			for(Line line: functionDecl.getBody()) line.accept(this);
			current.closeSpacedBlock();
		}
		
	}

	protected void writeFuncPrototype(FunctionDecl functionDecl) throws IOException {
		
		writeSpacedType(functionDecl.getReturnType());
		functionDecl.writeFullName(current);
		current.app('(');
		
		boolean isFirst = true;
		for(TypeParam param: functionDecl.getTypeParams()) {
			if(!isFirst) current.app(", ");
			isFirst = false;
			param.getArgument().accept(this);
		}
		
		for(Argument arg: functionDecl.getArguments()) {
			if(!isFirst) current.app(", ");
			isFirst = false;
			arg.accept(this);
		}
		
		current.app(')');
		
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
		current.nl();
		
		writeInitializeClassFunc(classDecl, className);
		writeDestroyFunc(classDecl, className);
		writeInstanceImplFuncs(classDecl, className);
		writeClassGettingFunction(classDecl);
		writeInstanceVirtualFuncs(classDecl, className);
		writeStaticFuncs(classDecl, className);
		
		current.nl();
		
	}
	
	protected void writeMemberFuncPrototype(String className,
			FunctionDecl decl) throws IOException {
		
		writeSpacedType(decl.getReturnType());
		current.app(className).app('_').app(decl.getName());
		writeFuncArgs(decl);
		
	}

	protected void writeFuncArgs(FunctionDecl decl) throws IOException {
		writeFuncArgs(decl, false);
	}
	
	protected void writeFuncArgs(FunctionDecl decl, boolean skipFirst) throws IOException {
		
		current.app('(');
		Iterator<Argument> iter = decl.getArguments().iterator();
		if(iter.hasNext()) { // of course, no point of doing all this if we have no arguments
			if(skipFirst) iter.next(); // especially that.
			while(iter.hasNext()) {
				iter.next().accept(this);
				if(iter.hasNext()) current.app(", ");
			}
		}
		current.app(')');
		
	}
	
	protected void writeTypelessFuncArgs(FunctionDecl decl)
	throws IOException {

		current.app('(');
		Iterator<Argument> iter = decl.getArguments().iterator();
		while(iter.hasNext()) {
			current.app(iter.next().getName());
			if(iter.hasNext()) current.app(", ");
		}
		current.app(')');

	}

	protected void writeStaticFuncs(ClassDecl classDecl, String className)
			throws IOException {
		
		for(FunctionDecl decl: classDecl.getFunctions()) {
			
			if(!decl.isStatic()) continue;
			
			current.nl();
			writeMemberFuncPrototype(className, decl);
			current.openBlock();
			decl.getBody().accept(this);
			current.closeSpacedBlock();
			
		}
	}

	protected void writeInstanceVirtualFuncs(ClassDecl classDecl, String className)
			throws IOException {
		
		for(FunctionDecl decl: classDecl.getFunctions()) {
			
			if(decl.isStatic() || decl.isFinal()) continue;
			
			current.nl();
			writeMemberFuncPrototype(className, decl);
			current.openSpacedBlock();
			
			if(!decl.getReturnType().isVoid()) current.app("return ");
			current.app("((").app(className).app("Class *)((Object *)this)->class)->");
			decl.writeSuffixedName(current);
			
			writeTypelessFuncArgs(decl);
			current.app(";").closeSpacedBlock();
			
		}
	}
	
	protected void writeBuiltinClassFuncName(String className, String returnType, String name)
		throws IOException {
		current.nl().app("static ").app(returnType).app(' ').app(className)
			.app('_').app(name).app('(').app(className).app(" *this)");
	}
	
	protected void writeInitializeModuleFunc() throws IOException {

		current = hw;
		current.nl().app("void ").app(module.getLoadFuncName()).app("();");
		
		current = cw;
		current.nl().app("void ").app(module.getLoadFuncName()).app("()").openBlock();
		
		current.nl().app("static bool __done__ = false;").nl().app("if (!__done__)").openBlock();
		current.nl().app("__done__ = true;");
		
		for(Node node: module.getBody()) {
			if(node instanceof ClassDecl) {
				ClassDecl classDecl = (ClassDecl) node;
				current.nl().app(classDecl.getInstanceType().getMangledName());
				current.app("_load();");
			}
		}
		for(Node node: module.getLoadFunc().getBody()) {
			node.accept(this);
		}
		for(Import imp: module.getImports()) {
			current.nl().app(imp.getModule().getLoadFuncName()).app("();");
		}
		
		current.closeBlock().closeSpacedBlock();
		
	}

	protected void writeInitializeClassFunc(ClassDecl classDecl, String className)
			throws IOException {
		
		writeBuiltinClassFuncName(className, "void", "initialize");
		current.openBlock();
		if(!classDecl.getSuperName().isEmpty()) {
			current.nl();
			current.app(classDecl.getSuperName());
			current.app("_class()->initialize((Object *) this);");
		}
		for(Line line: classDecl.getInitializeFunc().getBody()) line.accept(this);
		current.closeSpacedBlock();
		
		current.nl().app("void ").app(className).app("_load()").openBlock();
		current.nl().app("static bool __done__ = false;").nl().app("if (!__done__)").openBlock();
		current.nl().app("__done__ = true;");
		for(Line line: classDecl.getLoadFunc().getBody()) {
			line.accept(this);
		}
		current.closeBlock().closeSpacedBlock();
		
	}

	protected void writeDestroyFunc(ClassDecl classDecl, String className)
			throws IOException {
		
		current.nl();
		writeBuiltinClassFuncName(className, "void", "destroy");
		current.openBlock().openSpacedBlock();
		current.app("const Class *super = ((Object *) this)->class->super;");
		current.nl().app("if(super) super->destroy((Object *) this);");
		current.closeSpacedBlock().closeSpacedBlock();
	}

	protected void writeInstanceImplFuncs(ClassDecl classDecl, String className)
			throws IOException {
		
		/* Non-static (ie. instance) functions */
		for(FunctionDecl decl: classDecl.getFunctions()) {
			if(decl.isStatic()) continue;
		
			current.nl();
			if(!decl.isFinal()) current.app("static ");
			writeSpacedType(decl.getReturnType());
			decl.writeFullName(current);
			if(!decl.isFinal()) current.app("_impl");
		
			writeFuncArgs(decl, decl.isConstructor()); // if is constuctor, don't write the first arg
			
			current.openBlock();
			
			/* Special case: constructor */
			if(decl.isConstructor()) {
				current.nl().app(className).app(" *this = (").app(className)
					.app(" *) Class_newInstance((Class *)").app(className).app("_class());");
				current.nl().app(className).app("_construct");
				if(!decl.getSuffix().isEmpty()) current.app('_').app(decl.getSuffix());
				writeTypelessFuncArgs(decl);
				current.app(";").nl().app("return this;");
			} else {
				decl.getBody().accept(this);
			}			
			current.closeSpacedBlock();
			
			/* Special case: constructor, now write the corresponding construct function */
			if(decl.isConstructor()) {
				current.app("void ").app(className).app("_construct");
				if(!decl.getSuffix().isEmpty()) current.app('_').app(decl.getSuffix());
				writeFuncArgs(decl);
				current.openBlock();
				for(Line line: decl.getBody()) line.accept(this);
				current.closeSpacedBlock();
			}
		}
		
	}

	protected void writeClassGettingFunction(ClassDecl classDecl) throws IOException {
		
		current.app("Class *").app(classDecl.getName()).app("_class()").openSpacedBlock();
		current.app("static bool __done__ = false;").nl();
		current.app("static ").app(classDecl.getName()).app("Class class = ");
		writeFuncPointers(classDecl, classDecl);
		current.app(';');
		current.nl().app("Class *classPtr = (Class *) &class;");
		if(!classDecl.getSuperName().isEmpty()) {
			current.nl().app("if(!__done__)").openBlock().nl().app("__done__ = true;")
				.nl().app("classPtr->super = ").app(classDecl.getSuperName()).app("_class();")
			.closeBlock();
		}
		
		current.nl().app("return classPtr;").closeSpacedBlock();
	}

	protected void writeFuncPointers(ClassDecl currentClass, ClassDecl coreClass) throws IOException {
		
		current.openBlock();
		
		if(!currentClass.isRootClass() && !currentClass.getSuperName().isEmpty()) {
			
			writeFuncPointers(currentClass.getSuperRef(), coreClass);
			
		} else {
		
			current.openBlock();
			current.nl().app(".size = ").app("sizeof(").app(coreClass.getName()).app("),");
			current.nl().app(".name = ").app('"').app(coreClass.getName()).app("\",");
			writeDesignatedInit("initialize", "(void (*)(Object *))"+coreClass.getName()+"_initialize");
			writeDesignatedInit("destroy", "(void (*)(Object *))"+coreClass.getName()+"_destroy");
			
			current.closeBlock().app(',');
		
		}
		
		for(FunctionDecl decl: currentClass.getFunctions()) {
			if(decl.isStatic()) continue;
			if(decl.isConstructor()) continue;
			
			if(decl.isFinal()) writeDesignatedInit(decl.getName(), currentClass.getName() + "_" + decl.getName());
			else writeDesignatedInit(decl.getName(), currentClass.getName() + "_" + decl.getName() + "_impl");
			
		}
		
		current.closeBlock();
		if(coreClass != currentClass) current.app(',');
	}

	protected void writeMemberFuncPrototypes(ClassDecl classDecl, String className)
			throws IOException {

		current.nl().app("Class *").app(className).app("_class();").nl();
		for(FunctionDecl decl: classDecl.getFunctions()) {
			current.nl();
			writeSpacedType(decl.getReturnType());
			decl.writeFullName(current);
			writeFuncArgs(decl, decl.isConstructor());
			current.app(';');
			
			if(decl.getName().equals("new")) {
				current.nl().app("void ").app(className).app("_construct");
				if(!decl.getSuffix().isEmpty()) current.app('_').app(decl.getSuffix());
				writeFuncArgs(decl);
				current.app(';');
			}
		}
		current.nl();
	}

	protected void writeSpacedType(Type type) throws IOException {
		type.accept(this);
		if(type.isFlat()) current.app(' ');
	}

	protected Iterator<Argument> writeFuncPointer(FunctionDecl decl)
			throws IOException {
		decl.getReturnType().accept(this);
		current.app(" (*").app(decl.getName()).app(")(");
		Iterator<Argument> iter = decl.getArguments().iterator();
		while(iter.hasNext()) {
			Argument arg = iter.next();
			arg.accept(this);
			if(iter.hasNext()) current.app(", ");
		}
		current.app(')');
		return iter;
	}
	
	protected void writeClassStruct(ClassDecl classDecl, String className)
		throws IOException {
		
		current.nl().app("struct _").app(className).app("Class").openSpacedBlock();
		if(classDecl.isRootClass()) {
			current.app("struct _Class __super__;");
		} else {
			current.app("struct _").app(classDecl.getSuperName()).app("Class __super__;");
		}
		
		/* Now write all virtual functions prototypes in the class struct */
		for(FunctionDecl decl: classDecl.getFunctions()) {
			if(decl.isStatic() || decl.isConstructor()) continue;
			current.nl();
			writeFuncPointer(decl);
			current.app(';');
		}
		current.closeBlock().app(';').nl().nl();
	}

	protected void writeObjectStruct(ClassDecl classDecl, String className)
			throws IOException {
		current.nl().app("struct _").app(className).openSpacedBlock();
		
		if(classDecl.isClassClass()) {
			current.app("Class *class;");
		} else if(!classDecl.isObjectClass()) {
			current.app("struct _").app(classDecl.getSuperName()).app(" __super__;");
		}
		
		for(VariableDecl decl: classDecl.getVariables()) {
			if(decl.isStatic()) continue;
			current.nl();
			decl.accept(this);
			current.app(';');
		}
		
		current.closeBlock().app(';').nl().nl();
		
		for(VariableDecl decl: classDecl.getVariables()) {
			if(!decl.isStatic()) continue;
			current.nl();
			decl.accept(this);
			current.app(';');
		}
	}

	protected void writeStructTypedef(String structName) throws IOException {
		current.nl().app("struct _").app(structName).app(";");
		current.nl().app("typedef struct _").app(structName)
			.app(" ").app(structName).app(";");
	}

	protected void writeDesignatedInit(String contract, String implementation)
			throws IOException {
		current.nl().app('.').app(contract).app(" = ").app(implementation).app(',');
	}
	
	@Override
	public void visit(CoverDecl cover) throws IOException {
		current = hw;

		// addons only add functions to an already imported cover, so
		// we don't need to struct it again, it would confuse the C compiler
		if(!cover.isAddon() && !cover.isExtern() && cover.getFromType() == null) {
			current.app("struct _").app(cover.getName()).app(' ').openBlock();
			for(VariableDecl decl: cover.getVariables()) {
				current.nl();
				decl.accept(this);
				current.app(';');
			}
			current.closeBlock().app(';').nl();
		}
		
		for(FunctionDecl decl: cover.getFunctions()) {
			decl.accept(this);
			current.nl();
		}
	}

	protected void writeCoverTypedef(CoverDecl cover) throws IOException {
		
		if(!cover.isAddon() && !cover.isExtern()) {
			Type fromType = cover.getFromType();
			if(fromType == null) {
				current.nl().app("typedef struct _").app(cover.getName()).app(' ').app(cover.getName()).app(';');
			} else {
				current.nl().app("typedef ");
				writeSpacedType(fromType.getGroundType());
				current.app(cover.getName()).app(';');
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
			current.app("const ");
		}
		Type type = regularArgument.getType();
		if(type.isArray()) {
			current.app(type.getName()).app(' ').app(regularArgument.getName());
			for(int i = 0; i < type.getPointerLevel(); i++) {
				current.app("[]");
			}
		} else {
			writeSpacedType(type);
			current.app(regularArgument.getName());
		}
		
	}

	@Override
	public void visit(MemberArgument memberArgument) throws IOException {}

	@Override
	public void visit(MemberAssignArgument memberArgument) throws IOException {}

	@Override
	public void visit(Type type) throws IOException {
		if(type.getName().equals("Func")) {
			current.append("void (*)()");
			return;
		}
		
		if(type.getRef() instanceof TypeParam) {
			current.append("Pointer");
			return;
		}
		
		current.app(type.getName());
		if(!type.isFlat()) {
			current.app(' ');
		}
		
		if(type.getRef() == null) {
			throw new Error("Unresolved Type "+type.getName()+" !!");
		}
		
		if(type.getRef() instanceof ClassDecl) {
			current.app('*');
		}
		
		int level = type.getPointerLevel() + type.getReferenceLevel();
		for(int i = 0; i < level; i++) {
			if(type.isArray()) current.app("[]");
			else current.app('*');
		}
	}

	@Override
	public void visit(VarArg varArg) throws IOException {
		current.app("...");
	}
	
	@Override
	public void visit(NodeList<? extends Node> list) throws IOException {
		list.acceptChildren(this);
	}
	
	@Override
	public void visit(Block block) throws IOException {
		current.openBlock();
		block.acceptChildren(this);
		current.closeBlock();
	}

	@Override
	public void visit(BuiltinType builtinType) throws IOException {}

	@Override
	public void visit(FloatLiteral floatLiteral) throws IOException {
		current.app(Double.toString(floatLiteral.getValue()));
	}

	@Override
	public void visit(VariableDeclAtom variableDeclAtom) throws IOException {}
	
	@Override
	public void visit(Cast cast) throws IOException {
		if(cast.getExpression().getType().getRef() instanceof TypeParam) {
			System.out.println("Cast has a typeparam!");
			VariableAccess access = (VariableAccess) cast.getExpression();
			current.app("*((");
			cast.getType().accept(this);
			current.app("*)");
			writeVarAccess(access, false);
			current.app(')');
			return;
		}
		
		current.app("((");
		cast.getType().accept(this);
		current.app(") ");
		cast.getExpression().accept(this);
		current.app(")");
	}

	@Override
	public void visit(AddressOf addressOf) throws IOException {
		current.app("&(");
		addressOf.getExpression().accept(this);
		current.app(')');
	}

	@Override
	public void visit(Dereference dereference) throws IOException {
		current.app("(*");
		dereference.getExpression().accept(this);
		current.app(')');
	}

	@Override
	public void visit(OpDecl opDecl) throws IOException {
		opDecl.getFunc().accept(this);
	}

	@Override
	public void visit(Import import1) throws IOException {}
	
	@Override
	public void visit(ArrayLiteral arrayLiteral) throws IOException {
		current.app('{');
		Iterator<Expression> iter = arrayLiteral.getElements().iterator();
		while(iter.hasNext()) {
			iter.next().accept(this);
			if(iter.hasNext()) current.app(", ");
		}
		current.app('}');
	}

	@Override
	public void visit(Use use) throws IOException {}

	@Override
	public void visit(BinaryCombination binaryCombination) throws IOException {
		binaryCombination.getLeft().accept(this);
		current.app(' ').app(binaryCombination.getOpString()).app(' ');
		binaryCombination.getRight().accept(this);
	}

}
