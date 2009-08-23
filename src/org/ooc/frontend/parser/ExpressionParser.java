package org.ooc.frontend.parser;

import java.io.IOException;

import org.ooc.frontend.model.Access;
import org.ooc.frontend.model.Add;
import org.ooc.frontend.model.AddressOf;
import org.ooc.frontend.model.ArrayAccess;
import org.ooc.frontend.model.Assignment;
import org.ooc.frontend.model.BinaryCombination;
import org.ooc.frontend.model.Cast;
import org.ooc.frontend.model.Compare;
import org.ooc.frontend.model.Declaration;
import org.ooc.frontend.model.Dereference;
import org.ooc.frontend.model.Div;
import org.ooc.frontend.model.Expression;
import org.ooc.frontend.model.FunctionCall;
import org.ooc.frontend.model.Instantiation;
import org.ooc.frontend.model.IntLiteral;
import org.ooc.frontend.model.Literal;
import org.ooc.frontend.model.MemberAccess;
import org.ooc.frontend.model.MemberCall;
import org.ooc.frontend.model.Mod;
import org.ooc.frontend.model.Mul;
import org.ooc.frontend.model.Not;
import org.ooc.frontend.model.Parenthesis;
import org.ooc.frontend.model.RangeLiteral;
import org.ooc.frontend.model.Sub;
import org.ooc.frontend.model.Type;
import org.ooc.frontend.model.VariableAccess;
import org.ooc.frontend.model.Assignment.Mode;
import org.ooc.frontend.model.BinaryCombination.BinaryComp;
import org.ooc.frontend.model.Compare.CompareType;
import org.ooc.frontend.model.IntLiteral.Format;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.frontend.model.tokens.TokenReader;
import org.ooc.frontend.model.tokens.Token.TokenType;
import org.ubi.CompilationFailedError;
import org.ubi.SourceReader;

public class ExpressionParser {

	public static Expression parse(SourceReader sReader, TokenReader reader) throws IOException {
		
		int mark = reader.mark();
		
		Token firstToken = reader.peek();
		if(firstToken.type == TokenType.BANG) {
			reader.skip();
			Expression inner = ExpressionParser.parse(sReader, reader);
			if(inner == null) {
				reader.reset(mark);
				return null;
			}
			return new Not(inner, firstToken);
		}
		
		if(firstToken.type == TokenType.MINUS) {
			reader.skip();
			Expression inner = ExpressionParser.parse(sReader, reader);
			if(inner == null) {
				reader.reset(mark);
				return null;
			}
			return new Sub(new IntLiteral(0, Format.DEC, firstToken), inner, firstToken);
		}
		
		Expression expr = parseFlat(sReader, reader);
		if(expr == null) {
			return null;
		}
		
		while(reader.hasNext()) {
			
			Token token = reader.peek();
			
			if(token.isNameToken()) {
				
				FunctionCall call = FunctionCallParser.parse(sReader, reader);
				if(call != null) {
					expr = new MemberCall(expr, call, token);
					continue;
				}
				
				VariableAccess varAccess = VariableAccessParser.parse(sReader, reader);
				if(varAccess != null) {
					expr = new MemberAccess(expr, varAccess, token);
					continue;
				}
				
			}
			
			if(token.type == TokenType.DOUBLE_DOT) {
				
				reader.skip();
				Expression upper = ExpressionParser.parse(sReader, reader);
				if(upper == null) {
					throw new CompilationFailedError(sReader.getLocation(reader.peek()),
							"Expected expression for the upper part of a range literal");
				}
				// this is so beautiful it makes me wanna cry
				expr = new RangeLiteral(expr, upper, expr.startToken);
				
			}
			
			if(token.type == TokenType.OPEN_SQUAR) {

				reader.skip();
				Expression index = ExpressionParser.parse(sReader, reader);
				if(index == null) {
					throw new CompilationFailedError(sReader.getLocation(reader.peek()),
						"Expected expression for the index of an array access");
				}
				if(reader.read().type != TokenType.CLOS_SQUAR) {
					throw new CompilationFailedError(sReader.getLocation(reader.prev()),
						"Expected closing bracket to end array access, got "+reader.prev().type+" instead.");
				}
				expr = new ArrayAccess(expr, index, token);
				continue;
				
			}
			
			if(token.type == TokenType.ASSIGN || token.type == TokenType.DECL_ASSIGN) {
				
				reader.skip();
				Expression rvalue = ExpressionParser.parse(sReader, reader);
				if(rvalue == null) {
					throw new CompilationFailedError(sReader.getLocation(reader.peek()),
						"Expected expression after '='.");
				}
				if(!(expr instanceof Access)) {
					throw new CompilationFailedError(sReader.getLocation(reader.peek()),
						"Attempting to assign to a constant, e.g. "+expr);
				}
				if(token.type == TokenType.ASSIGN) {
					expr = new Assignment((Access) expr, rvalue, token);
				} else if(token.type == TokenType.DECL_ASSIGN) {
					expr = new Assignment(Mode.DECLARATION, (Access) expr, rvalue, token);
				}
				continue;
				
			}
			
			if(token.type == TokenType.AMPERSAND) {
				reader.skip();
				expr = new AddressOf(expr, token);
				continue;
			}
			
			if(token.type == TokenType.AT) {
				reader.skip();
				expr = new Dereference(expr, token);
				continue;
			}
			
			if(token.type == TokenType.PLUS || token.type == TokenType.STAR
					|| token.type == TokenType.MINUS || token.type == TokenType.SLASH
					|| token.type == TokenType.PERCENT || token.type == TokenType.GREATERTHAN
					|| token.type == TokenType.LESSTHAN || token.type == TokenType.GREATERTHAN_EQUALS
					|| token.type == TokenType.LESSTHAN_EQUALS || token.type == TokenType.EQUALS
					|| token.type == TokenType.NOT_EQUALS || token.type == TokenType.PLUS_ASSIGN
					|| token.type == TokenType.MINUS_ASSIGN || token.type == TokenType.STAR_ASSIGN
					|| token.type == TokenType.SLASH_ASSIGN || token.type == TokenType.DOUBLE_PIPE
					|| token.type == TokenType.DOUBLE_AMPERSAND || token.type == TokenType.PIPE
					|| token.type == TokenType.AMPERSAND) {
				
				reader.skip();
				Expression rvalue = ExpressionParser.parse(sReader, reader);
				if(rvalue == null) {
					throw new CompilationFailedError(sReader.getLocation(reader.peek()),
						"Expected rvalue after binary operator");
				}
				switch(token.type) {
					case PLUS:  expr = new Add(expr, rvalue, token); break;
					case STAR:  expr = new Mul(expr, rvalue, token); break;
					case MINUS: expr = new Sub(expr, rvalue, token); break;
					case SLASH: expr = new Div(expr, rvalue, token); break;
					case PERCENT: expr = new Mod(expr, rvalue, token); break;
					case GREATERTHAN: expr = new Compare(expr, rvalue, CompareType.GREATER, token); break;
					case GREATERTHAN_EQUALS: expr = new Compare(expr, rvalue, CompareType.GREATER_OR_EQUAL, token); break;
					case LESSTHAN: expr = new Compare(expr, rvalue, CompareType.LESSER, token); break;
					case LESSTHAN_EQUALS: expr = new Compare(expr, rvalue, CompareType.LESSER_OR_EQUAL, token); break;
					case EQUALS: expr = new Compare(expr, rvalue, CompareType.EQUAL, token); break;
					case NOT_EQUALS: expr = new Compare(expr, rvalue, CompareType.NOT_EQUAL, token); break;
					case PLUS_ASSIGN:  ensureAccess(expr);
						expr = new Assignment(Mode.ADD, (Access) expr, rvalue, token); break;
					case MINUS_ASSIGN: ensureAccess(expr);
						expr = new Assignment(Mode.SUB, (Access) expr, rvalue, token); break;
					case STAR_ASSIGN:  ensureAccess(expr);
						expr = new Assignment(Mode.MUL, (Access) expr, rvalue, token); break;
					case SLASH_ASSIGN: ensureAccess(expr);
						expr = new Assignment(Mode.DIV, (Access) expr, rvalue, token); break;
					case PIPE: expr = new BinaryCombination(BinaryComp.BINARY_OR, expr, rvalue, token); break;
					case AMPERSAND: expr = new BinaryCombination(BinaryComp.BINARY_AND, expr, rvalue, token); break;
					case DOUBLE_PIPE:  expr = new BinaryCombination(BinaryComp.LOGICAL_OR,  expr, rvalue, token); break;
					case DOUBLE_AMPERSAND: expr = new BinaryCombination(BinaryComp.LOGICAL_AND, expr, rvalue, token); break;
					default: throw new CompilationFailedError(sReader.getLocation(reader.prev()),
							"Unknown binary operation yet "+token.type);
				}
				continue;
				
				
			}
			
			if(token.type == TokenType.AS_KW) {
				
				reader.skip();
				Type type = TypeParser.parse(sReader, reader);
				if(type == null) {
					throw new CompilationFailedError(sReader.getLocation(reader.prev()),
							"Expected destination type after 'as' keyword (e.g. for casting)");
				}
				expr = new Cast(expr, type, token);
				continue;
				
			}
			
			return expr;
			
		}
		
		return null;
		
	}
	
