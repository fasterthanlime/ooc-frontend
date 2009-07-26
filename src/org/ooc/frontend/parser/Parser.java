package org.ooc.frontend.parser;

import static org.ooc.frontend.model.tokens.Token.TokenType.ABSTRACT_KW;
import static org.ooc.frontend.model.tokens.Token.TokenType.ARROW;
import static org.ooc.frontend.model.tokens.Token.TokenType.ASSIGN;
import static org.ooc.frontend.model.tokens.Token.TokenType.BIN_NUMBER;
import static org.ooc.frontend.model.tokens.Token.TokenType.CHAR_LIT;
import static org.ooc.frontend.model.tokens.Token.TokenType.CLASS_KW;
import static org.ooc.frontend.model.tokens.Token.TokenType.CLOS_BRACK;
import static org.ooc.frontend.model.tokens.Token.TokenType.CLOS_PAREN;
import static org.ooc.frontend.model.tokens.Token.TokenType.CLOS_SQUAR;
import static org.ooc.frontend.model.tokens.Token.TokenType.COL;
import static org.ooc.frontend.model.tokens.Token.TokenType.COMMA;
import static org.ooc.frontend.model.tokens.Token.TokenType.CONST_KW;
import static org.ooc.frontend.model.tokens.Token.TokenType.COVER_KW;
import static org.ooc.frontend.model.tokens.Token.TokenType.DEC_NUMBER;
import static org.ooc.frontend.model.tokens.Token.TokenType.DOT;
import static org.ooc.frontend.model.tokens.Token.TokenType.DOUBLE_DOT;
import static org.ooc.frontend.model.tokens.Token.TokenType.EXCL;
import static org.ooc.frontend.model.tokens.Token.TokenType.EXTERN_KW;
import static org.ooc.frontend.model.tokens.Token.TokenType.FALSE;
import static org.ooc.frontend.model.tokens.Token.TokenType.FOR_KW;
import static org.ooc.frontend.model.tokens.Token.TokenType.FROM_KW;
import static org.ooc.frontend.model.tokens.Token.TokenType.FUNC_KW;
import static org.ooc.frontend.model.tokens.Token.TokenType.HASH;
import static org.ooc.frontend.model.tokens.Token.TokenType.HEX_NUMBER;
import static org.ooc.frontend.model.tokens.Token.TokenType.IF_KW;
import static org.ooc.frontend.model.tokens.Token.TokenType.IMPL_KW;
import static org.ooc.frontend.model.tokens.Token.TokenType.IMPORT_KW;
import static org.ooc.frontend.model.tokens.Token.TokenType.INCLUDE_KW;
import static org.ooc.frontend.model.tokens.Token.TokenType.MINUS;
import static org.ooc.frontend.model.tokens.Token.TokenType.ML_COMMENT;
import static org.ooc.frontend.model.tokens.Token.TokenType.NAME;
import static org.ooc.frontend.model.tokens.Token.TokenType.NEW_KW;
import static org.ooc.frontend.model.tokens.Token.TokenType.NULL;
import static org.ooc.frontend.model.tokens.Token.TokenType.OCT_NUMBER;
import static org.ooc.frontend.model.tokens.Token.TokenType.OOCDOC;
import static org.ooc.frontend.model.tokens.Token.TokenType.OPEN_BRACK;
import static org.ooc.frontend.model.tokens.Token.TokenType.OPEN_PAREN;
import static org.ooc.frontend.model.tokens.Token.TokenType.OPEN_SQUAR;
import static org.ooc.frontend.model.tokens.Token.TokenType.OVER_KW;
import static org.ooc.frontend.model.tokens.Token.TokenType.PERCENT;
import static org.ooc.frontend.model.tokens.Token.TokenType.PLUS;
import static org.ooc.frontend.model.tokens.Token.TokenType.RETURN_KW;
import static org.ooc.frontend.model.tokens.Token.TokenType.SEMICOL;
import static org.ooc.frontend.model.tokens.Token.TokenType.SLASH;
import static org.ooc.frontend.model.tokens.Token.TokenType.SL_COMMENT;
import static org.ooc.frontend.model.tokens.Token.TokenType.STAR;
import static org.ooc.frontend.model.tokens.Token.TokenType.STATIC_KW;
import static org.ooc.frontend.model.tokens.Token.TokenType.STRING_LIT;
import static org.ooc.frontend.model.tokens.Token.TokenType.THIS_KW;
import static org.ooc.frontend.model.tokens.Token.TokenType.TRIPLE_DOT;
import static org.ooc.frontend.model.tokens.Token.TokenType.TRUE;
import static org.ooc.frontend.model.tokens.Token.TokenType.WHILE_KW;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.ooc.frontend.model.Access;
import org.ooc.frontend.model.Add;
import org.ooc.frontend.model.Argument;
import org.ooc.frontend.model.ArrayAccess;
import org.ooc.frontend.model.Assignment;
import org.ooc.frontend.model.BoolLiteral;
import org.ooc.frontend.model.CharLiteral;
import org.ooc.frontend.model.ClassDecl;
import org.ooc.frontend.model.Conditional;
import org.ooc.frontend.model.ControlStatement;
import org.ooc.frontend.model.CoverDecl;
import org.ooc.frontend.model.Declaration;
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
import org.ooc.frontend.model.Literal;
import org.ooc.frontend.model.MemberAccess;
import org.ooc.frontend.model.MemberArgument;
import org.ooc.frontend.model.MemberAssignArgument;
import org.ooc.frontend.model.MemberCall;
import org.ooc.frontend.model.Mod;
import org.ooc.frontend.model.Mul;
import org.ooc.frontend.model.NodeList;
import org.ooc.frontend.model.Not;
import org.ooc.frontend.model.NullLiteral;
import org.ooc.frontend.model.NumberLiteral;
import org.ooc.frontend.model.OocDocComment;
import org.ooc.frontend.model.Parenthesis;
import org.ooc.frontend.model.RangeLiteral;
import org.ooc.frontend.model.RegularArgument;
import org.ooc.frontend.model.SingleLineComment;
import org.ooc.frontend.model.SourceUnit;
import org.ooc.frontend.model.Statement;
import org.ooc.frontend.model.StringLiteral;
import org.ooc.frontend.model.Sub;
import org.ooc.frontend.model.Tokenizer;
import org.ooc.frontend.model.Type;
import org.ooc.frontend.model.ValuedReturn;
import org.ooc.frontend.model.VarArg;
import org.ooc.frontend.model.VariableAccess;
import org.ooc.frontend.model.VariableDecl;
import org.ooc.frontend.model.VariableDeclAssigned;
import org.ooc.frontend.model.Visitable;
import org.ooc.frontend.model.While;
import org.ooc.frontend.model.FunctionDecl.FunctionDeclType;
import org.ooc.frontend.model.NumberLiteral.Format;
import org.ooc.frontend.model.tokens.ListReader;
import org.ooc.frontend.model.tokens.Token;
import org.ubi.CompilationFailedError;
import org.ubi.SourceReader;
import org.ubi.SyntaxError;

