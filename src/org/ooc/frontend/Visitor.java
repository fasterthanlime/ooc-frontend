package org.ooc.frontend;

import org.ooc.frontend.model.Add;
import org.ooc.frontend.model.ArrayAccess;
import org.ooc.frontend.model.Assignment;
import org.ooc.frontend.model.BoolLiteral;
import org.ooc.frontend.model.CharLiteral;
import org.ooc.frontend.model.ClassDecl;
import org.ooc.frontend.model.Comment;
import org.ooc.frontend.model.Div;
import org.ooc.frontend.model.Foreach;
import org.ooc.frontend.model.FunctionCall;
import org.ooc.frontend.model.FunctionDecl;
import org.ooc.frontend.model.If;
import org.ooc.frontend.model.Import;
import org.ooc.frontend.model.Include;
import org.ooc.frontend.model.Line;
import org.ooc.frontend.model.MemberArgument;
import org.ooc.frontend.model.MemberAssignArgument;
import org.ooc.frontend.model.Mul;
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
import org.ooc.frontend.model.VariableAccess;
import org.ooc.frontend.model.VariableDecl;
import org.ooc.frontend.model.VariableDeclAssigned;
import org.ooc.frontend.model.While;

public interface Visitor {

	public void visit(SourceUnit sourceUnit);
	
	public void visit(Add add);
	public void visit(Mul mul);
	public void visit(Sub sub);
	public void visit(Div div);
	public void visit(Not not);
	
	public void visit(Comment comment);

	public void visit(FunctionCall functionCall);
	public void visit(Parenthesis parenthesis);
	public void visit(Assignment assignment);
	public void visit(Return return1);
	
	public void visit(NullLiteral nullLiteral);
	public void visit(NumberLiteral numberLiteral);
	public void visit(StringLiteral stringLiteral);
	public void visit(RangeLiteral rangeLiteral);
	public void visit(BoolLiteral boolLiteral);
	public void visit(CharLiteral charLiteral);
	
	public void visit(Line line);

	public void visit(Include include);
	public void visit(Import import1);

	public void visit(If if1);
	public void visit(While while1);
	public void visit(Foreach foreach);

	public void visit(VariableAccess variableAccess);
	public void visit(ArrayAccess arrayAccess);

	public void visit(VariableDecl variableDecl);
	public void visit(VariableDeclAssigned variableDeclAssigned);
	public void visit(FunctionDecl functionDecl);
	public void visit(ClassDecl classDecl);

	public void visit(RegularArgument regularArgument);
	public void visit(MemberArgument memberArgument);
	public void visit(MemberAssignArgument memberArgument);
	
}
