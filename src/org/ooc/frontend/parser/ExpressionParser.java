package org.ooc.frontend.parser;

import static org.ooc.frontend.model.tokens.Token.TokenType.ASSIGN;
import static org.ooc.frontend.model.tokens.Token.TokenType.AS_KW;
import static org.ooc.frontend.model.tokens.Token.TokenType.CLOS_PAREN;
import static org.ooc.frontend.model.tokens.Token.TokenType.CLOS_SQUAR;
import static org.ooc.frontend.model.tokens.Token.TokenType.DOUBLE_DOT;
import static org.ooc.frontend.model.tokens.Token.TokenType.EQUALS;
import static org.ooc.frontend.model.tokens.Token.TokenType.EXCL;
import static org.ooc.frontend.model.tokens.Token.TokenType.GT;
import static org.ooc.frontend.model.tokens.Token.TokenType.GTE;
import static org.ooc.frontend.model.tokens.Token.TokenType.LT;
import static org.ooc.frontend.model.tokens.Token.TokenType.LTE;
import static org.ooc.frontend.model.tokens.Token.TokenType.MINUS;
import static org.ooc.frontend.model.tokens.Token.TokenType.NAME;
import static org.ooc.frontend.model.tokens.Token.TokenType.NOT_EQ;
import static org.ooc.frontend.model.tokens.Token.TokenType.OPEN_PAREN;
import static org.ooc.frontend.model.tokens.Token.TokenType.OPEN_SQUAR;
import static org.ooc.frontend.model.tokens.Token.TokenType.PERCENT;
import static org.ooc.frontend.model.tokens.Token.TokenType.PLUS;
import static org.ooc.frontend.model.tokens.Token.TokenType.SLASH;
import static org.ooc.frontend.model.tokens.Token.TokenType.STAR;

import java.io.IOException;

import org.ooc.frontend.model.Access;
import org.ooc.frontend.model.Add;
import org.ooc.frontend.model.ArrayAccess;
import org.ooc.frontend.model.Assignment;
import org.ooc.frontend.model.Cast;
import org.ooc.frontend.model.Compare;
import org.ooc.frontend.model.Declaration;
import org.ooc.frontend.model.Div;
import org.ooc.frontend.model.Expression;
import org.ooc.frontend.model.FunctionCall;
import org.ooc.frontend.model.Instantiation;
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
import org.ooc.frontend.model.Compare.CompareType;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.frontend.model.tokens.TokenReader;
import org.ubi.CompilationFailedError;
import org.ubi.SourceReader;

public class ExpressionParser {