public class Parser {

	public Parser() {
		
	}
	
	public SourceUnit parse(File file) throws IOException {

		SourceReader sReader = SourceReader.getReaderFromFile(file);
		List<Token> tokens = new Tokenizer().parse(sReader);
		SourceUnit unit = sourceUnit(sReader, new ListReader<Token>(tokens));
		return unit;
		
	}
	
	private SourceUnit sourceUnit(SourceReader sReader, ListReader<Token> reader) throws IOException {
		
		SourceUnit unit = new SourceUnit(sReader.getLocation().getFileName());
		
		while(reader.hasNext()) {
			
			Declaration declaration = declaration(sReader, reader);
			if(declaration != null) {
				unit.getBody().add(declaration);
				continue;
			}
			
			if(include(sReader, reader, unit.getIncludes())) {
				continue;
			}
			
			if(importStatement(sReader, reader, unit.getImports())) {
				continue;
			}
			
			if(comment(sReader, reader) != null) {
				// TODO store comments somewhere..
				continue;
			}
			
			throw new CompilationFailedError(sReader.getLocation(reader.prev().start + reader.prev().length),
					"Expected declaration, include, or import in source unit");
			
		}
		
		return unit;
		
	}
	
	private Visitable comment(SourceReader sReader, ListReader<Token> reader) {
		
		Token t = reader.peek();
		
		if(t.type == SL_COMMENT) {
			reader.skip();
			return new SingleLineComment(t.get(sReader));
		}

		if(t.type == ML_COMMENT) {
			reader.skip();
			
		}
		
		return null;
		
	}

	private boolean include(SourceReader sReader,	ListReader<Token> reader, NodeList<Include> includes) throws EOFException, CompilationFailedError {

		if(reader.peek().type != INCLUDE_KW) {
			return false;
		}
		reader.skip();
		
		StringBuilder sb = new StringBuilder();
		
		while(true) {
		
			Token t = reader.read();
			if(t.type == SEMICOL) {
				includes.add(new Include(sb.toString()));
				break;
			}
			if(t.type == COMMA) {
				includes.add(new Include(sb.toString()));
				sb.setLength(0);
			} else if(t.type == NAME) {
				sb.append(t.get(sReader));
			} else if(t.type == SLASH) {
				sb.append('/');
			} else {
				throw new CompilationFailedError(sReader.getLocation(t.start), "Unexpected token "+t.type);
			}
			
		}
		
		return true;
		
	}
	
