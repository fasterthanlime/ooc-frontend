package org.ooc.frontend;

import java.io.IOException;

import org.ooc.frontend.model.Add;
import org.ooc.frontend.model.AddressOf;
import org.ooc.frontend.model.ArrayAccess;
import org.ooc.frontend.model.Assignment;
import org.ooc.frontend.model.Block;
import org.ooc.frontend.model.BoolLiteral;
import org.ooc.frontend.model.BuiltinType;
import org.ooc.frontend.model.Cast;
import org.ooc.frontend.model.CharLiteral;
import org.ooc.frontend.model.ClassDecl;
import org.ooc.frontend.model.Compare;
import org.ooc.frontend.model.CoverDecl;
import org.ooc.frontend.model.Dereference;
import org.ooc.frontend.model.Div;
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
import org.ooc.frontend.model.ValuedReturn;
import org.ooc.frontend.model.VarArg;
import org.ooc.frontend.model.VariableAccess;
import org.ooc.frontend.model.VariableDecl;
import org.ooc.frontend.model.While;
import org.ooc.frontend.model.VariableDecl.VariableDeclAtom;
import org.ooc.frontend.parser.TypeArgument;

public interface Visitor {

	public void visit(Module module) throws IOException;
	
	public void visit(Add add) throws IOException;
	public void visit(Mul mul) throws IOException;
	public void visit(Sub sub) throws IOException;
	public void visit(Div div) throws IOException;
	public void visit(Not not) throws IOException;
	public void visit(Mod mod) throws IOException;
	public void visit(Compare compare) throws IOException;
	
	public void visit(MultiLineComment comment) throws IOException;
	public void visit(SingleLineComment slComment) throws IOException;

	public void visit(FunctionCall functionCall) throws IOException;
	public void visit(MemberCall memberCall) throws IOException;
	public void visit(Instantiation inst) throws IOException;
	
	public void visit(Parenthesis parenthesis) throws IOException;
	public void visit(Assignment assignment) throws IOException;
	public void visit(ValuedReturn return1) throws IOException;
	public void visit(Return return1) throws IOException;
	
	public void visit(NullLiteral nullLiteral) throws IOException;
	public void visit(IntLiteral numberLiteral) throws IOException;
	public void visit(FloatLiteral floatLiteral) throws IOException;
	public void visit(StringLiteral stringLiteral) throws IOException;
	public void visit(RangeLiteral rangeLiteral) throws IOException;
	public void visit(BoolLiteral boolLiteral) throws IOException;
	public void visit(CharLiteral charLiteral) throws IOException;
	
	public void visit(Line line) throws IOException;

	public void visit(Include include) throws IOException;
	public void visit(Import import1) throws IOException;

	public void visit(If if1) throws IOException;
	public void visit(While while1) throws IOException;
	public void visit(Foreach foreach) throws IOException;

	public void visit(VariableAccess variableAccess) throws IOException;
	public void visit(MemberAccess memberAccess) throws IOException;
	public void visit(ArrayAccess arrayAccess) throws IOException;

	public void visit(VariableDecl variableDecl) throws IOException;
	public void visit(VariableDeclAtom variableDeclAtom) throws IOException;
	public void visit(FunctionDecl functionDecl) throws IOException;
	public void visit(ClassDecl classDecl) throws IOException;
	public void visit(CoverDecl cover) throws IOException;

	public void visit(TypeArgument typeArgument) throws IOException;
	public void visit(RegularArgument regularArgument) throws IOException;
	public void visit(MemberArgument memberArgument) throws IOException;
	public void visit(MemberAssignArgument memberArgument) throws IOException;

	public void visit(Type type) throws IOException;
	public void visit(BuiltinType builtinType) throws IOException;

	public void visit(VarArg varArg) throws IOException;
	
	public void visit(NodeList<? extends Node> list) throws IOException;

	public void visit(Block block) throws IOException;

	public void visit(Cast cast) throws IOException;

	public void visit(AddressOf addressOf) throws IOException;
	public void visit(Dereference dereference) throws IOException;
	
}