	public static Expression parse(SourceReader sReader, TokenReader reader) throws IOException {
		
		int mark = reader.mark();
		
		if(reader.peek().type == EXCL) {
			reader.skip();
			Expression inner = ExpressionParser.parse(sReader, reader);
			if(inner == null) {
				reader.reset(mark);
				return null;
			}
			return new Not(inner);
		}
		
		Expression expr = parseFlat(sReader, reader);
		if(expr == null) {
			return null;
		}
		
		while(reader.hasNext()) {
			
			Token t = reader.peek();
			
			if(t.type == NAME) {
				
				FunctionCall call = FunctionCallParser.parse(sReader, reader);
				if(call != null) {
					expr = new MemberCall(expr, call);
					continue;
				}
				
				VariableAccess varAccess = VariableAccessParser.parse(sReader, reader);
				if(varAccess != null) {
					expr = new MemberAccess(expr, varAccess);
					continue;
				}
				
			}
			
			if(t.type == DOUBLE_DOT) {
				
				reader.skip();
				Expression upper = ExpressionParser.parse(sReader, reader);
				if(upper == null) {
					throw new CompilationFailedError(sReader.getLocation(reader.peek().start),
							"Expected expression for the upper part of a range literal");
				}
				// this is so beautiful it makes me wanna cry
				expr = new RangeLiteral(expr, upper);
				
			}
			
			if(t.type == OPEN_SQUAR) {

				reader.skip();
				Expression index = ExpressionParser.parse(sReader, reader);
				if(index == null) {
					throw new CompilationFailedError(sReader.getLocation(reader.peek().start),
						"Expected expression for the index of an array access");
				}
				if(reader.read().type != CLOS_SQUAR) {
					throw new CompilationFailedError(sReader.getLocation(reader.prev().start),
						"Expected closing bracket to end array access, got "+reader.prev().type+" instead.");
				}
				expr = new ArrayAccess(expr, index);
				continue;
				
			}
			
			if(t.type == ASSIGN) {
				
				reader.skip();
				Expression rvalue = ExpressionParser.parse(sReader, reader);
				if(rvalue == null) {
					throw new CompilationFailedError(sReader.getLocation(reader.peek().start),
						"Expected expression after '='.");
				}
				if(!(expr instanceof Access)) {
					throw new CompilationFailedError(sReader.getLocation(reader.peek().start),
						"Attempting to assign to a constant, e.g. "+expr);
				}
				expr = new Assignment((Access) expr, rvalue);
				continue;
				
			}
			
			if(t.type == PLUS || t.type == STAR
					|| t.type == MINUS || t.type == SLASH || t.type == PERCENT
					|| t.type == GT || t.type == LT || t.type == GTE || t.type == LTE
					|| t.type == EQUALS || t.type == NOT_EQ) {
				
				reader.skip();
				Expression rvalue = ExpressionParser.parse(sReader, reader);
				if(rvalue == null) {
					throw new CompilationFailedError(sReader.getLocation(reader.peek().start),
						"Expected rvalue after binary operator");
				}
				switch(t.type) {
					case PLUS:  expr = new Add(expr, rvalue); break;
					case STAR:  expr = new Mul(expr, rvalue); break;
					case MINUS: expr = new Sub(expr, rvalue); break;
					case SLASH: expr = new Div(expr, rvalue); break;
					case PERCENT: expr = new Mod(expr, rvalue); break;
					case GT: expr = new Compare(expr, rvalue, CompareType.GREATER); break;
					case GTE: expr = new Compare(expr, rvalue, CompareType.GREATER_OR_EQUAL); break;
					case LT: expr = new Compare(expr, rvalue, CompareType.LESSER); break;
					case LTE: expr = new Compare(expr, rvalue, CompareType.LESSER_OR_EQUAL); break;
					case EQUALS: expr = new Compare(expr, rvalue, CompareType.EQUAL); break;
					case NOT_EQ: expr = new Compare(expr, rvalue, CompareType.NOT_EQUAL); break;
					default: throw new CompilationFailedError(sReader.getLocation(reader.prev().start),
							"Unknown binary operation yet");
				}
				continue;
				
				
			}
			
			if(t.type == AS_KW) {
				
				reader.skip();
				Type type = TypeParser.parse(sReader, reader);
				if(type == null) {
					throw new CompilationFailedError(sReader.getLocation(reader.prev().start),
							"Expected destination type after 'as' keyword (e.g. for casting)");
				}
				expr = new Cast(expr, type);
				continue;
				
			}
			
			return expr;
			
		}
		
		return null;
		
	}
	
	private static Expression parseFlat(SourceReader sReader, TokenReader reader) throws IOException {
		
		int mark = reader.mark();
		
		int parenNumber = 0;
		Token t = reader.peek();
		while(t.type == OPEN_PAREN) {
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
				throw new CompilationFailedError(sReader.getLocation(reader.peek().start),
					"Expected expression in parenthesis");
			}
			reader.reset(mark);
			return null;
		}
		
		while(parenNumber > 0) {
			if(reader.read().type != CLOS_PAREN) {
				throw new CompilationFailedError(sReader.getLocation(reader.peek().start),
					"Missing closing parenthesis.");
			}
			expression = new Parenthesis(expression);
			parenNumber--;
		}
		
		return expression;
		
	}
	
	private static Expression parseFlatNoparen(SourceReader sReader, TokenReader reader) throws IOException {
		
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