	private boolean importStatement(SourceReader sReader,	ListReader<Token> reader, NodeList<Import> imports) throws EOFException, CompilationFailedError {

		if(reader.peek().type != IMPORT_KW) {
			return false;
		}
		reader.skip();
		
		StringBuilder sb = new StringBuilder();
		
		while(true) {
		
			Token t = reader.read();
			if(t.type == SEMICOL) {
				imports.add(new Import(sb.toString()));
				break;
			}
			if(t.type == COMMA) {
				imports.add(new Import(sb.toString()));
				sb.setLength(0);
			} else if(t.type == NAME) {
				sb.append(t.get(sReader));
			} else if(t.type == DOT) {
				sb.append('.');
			} else {
				throw new CompilationFailedError(sReader.getLocation(t.start), "Unexpected token "+t.type);
			}
			
		}
		
		return true;
		
	}

	private Line line(SourceReader sReader, ListReader<Token> reader) throws IOException {

		int mark = reader.mark();
		
		if(reader.peek().type == SL_COMMENT) {
			Token t = reader.read();
			return new SingleLineComment(t.get(sReader));
		}
		
		Statement statement = statement(sReader, reader);
		if(statement != null) {
			// control statements (if, else, for, version, etc.) don't need a semicolon
			if(!(statement instanceof ControlStatement) && reader.read().type != SEMICOL) {
				throw new CompilationFailedError(sReader.getLocation(reader.prev(2).start),
						"Missing semi-colon at the end of a line (got a "+reader.prev(2).type+" instead)");
			}
			return new Line(statement);
		}
		
		reader.reset(mark);
		return null;
		
	}

	private Statement statement(SourceReader sReader, ListReader<Token> reader) throws IOException {

		int mark = reader.mark();
		
		//System.out.println("Parsing statement, trying function call");
		
		Foreach foreach = foreach(sReader, reader);
		if(foreach != null) {
			return foreach;
		}
		
		Conditional conditional = conditional(sReader, reader);
		if(conditional != null) {
			return conditional;
		}
		
		ValuedReturn ret = returnStatement(sReader, reader);
		if(ret != null) {
			return ret;
		}
		
		Expression expression = expression(sReader, reader);
		if(expression != null) {
			return expression;
		}
		
		Instantiation inst = instantiation(sReader, reader);
		if(inst != null) {
			return inst;
		}
		
		FunctionCall call = functionCall(sReader, reader);
		if(call != null) {
			return call;
		}
		
		//System.out.println("Parsing statement, trying assignment");
		
		Assignment ass = assignment(sReader, reader);
		if(ass != null) {
			return ass;
		}
		
		//System.out.println("Parsing statement, trying declaration");
		
		Declaration decl = declaration(sReader, reader);
		if(decl != null) {
			return decl;
		}
		
		reader.reset(mark);
		return null;
		
	}
	
	private Conditional conditional(
			SourceReader sReader, ListReader<Token> reader) throws IOException {
		
		int mark = reader.mark();
		
		Token token = reader.read();
		if(token.type != WHILE_KW && token.type != IF_KW) {
			reader.reset(mark);
			return null;
		}
		
		Expression condition = expression(sReader, reader);
		if(condition == null) {
			throw new CompilationFailedError(sReader.getLocation(reader.peek().start),
					"Expected expression as while condition");
		}
		
		Conditional statement;
		if(token.type == WHILE_KW) {
			statement = new While(condition);
		} else if(token.type == IF_KW) {
			statement = new If(condition);
		} else {
			reader.reset(mark);
			return null;
		}
		fillControlStatement(sReader, reader, statement);
		return statement;
		
	}
	
	private Foreach foreach(SourceReader sReader, ListReader<Token> reader) throws IOException {

		int mark = reader.mark();
		
		if(reader.read().type == FOR_KW) {
			boolean hasParen = false;
			if(reader.peek().type == OPEN_PAREN) {
				reader.skip();
				hasParen = true;
			}
			
			VariableDecl variable = variableDecl(sReader, reader);
			if(variable == null) {
				return null;
			}
			
			if(reader.read().type != COL) {
				return null;
			}
			
			//System.out.println("Attempting to parse collection expression in foreach");
			Expression collection = expression(sReader, reader);
			if(collection == null) {
				throw new CompilationFailedError(sReader.getLocation(reader.peek().start),
						"Expected expression after colon in a foreach");
			}
			
			if(hasParen && reader.read().type != CLOS_PAREN) {
				throw new CompilationFailedError(sReader.getLocation(reader.prev().start),
				"Expected closing parenthesis at the end of a parenthesized foreach");
			}
			
			Foreach foreach = new Foreach(variable, collection);
			fillControlStatement(sReader, reader, foreach);
			return foreach;
			
		}
		
		reader.reset(mark);
		return null;
		
	}