	protected static void ensureAccess(Expression expr) {

		if(!(expr instanceof Access)) {
			throw new CompilationFailedError(null, "Trying to assign to a constant :/");
		}
		
	}

	protected static Expression parseFlat(SourceReader sReader, TokenReader reader) throws IOException {
		
		int mark = reader.mark();
		
		int parenNumber = 0;
		Token t = reader.peek();
		while(t.type == TokenType.OPEN_PAREN) {
			reader.skip();
			parenNumber++;
			t = reader.peek();
		}
		
		Expression expression;
		
		if(parenNumber > 0) {
			expression = parse(sReader, reader);
		} else {
			expression = parseFlatNoparen(sReader, reader);
		}
		
		if(expression == null) {
			if(parenNumber > 0) {
				throw new CompilationFailedError(sReader.getLocation(reader.peek()),
					"Expected expression in parenthesis");
			}
			reader.reset(mark);
			return null;
		}
		
		while(parenNumber > 0) {
			if(reader.read().type != TokenType.CLOS_PAREN) {
				throw new CompilationFailedError(sReader.getLocation(reader.peek()),
					"Missing closing parenthesis.");
			}
			expression = new Parenthesis(expression, expression.startToken);
			parenNumber--;
		}
		
		return expression;
		
	}
	
	protected static Expression parseFlatNoparen(SourceReader sReader, TokenReader reader) throws IOException {
		
		int mark = reader.mark();
		
		Literal literal = LiteralParser.parse(sReader, reader);
		if(literal != null) return literal;

		Instantiation instantiation = InstantiationParser.parse(sReader, reader);
		if(instantiation != null) return instantiation;
		
		FunctionCall funcCall = FunctionCallParser.parse(sReader, reader);
		if(funcCall != null) return funcCall;
		
		Declaration declaration = DeclarationParser.parse(sReader, reader);
		if(declaration != null) return declaration;
				
		Access access = VariableAccessParser.parse(sReader, reader);
		if(access != null) return access;
		
		reader.reset(mark);
		return null;
		
	}
	
}