	private void fillControlStatement(SourceReader sReader,
			ListReader<Token> reader, ControlStatement controlStatement)
			throws EOFException, IOException, CompilationFailedError {
		
		boolean hasBrack = false;
		if(reader.peek().type == OPEN_BRACK) {
			reader.skip();
			hasBrack = true;
		}
		
		if(hasBrack) {
			
			//System.out.println("Parsing a bracketed "+controlStatement.getClass().getSimpleName()
					//+" at "+sReader.getLocation(reader.peek().start));
			//System.out.println("Current token is a "+reader.peek().type);
			
			while(reader.peek().type != CLOS_BRACK) {
				Line line = line(sReader, reader);
				if(line == null) {
					throw new CompilationFailedError(sReader.getLocation(reader.peek().start),
					"Expected line inside of "+controlStatement.getClass().getSimpleName());
				}
				controlStatement.getBody().add(line);
			}
			reader.skip(); // the closing bracket
			
		} else {
			
			Line only = line(sReader, reader);
			if(only == null) {
				throw new CompilationFailedError(sReader.getLocation(reader.peek().start),
				"Expected line inside of bracket-less "+controlStatement.getClass().getSimpleName());
			}
			controlStatement.getBody().add(only);

		}
	}

	private Instantiation instantiation(SourceReader sReader, ListReader<Token> reader) throws IOException {

		int mark = reader.mark();
		
		if(reader.read().type == NEW_KW) {
			Token tName = reader.peek();
			if(tName.type == NAME) {
				reader.skip();
				String name = tName.get(sReader);
				Instantiation inst = new Instantiation(name, ""); // TODO add '#' parsing.
				exprList(sReader, reader, inst.getArguments()); // we don't care whether we have args or not
				return inst;
			}
			Instantiation inst = new Instantiation();
			exprList(sReader, reader, inst.getArguments()); // we don't care whether we have args or not
			return inst;
		}
		
		reader.reset(mark);
		return null;
		
	}

	private ValuedReturn returnStatement(SourceReader sReader,
			ListReader<Token> reader) throws IOException {

		int mark = reader.mark();
		
		if(reader.read().type == RETURN_KW) {
			Expression expr = expression(sReader, reader);
			if(expr == null) {
				throw new CompilationFailedError(sReader.getLocation(reader.peek().start),
						"Expecting expression after keyword");
			}
			return new ValuedReturn(expr);
		}
		
		reader.reset(mark);
		return null;
		
	}

	private FunctionCall functionCall(SourceReader sReader, ListReader<Token> reader) throws IOException {
		
		int mark = reader.mark();
		
		Token tName = reader.read();
		if(tName.type != NAME) {
			reader.reset(mark);
			return null;
		}
		String name = tName.get(sReader);
		
		String suffix = "";
		if(reader.peek().type == HASH) {
			reader.skip();
			Token tSuff = reader.read();
			if(tSuff.type != NAME) {
				throw new CompilationFailedError(sReader.getLocation(tSuff.start),
				"Expecting suffix after 'functionname#' !");
			}
			suffix = tSuff.get(sReader);
		}

		FunctionCall call = new FunctionCall(name, suffix);
		
		if(!exprList(sReader, reader, call.getArguments())) {
			reader.reset(mark);
			return null; // not a function call
		}
		
		//System.out.println("Parsed function call, ended on token "+reader.peek().type);
		
		return call;
		
	}

	private boolean exprList(SourceReader sReader, ListReader<Token> reader, NodeList<Expression> list) throws IOException {

		int mark = reader.mark();
		
		if(reader.read().type != OPEN_PAREN) {
			reader.reset(mark);
			return false;
		}
		
		boolean comma = false;
		while(true) {
			
			if(reader.peek().type == CLOS_PAREN) {
				reader.skip(); // skip the ')'
				break;
			}
			if(comma) {
				if(reader.read().type != COMMA) {
					throw new CompilationFailedError(sReader.getLocation(reader.prev().start), "Expected comma between arguments of a function call");
				}
			} else {
				Expression expr = expression(sReader, reader);
				if(expr == null) {
					throw new CompilationFailedError(sReader.getLocation(reader.peek().start), "Expected expression as argument of function call");
				}
				list.add(expr);
			}
			comma = !comma;
			
		}
		
		return true;
		
	}
	
	private Declaration declaration(SourceReader sReader, ListReader<Token> reader) throws IOException {

		int mark = reader.mark();
		
		//System.out.println("Parsing declaration, trying variable declaration");
		
		VariableDecl varDecl = variableDecl(sReader, reader);
		if(varDecl != null) {
			return varDecl;
		}
		
		//System.out.println("Parsing declaration, trying function declaration");
		
		FunctionDecl funcDecl = functionDecl(sReader, reader);
		if(funcDecl != null) {
			return funcDecl;
		}
		
		ClassDecl classDecl = classDecl(sReader, reader);
		if(classDecl != null) {
			return classDecl;
		}
		
		CoverDecl coverDecl = coverDecl(sReader, reader);
		if(coverDecl != null) {
			return coverDecl;
		}
		
		reader.reset(mark);
		return null;
		
	}
	
	private VariableDecl variableDecl(SourceReader sReader, ListReader<Token> reader) throws IOException {

		int mark = reader.mark();
		
		//System.out.println("Parsing variable declaration");
		
		boolean isConst = false;
		boolean isStatic = false;
		
		while(true) {
			Token t = reader.peek();
			if(t.type == CONST_KW) {
				isConst = true;
				reader.skip();
			} else if(t.type == STATIC_KW) {
				isStatic = true;
				reader.skip();
			} else {
				break;
			}
		}
		
		Type type = type(sReader, reader);
		if(type != null) {
			Token t = reader.peek();
			if(t.type == NAME) {
				reader.skip();
				Token t2 = reader.peek();
				if(t2.type == ASSIGN) {
					reader.skip();
					Expression expr = expression(sReader, reader);
					if(expr == null) {
						throw new CompilationFailedError(sReader.getLocation(t2.start),
								"Expected expression as an initializer to a variable declaration.");
					}
					return new VariableDeclAssigned(type, t.get(sReader), expr, isConst, isStatic);
				}
				return new VariableDecl(type, t.get(sReader), isConst, isStatic);
			}
		}
		
		reader.reset(mark);
		return null;
		
	}
	
	private CoverDecl coverDecl(SourceReader sReader, ListReader<Token> reader) throws IOException {

		int mark = reader.mark();
		
		OocDocComment comment = null;
		if(reader.peek().type == OOCDOC) {
			Token t = reader.read();
			comment = new OocDocComment(t.get(sReader));
		}
		
		if(reader.read().type == COVER_KW) {
			
			Token t = reader.read();
			if(t.type != NAME) {
				throw new CompilationFailedError(sReader.getLocation(t.start),
						"Expected cover name after the cover keyword.");
			}
			
			Type fromType = null;
			if(reader.peek().type == FROM_KW) {
				reader.skip();
				fromType = type(sReader, reader);
				if(fromType == null) {
					throw new CompilationFailedError(sReader.getLocation(reader.peek().start),
					"Expected cover's base type name after the from keyword.");
				}
			}
			
			CoverDecl coverDecl = new CoverDecl(t.get(sReader), fromType);
			if(comment != null) coverDecl.setComment(comment);
			
			Token t2 = reader.read();
			if(t2.type != OPEN_BRACK) {
				if(t2.type == SEMICOL) {
					return coverDecl; // empty cover, acts like a typedef
				}
				throw new CompilationFailedError(sReader.getLocation(t2.start),
						"Expected opening bracket to begin cover declaration.");
			}
			
			while(reader.peek().type != CLOS_BRACK) {
			
				VariableDecl varDecl = variableDecl(sReader, reader);
				if(varDecl != null) {
					if(reader.read().type != SEMICOL) {
						throw new CompilationFailedError(sReader.getLocation(reader.prev().start),
							"Expected semi-colon after variable declaration in class declaration");
					}
					if(fromType != null) {
						throw new CompilationFailedError(sReader.getLocation(reader.prev().start),
							"You can't add member variables to a Cover which already has a base type (in this case, "
								+fromType.getName()+")");
					}
					coverDecl.getVariables().add(varDecl);
					continue;
				}
				
				FunctionDecl funcDecl = functionDecl(sReader,reader);
				if(funcDecl != null) {
					coverDecl.getFunctions().add(funcDecl);
					continue;
				}
				
				throw new CompilationFailedError(sReader.getLocation(reader.peek().start),
						"Expected variable declaration or function declaration in a class declaration");
			
			}
			reader.skip();
			
			return coverDecl;
			
		}
		
		reader.reset(mark);
		return null;
		
	}
	
	private ClassDecl classDecl(SourceReader sReader, ListReader<Token> reader) throws IOException {

		int mark = reader.mark();
		
		OocDocComment comment = null;
		if(reader.peek().type == OOCDOC) {
			Token t = reader.read();
			comment = new OocDocComment(t.get(sReader));
		}
		
		boolean isAbstract = reader.peek().type == ABSTRACT_KW;
		if(isAbstract) {
			reader.skip();
		}
		
		if(reader.read().type == CLASS_KW) {
			
			Token t = reader.read();
			if(t.type != NAME) {
				throw new CompilationFailedError(sReader.getLocation(t.start),
						"Expected class name after the class keyword.");
			}
			
			String superName = "";
			if(reader.peek().type == FROM_KW) {
				reader.skip();
				Token tSuper = reader.read();
				if(tSuper.type != NAME) {
					throw new CompilationFailedError(sReader.getLocation(reader.peek().start),
					"Expected super-class name after the from keyword.");
				}
				superName = tSuper.get(sReader);
				
			}
			
			Token t2 = reader.read();
			if(t2.type != OPEN_BRACK) {
				throw new CompilationFailedError(sReader.getLocation(t2.start),
						"Expected opening bracket to begin class declaration.");
			}
			
			ClassDecl classDecl = new ClassDecl(t.get(sReader), isAbstract);
			classDecl.setSuperName(superName);
			if(comment != null) classDecl.setComment(comment);
			
			while(reader.peek().type != CLOS_BRACK) {
			
				VariableDecl varDecl = variableDecl(sReader, reader);
				if(varDecl != null) {
					if(reader.read().type != SEMICOL) {
						throw new CompilationFailedError(sReader.getLocation(reader.prev().start),
							"Expected semi-colon after variable declaration in class declaration");
					}
					classDecl.getVariables().add(varDecl);
					continue;
				}
				
				FunctionDecl funcDecl = functionDecl(sReader,reader);
				if(funcDecl != null) {
					classDecl.getFunctions().add(funcDecl);
					continue;
				}
				
				throw new CompilationFailedError(sReader.getLocation(reader.peek().start),
						"Expected variable declaration or function declaration in a class declaration");
			
			}
			reader.skip();
			
			return classDecl;
			
		}
		
		reader.reset(mark);
		return null;
		
	}
	
	private FunctionDecl functionDecl(SourceReader sReader, ListReader<Token> reader) throws IOException {

		int mark = reader.mark();
		
		OocDocComment comment = null;
		if(reader.peek().type == OOCDOC) {
			Token t = reader.read();
			comment = new OocDocComment(t.get(sReader));
		}
		
		boolean isAbstract = false;
		
		FunctionDeclType declType = FunctionDeclType.FUNC;
		if(reader.peek().type == IMPL_KW) {
			reader.skip();
			declType = FunctionDeclType.IMPL;
		} else if(reader.peek().type == OVER_KW) {
			reader.skip();
			declType = FunctionDeclType.OVER;
		} else if(reader.peek().type == EXTERN_KW) {
			reader.skip();
			declType = FunctionDeclType.EXTERN;
			if(reader.read().type != FUNC_KW) {
				
			}
		} else {
			while(reader.peek().type != FUNC_KW) {
				Token t = reader.read();
				if(t.type == ABSTRACT_KW) {
					isAbstract = true;
				} else {
					reader.reset(mark);
					return null;
				}
			}
			reader.skip(); // the 'func' keyword
		}
		
		Token tName = reader.read();
		if(tName.type != NAME && tName.type != NEW_KW) {
			throw new CompilationFailedError(sReader.getLocation(tName.start),
					"Expected function name after 'func' keyword");
		}
		String name = tName.get(sReader);
		
		String suffix = "";
		if(reader.peek().type == HASH) {
			reader.skip();
			Token tSuffix = reader.read();
			if(tSuffix.type != NAME) {
				throw new CompilationFailedError(sReader.getLocation(tSuffix.start),
					"Expected suffix after function name and '#'");
			}
			suffix = tSuffix.get(sReader);
		}
		
		FunctionDecl functionDecl = new FunctionDecl(declType,
				name, suffix, isAbstract);
		if(comment != null) functionDecl.setComment(comment);
		
		if(reader.peek().type == OPEN_PAREN) {
			reader.skip();
			boolean comma = false;
			while(true) {
				
				if(reader.peek().type == CLOS_PAREN) {
					reader.skip(); // skip the ')'
					break;
				}
				if(comma) {
					if(reader.read().type != COMMA) {
						throw new CompilationFailedError(sReader.getLocation(reader.prev().start),
								"Expected comma between arguments of a function definition");
					}
				} else {
					if(declType == FunctionDeclType.FUNC) {
						Argument arg = argument(sReader, reader);
						if(arg == null) {
							throw new CompilationFailedError(sReader.getLocation(reader.peek().start),
									"Expected variable declaration as an argument of a function definition");
						}
						functionDecl.getArguments().add(arg);
					} else {
						Type type = type(sReader, reader);
						if(type == null) {
							if(reader.peek().type == TRIPLE_DOT) {
								reader.skip();
								functionDecl.getArguments().add(new VarArg());
							} else {
								throw new CompilationFailedError(sReader.getLocation(reader.peek().start),
									"Expected type as an argument of an extern function definition");
							}
						} else {
							functionDecl.getArguments().add(new TypeArgument(type));
						}
					}
				}
				comma = !comma;
				
			}
		}
		
		Token t = reader.read();
		if(t.type == ARROW) {
			Type returnType = type(sReader, reader);
			if(returnType == null) {
				throw new CompilationFailedError(sReader.getLocation(reader.peek().start), "Expected return type after arrow");
			}
			functionDecl.setReturnType(returnType);
			t = reader.read();
		}
		if(t.type == SEMICOL) {
			return functionDecl;
		}
		if(t.type != OPEN_BRACK) {
			throw new CompilationFailedError(sReader.getLocation(reader.prev().start), "Expected opening brace after function name.");
		}
	
		while(reader.peek().type != CLOS_BRACK) {
		
			Line line = line(sReader, reader);
			if(line == null) {
				throw new CompilationFailedError(sReader.getLocation(reader.peek().start), "Expected statement in function body.");
			}
			functionDecl.getBody().add(line);
		
		}
		reader.skip();
		
		if(functionDecl.getName().equals("main")) {
			functionDecl.setReturnType(new Type("int"));
		}
		
		return functionDecl;
		
	}
	
	private Argument argument(SourceReader sReader, ListReader<Token> reader) throws IOException {

		int mark = reader.mark();
		
		if(reader.peek().type == TRIPLE_DOT) {
			reader.skip();
			return new VarArg();
		}
		
		Type type = type(sReader, reader);
		if(type != null) {
			Token t = reader.peek();
			if(t.type == NAME) {
				reader.skip();
				return new RegularArgument(type, t.get(sReader));
			}
		}
		reader.reset(mark);
		
		Token t = reader.read();
		if(t.type == ASSIGN) {
			Token t2 = reader.read();
			if(t2.type != NAME) {
				throw new CompilationFailedError(sReader.getLocation(t2.start),
						"Expecting member variable name in member-assign-argument");
			}
			return new MemberAssignArgument(t2.get(sReader));
		}
		
		if(t.type != NAME) {
			throw new CompilationFailedError(sReader.getLocation(t.start),
			"Expecting member variable name in member-assign-argument");
		}
		return new MemberArgument(t.get(sReader));
		
	}

	private Assignment assignment(SourceReader sReader, ListReader<Token> reader) throws IOException {
		
		int mark = reader.mark();

		//System.out.println("Parsing an assignment");
		
		Access lvalue = access(sReader, reader);
		//System.out.println("Got lvalue "+lvalue);
		if(lvalue != null) {
			Token t = reader.peek();
			if(t.type == ASSIGN) {
				reader.skip();
				Expression rvalue = expression(sReader, reader);
				if(rvalue != null) {
					return new Assignment(lvalue, rvalue);
				}
			}
		}
		
		reader.reset(mark);
		//System.out.println("Didn't find anything to assign, resetting... Now next token is "+reader.peek().type);
		return null;
		
	}
	
	private Access access(SourceReader sReader, ListReader<Token> reader) {
		
		int mark = reader.mark();
		
		//System.out.println("Attempting to read varAccess");
		VariableAccess varAcc = variableAccess(sReader, reader);
		if(varAcc != null) {
			return varAcc;
		}
		
		reader.reset(mark);
		return null;
		
	}

	private VariableAccess variableAccess(SourceReader sReader, ListReader<Token> reader) {
		
		int mark = reader.mark();
		
		Token t = reader.peek();
		//System.out.println("Got token "+t.type);
		if(t.type == NAME || t.type == THIS_KW) {
			reader.skip();
			return new VariableAccess(t.get(sReader));
		}
		
		reader.reset(mark);
		return null;
		
	}
	
	private Type type(SourceReader sReader, ListReader<Token> reader) {
		
		Token t = reader.peek();
		if(t.type == NAME) {
			reader.skip();
			int pointerLevel = 0;
			while(reader.peek().type == STAR) {
				reader.skip();
				pointerLevel++;
			}
			return new Type(t.get(sReader), pointerLevel);
		}
		
		return null;
		
	}
	
	private Expression expression(SourceReader sReader, ListReader<Token> reader) throws IOException {
		
		int mark = reader.mark();
		
		if(reader.peek().type == EXCL) {
			reader.skip();
			Expression inner = expression(sReader, reader);
			if(inner == null) {
				reader.reset(mark);
				return null;
			}
			System.out.println("Got NOT of a "+inner.getClass().getSimpleName());
			return new Not(inner);
		}
		
		Expression expr = flatExpression(sReader, reader);
		if(expr == null) {
			//System.out.println("Null flat expression");
			return null;
		}
		
		//int count = 1;
		
		while(true) {
			
			//System.out.println("Got #" + (count++) + " expression "+expr.getClass().getSimpleName());
			
			Token t = reader.peek();
			if(t.type == DOT) {
				
				reader.skip();
				FunctionCall call = functionCall(sReader, reader);
				if(call != null) {
					expr = new MemberCall(expr, call);
					continue;
				}
				
				VariableAccess varAccess = variableAccess(sReader, reader);
				if(varAccess != null) {
					expr = new MemberAccess(expr, varAccess);
					continue;
				}
				
			}
			
			if(t.type == DOUBLE_DOT) {
				
				reader.skip();
				Expression upper = expression(sReader, reader);
				if(upper == null) {
					throw new CompilationFailedError(sReader.getLocation(reader.peek().start),
							"Expected expression for the upper part of a range literal");
				}
				// this is so beautiful it makes me wanna cry
				expr = new RangeLiteral(expr, upper);
				
			}
			
			if(t.type == OPEN_SQUAR) {

				reader.skip();
				Expression index = expression(sReader, reader);
				if(index == null) {
					throw new CompilationFailedError(sReader.getLocation(reader.peek().start),
						"Expected expression for the index of an array access");
				}
				//System.out.println("Got expression which is really a "+index.getClass().getSimpleName());
				//System.out.println("Next is a "+reader.peek().type);
				if(reader.read().type != CLOS_SQUAR) {
					throw new CompilationFailedError(sReader.getLocation(reader.prev().start),
						"Expected closing bracket to end array access, got "+reader.prev().type+" instead.");
				}
				//System.out.println("Did arrayAccess just fine, next is a "+reader.peek().type);
				expr = new ArrayAccess(expr, index);
				continue;
				
			}
			
			if(t.type == ASSIGN) {
				
				reader.skip();
				Expression rvalue = expression(sReader, reader);
				if(rvalue == null) {
					throw new CompilationFailedError(sReader.getLocation(reader.peek().start),
						"Expected rvalue for assignment");
				}
				if(!(expr instanceof Access)) {
					throw new CompilationFailedError(sReader.getLocation(reader.peek().start),
						"Trying to assign something which is not an access (ie. not a lvalue)");
				}
				expr = new Assignment((Access) expr, rvalue);
				continue;
				
			}
			
			if(t.type == PLUS || t.type == STAR
					|| t.type == MINUS || t.type == SLASH || t.type == PERCENT) {
				
				reader.skip();
				Expression rvalue = expression(sReader, reader);
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
					default: throw new CompilationFailedError(sReader.getLocation(reader.prev().start),
							"Unknown binary operation yet");
				}
				continue;
				
				
			}
			
			return expr;
			
		}
		
	}
	
	private Expression flatExpression(SourceReader sReader, ListReader<Token> reader) throws IOException {
		
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
			expression = expression(sReader, reader);
		} else {
			expression = flatUnparenExpression(sReader, reader);
		}
		
		if(expression == null) {
			if(parenNumber > 0) {
				throw new CompilationFailedError(sReader.getLocation(reader.peek().start),
					"Expected expression into parenthesis");
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
	
	private Expression flatUnparenExpression(SourceReader sReader, ListReader<Token> reader) throws IOException {
		
		int mark = reader.mark();
		
		//System.out.println("Parsing flatUnparenExpression");
		
		//System.out.println("Attempting to parse literal for flatUnparenExpression (next is "+reader.peek().type+")");
		Literal literal = literal(sReader, reader);
		if(literal != null) {
			return literal;
		}
		
		//System.out.println("Attempting to parse assignment for flatUnparenExpression (next is "+reader.peek().type+")");
		Assignment ass = assignment(sReader, reader);
		if(ass != null) {
			return ass;
		}
		
		//System.out.println("Attempting to parse declaration for flatUnparenExpression (next is "+reader.peek().type+")");
		Declaration declaration = declaration(sReader, reader);
		if(declaration != null) {
			return declaration;
		}
		
		//System.out.println("Attempting to parse instantiation for flatUnparenExpression (next is "+reader.peek().type+")");
		Instantiation instantiation = instantiation(sReader, reader);
		if(instantiation != null) {
			return instantiation;
		}
		
		//System.out.println("Attempting to parse funcCall for flatUnparenExpression (next is "+reader.peek().type+")");
		FunctionCall funcCall = functionCall(sReader, reader);
		if(funcCall != null) {
			return funcCall;
		}
		
		//System.out.println("Attempting to parse access for flatUnparenExpression (next is "+reader.peek().type+")");
		Access access = access(sReader, reader);
		if(access != null) {
			return access;
		}
		
		reader.reset(mark);
		return null;
		
	}

	private Literal literal(SourceReader sReader, ListReader<Token> reader) throws IOException {

		int mark = reader.mark();
		
		Token t = reader.read();
		if(t.type == STRING_LIT) {
			return new StringLiteral(t.get(sReader));
		}
		if(t.type == CHAR_LIT) {
			try {
				return new CharLiteral(SourceReader.parseCharLiteral(t.get(sReader)));
			} catch (SyntaxError e) {
				throw new CompilationFailedError(sReader.getLocation(t.start), "Malformed char literal");
			}
		}
		if(t.type == DEC_NUMBER) {
			return new NumberLiteral(Long.parseLong(t.get(sReader)), Format.DEC);
		}
		if(t.type == HEX_NUMBER) {
			return new NumberLiteral(Long.parseLong(t.get(sReader).toUpperCase(), 16), Format.HEX);
		}
		if(t.type == OCT_NUMBER) {
			return new NumberLiteral(Long.parseLong(t.get(sReader).toUpperCase(), 8), Format.OCT);
		}
		if(t.type == BIN_NUMBER) {
			return new NumberLiteral(Long.parseLong(t.get(sReader).toUpperCase(), 2), Format.BIN);
		}
		if(t.type == TRUE) {
			return new BoolLiteral(true);
		}
		if(t.type == FALSE) {
			return new BoolLiteral(false);
		}
		if(t.type == NULL) {
			return new NullLiteral();
		}
		
		reader.reset(mark);
		return null;
		
	}
		
}
