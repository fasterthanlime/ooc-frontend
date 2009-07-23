package org.ooc.frontend.parser;

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
import org.ooc.frontend.model.Mul;
import org.ooc.frontend.model.NodeList;
import org.ooc.frontend.model.Not;
import org.ooc.frontend.model.NullLiteral;
import org.ooc.frontend.model.NumberLiteral;
import org.ooc.frontend.model.OocDocComment;
import org.ooc.frontend.model.Parenthesis;
import org.ooc.frontend.model.RangeLiteral;
import org.ooc.frontend.model.RegularArgument;
import org.ooc.frontend.model.Return;
import org.ooc.frontend.model.SingleLineComment;
import org.ooc.frontend.model.SourceUnit;
import org.ooc.frontend.model.Statement;
import org.ooc.frontend.model.StringLiteral;
import org.ooc.frontend.model.Sub;
import org.ooc.frontend.model.Tokenizer;
import org.ooc.frontend.model.Type;
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
import org.ooc.frontend.model.tokens.Token.TokenType;
import org.ubi.CompilationFailedError;
import org.ubi.SourceReader;
import org.ubi.SyntaxError;

public class Parser {

	public Parser() {
		
	}
	
	public SourceUnit parse(File file) throws IOException {

		SourceReader sourceReader = SourceReader.getReaderFromFile(file);
		List<Token> tokens = new Tokenizer().parse(sourceReader);
		SourceUnit unit = sourceUnit(sourceReader, new ListReader<Token>(tokens));
		return unit;
		
	}
	
	private SourceUnit sourceUnit(SourceReader sourceReader, ListReader<Token> reader) throws IOException {
		
		SourceUnit unit = new SourceUnit(sourceReader.getLocation().getFileName());
		
		while(reader.hasNext()) {
			
			Declaration declaration = declaration(sourceReader, reader);
			if(declaration != null) {
				unit.getBody().add(declaration);
				continue;
			}
			
			if(include(sourceReader, reader, unit.getIncludes())) {
				continue;
			}
			
			if(importStatement(sourceReader, reader, unit.getImports())) {
				continue;
			}
			
			if(comment(sourceReader, reader) != null) {
				// TODO store comments somewhere..
				continue;
			}
			
			throw new CompilationFailedError(sourceReader.getLocation(reader.prev().start + reader.prev().length),
					"Expected declaration, include, or import in source unit");
			
		}
		
		return unit;
		
	}
	
	private Visitable comment(SourceReader sourceReader, ListReader<Token> reader) {
		
		Token t = reader.peek();
		
		if(t.type == TokenType.SL_COMMENT) {
			reader.skip();
			return new SingleLineComment(sourceReader.getSlice(t.start, t.length));
		}

		if(t.type == TokenType.ML_COMMENT) {
			reader.skip();
			
		}
		
		return null;
		
	}

	private boolean include(SourceReader sourceReader,	ListReader<Token> reader, NodeList<Include> includes) throws EOFException, CompilationFailedError {

		if(reader.peek().type != TokenType.INCLUDE_KW) {
			return false;
		}
		reader.skip();
		
		StringBuilder sb = new StringBuilder();
		
		while(true) {
		
			Token t = reader.read();
			if(t.type == TokenType.SEMICOL) {
				includes.add(new Include(sb.toString()));
				break;
			}
			if(t.type == TokenType.COMMA) {
				includes.add(new Include(sb.toString()));
				sb.setLength(0);
			} else if(t.type == TokenType.NAME) {
				sb.append(sourceReader.getSlice(t.start, t.length));
			} else if(t.type == TokenType.SLASH) {
				sb.append('/');
			} else {
				throw new CompilationFailedError(sourceReader.getLocation(t.start), "Unexpected token "+t.type);
			}
			
		}
		
		return true;
		
	}
	
	private boolean importStatement(SourceReader sourceReader,	ListReader<Token> reader, NodeList<Import> imports) throws EOFException, CompilationFailedError {

		if(reader.peek().type != TokenType.IMPORT_KW) {
			return false;
		}
		reader.skip();
		
		StringBuilder sb = new StringBuilder();
		
		while(true) {
		
			Token t = reader.read();
			if(t.type == TokenType.SEMICOL) {
				imports.add(new Import(sb.toString()));
				break;
			}
			if(t.type == TokenType.COMMA) {
				imports.add(new Import(sb.toString()));
				sb.setLength(0);
			} else if(t.type == TokenType.NAME) {
				sb.append(sourceReader.getSlice(t.start, t.length));
			} else if(t.type == TokenType.DOT) {
				sb.append('.');
			} else {
				throw new CompilationFailedError(sourceReader.getLocation(t.start), "Unexpected token "+t.type);
			}
			
		}
		
		return true;
		
	}

	private Line line(SourceReader sourceReader, ListReader<Token> reader) throws IOException {

		int mark = reader.mark();
		
		if(reader.peek().type == TokenType.SL_COMMENT) {
			Token t = reader.read();
			return new SingleLineComment(sourceReader.getSlice(t.start, t.length));
		}
		
		Statement statement = statement(sourceReader, reader);
		if(statement != null) {
			// control statements (if, else, for, version, etc.) don't need a semicolon
			if(!(statement instanceof ControlStatement) && reader.read().type != TokenType.SEMICOL) {
				throw new CompilationFailedError(sourceReader.getLocation(reader.prev(2).start),
						"Missing semi-colon at the end of a line (got a "+reader.prev(2).type+" instead)");
			}
			return new Line(statement);
		}
		
		reader.reset(mark);
		return null;
		
	}

	private Statement statement(SourceReader sourceReader, ListReader<Token> reader) throws IOException {

		int mark = reader.mark();
		
		//System.out.println("Parsing statement, trying function call");
		
		Foreach foreach = foreach(sourceReader, reader);
		if(foreach != null) {
			return foreach;
		}
		
		Conditional conditional = conditional(sourceReader, reader);
		if(conditional != null) {
			return conditional;
		}
		
		Return ret = returnStatement(sourceReader, reader);
		if(ret != null) {
			return ret;
		}
		
		Expression expression = expression(sourceReader, reader);
		if(expression != null) {
			return expression;
		}
		
		Instantiation inst = instantiation(sourceReader, reader);
		if(inst != null) {
			return inst;
		}
		
		FunctionCall call = functionCall(sourceReader, reader);
		if(call != null) {
			return call;
		}
		
		//System.out.println("Parsing statement, trying assignment");
		
		Assignment ass = assignment(sourceReader, reader);
		if(ass != null) {
			return ass;
		}
		
		//System.out.println("Parsing statement, trying declaration");
		
		Declaration decl = declaration(sourceReader, reader);
		if(decl != null) {
			return decl;
		}
		
		reader.reset(mark);
		return null;
		
	}
	
	private Conditional conditional(
			SourceReader sourceReader, ListReader<Token> reader) throws IOException {
		
		int mark = reader.mark();
		
		Token token = reader.read();
		if(token.type != TokenType.WHILE_KW && token.type != TokenType.IF_KW) {
			reader.reset(mark);
			return null;
		}
		
		Expression condition = expression(sourceReader, reader);
		if(condition == null) {
			throw new CompilationFailedError(sourceReader.getLocation(reader.peek().start),
					"Expected expression as while condition");
		}
		
		Conditional statement;
		if(token.type == TokenType.WHILE_KW) {
			statement = new While(condition);
		} else if(token.type == TokenType.IF_KW) {
			statement = new If(condition);
		} else {
			reader.reset(mark);
			return null;
		}
		fillControlStatement(sourceReader, reader, statement);
		return statement;
		
	}
	
	private Foreach foreach(SourceReader sourceReader, ListReader<Token> reader) throws IOException {

		int mark = reader.mark();
		
		if(reader.read().type == TokenType.FOR_KW) {
			boolean hasParen = false;
			if(reader.peek().type == TokenType.OPEN_PAREN) {
				reader.skip();
				hasParen = true;
			}
			
			VariableDecl variable = variableDecl(sourceReader, reader);
			if(variable == null) {
				return null;
			}
			
			if(reader.read().type != TokenType.COL) {
				return null;
			}
			
			//System.out.println("Attempting to parse collection expression in foreach");
			Expression collection = expression(sourceReader, reader);
			if(collection == null) {
				throw new CompilationFailedError(sourceReader.getLocation(reader.peek().start),
						"Expected expression after colon in a foreach");
			}
			
			if(hasParen && reader.read().type != TokenType.CLOS_PAREN) {
				throw new CompilationFailedError(sourceReader.getLocation(reader.prev().start),
				"Expected closing parenthesis at the end of a parenthesized foreach");
			}
			
			Foreach foreach = new Foreach(variable, collection);
			fillControlStatement(sourceReader, reader, foreach);
			return foreach;
			
		}
		
		reader.reset(mark);
		return null;
		
	}

	private void fillControlStatement(SourceReader sourceReader,
			ListReader<Token> reader, ControlStatement controlStatement)
			throws EOFException, IOException, CompilationFailedError {
		
		boolean hasBrack = false;
		if(reader.peek().type == TokenType.OPEN_BRACK) {
			reader.skip();
			hasBrack = true;
		}
		
		if(hasBrack) {
			
			//System.out.println("Parsing a bracketed "+controlStatement.getClass().getSimpleName()
					//+" at "+sourceReader.getLocation(reader.peek().start));
			//System.out.println("Current token is a "+reader.peek().type);
			
			while(reader.peek().type != TokenType.CLOS_BRACK) {
				Line line = line(sourceReader, reader);
				if(line == null) {
					throw new CompilationFailedError(sourceReader.getLocation(reader.peek().start),
					"Expected line inside of "+controlStatement.getClass().getSimpleName());
				}
				controlStatement.getBody().add(line);
			}
			reader.skip(); // the closing bracket
			
		} else {
			
			Line only = line(sourceReader, reader);
			if(only == null) {
				throw new CompilationFailedError(sourceReader.getLocation(reader.peek().start),
				"Expected line inside of bracket-less "+controlStatement.getClass().getSimpleName());
			}
			controlStatement.getBody().add(only);

		}
	}

	private Instantiation instantiation(SourceReader sourceReader, ListReader<Token> reader) throws IOException {

		int mark = reader.mark();
		
		if(reader.read().type == TokenType.NEW_KW) {
			Token t = reader.peek();
			if(t.type == TokenType.NAME) {
				reader.skip();
				Instantiation inst = new Instantiation(sourceReader.getSlice(t.start, t.length));
				exprList(sourceReader, reader, inst.getArguments()); // we don't care whether we have args or not
				return inst;
			}
			Instantiation inst = new Instantiation();
			exprList(sourceReader, reader, inst.getArguments()); // we don't care whether we have args or not
			return inst;
		}
		
		reader.reset(mark);
		return null;
		
	}

	private Return returnStatement(SourceReader sourceReader,
			ListReader<Token> reader) throws IOException {

		int mark = reader.mark();
		
		if(reader.read().type == TokenType.RETURN_KW) {
			Expression expr = expression(sourceReader, reader);
			if(expr == null) {
				throw new CompilationFailedError(sourceReader.getLocation(reader.peek().start),
						"Expecting expression after keyword");
			}
			return new Return(expr);
		}
		
		reader.reset(mark);
		return null;
		
	}

	private FunctionCall functionCall(SourceReader sourceReader, ListReader<Token> reader) throws IOException {
		
		int mark = reader.mark();
		
		Token name = reader.read();
		if(name.type != TokenType.NAME) {
			reader.reset(mark);
			return null;
		}
		
		FunctionCall call = new FunctionCall(sourceReader.getSlice(name.start, name.length));
		
		if(!exprList(sourceReader, reader, call.getArguments())) {
			
			reader.reset(mark);
			return null; // not a function call
		}
		
		//System.out.println("Parsed function call, ended on token "+reader.peek().type);
		
		return call;
		
	}

	private boolean exprList(SourceReader sourceReader, ListReader<Token> reader, NodeList<Expression> list) throws IOException {

		int mark = reader.mark();
		
		if(reader.read().type != TokenType.OPEN_PAREN) {
			reader.reset(mark);
			return false;
		}
		
		boolean comma = false;
		while(true) {
			
			if(reader.peek().type == TokenType.CLOS_PAREN) {
				reader.skip(); // skip the ')'
				break;
			}
			if(comma) {
				if(reader.read().type != TokenType.COMMA) {
					throw new CompilationFailedError(sourceReader.getLocation(reader.prev().start), "Expected comma between arguments of a function call");
				}
			} else {
				Expression expr = expression(sourceReader, reader);
				if(expr == null) {
					throw new CompilationFailedError(sourceReader.getLocation(reader.peek().start), "Expected expression as argument of function call");
				}
				list.add(expr);
			}
			comma = !comma;
			
		}
		
		return true;
		
	}
	
	private Declaration declaration(SourceReader sourceReader, ListReader<Token> reader) throws IOException {

		int mark = reader.mark();
		
		//System.out.println("Parsing declaration, trying variable declaration");
		
		VariableDecl varDecl = variableDecl(sourceReader, reader);
		if(varDecl != null) {
			return varDecl;
		}
		
		//System.out.println("Parsing declaration, trying function declaration");
		
		FunctionDecl funcDecl = functionDecl(sourceReader, reader);
		if(funcDecl != null) {
			return funcDecl;
		}
		
		ClassDecl classDecl = classDecl(sourceReader, reader);
		if(classDecl != null) {
			return classDecl;
		}
		
		reader.reset(mark);
		return null;
		
	}
	
	private VariableDecl variableDecl(SourceReader sourceReader, ListReader<Token> reader) throws IOException {

		int mark = reader.mark();
		
		//System.out.println("Parsing variable declaration");
		
		boolean isConst = false;
		boolean isStatic = false;
		
		while(true) {
			Token t = reader.peek();
			if(t.type == TokenType.CONST_KW) {
				isConst = true;
				reader.skip();
			} else if(t.type == TokenType.STATIC_KW) {
				isStatic = true;
				reader.skip();
			} else {
				break;
			}
		}
		
		Type type = type(sourceReader, reader);
		if(type != null) {
			Token t = reader.peek();
			if(t.type == TokenType.NAME) {
				reader.skip();
				Token t2 = reader.peek();
				if(t2.type == TokenType.ASSIGN) {
					reader.skip();
					Expression expr = expression(sourceReader, reader);
					if(expr == null) {
						throw new CompilationFailedError(sourceReader.getLocation(t2.start), "Expected expression as an initializer to a variable declaration.");
					}
					return new VariableDeclAssigned(type, sourceReader.getSlice(t.start, t.length), expr, isConst, isStatic);
				}
				return new VariableDecl(type, sourceReader.getSlice(t.start, t.length), isConst, isStatic);
			}
		}
		
		reader.reset(mark);
		return null;
		
	}
	
	private ClassDecl classDecl(SourceReader sourceReader, ListReader<Token> reader) throws IOException {

		int mark = reader.mark();
		
		OocDocComment comment = null;
		if(reader.peek().type == TokenType.OOCDOC) {
			Token t = reader.read();
			comment = new OocDocComment(sourceReader.getSlice(t.start, t.length));
		}
		
		boolean isAbstract = reader.peek().type == TokenType.ABSTRACT_KW;
		if(isAbstract) {
			reader.skip();
		}
		
		if(reader.read().type == TokenType.CLASS_KW) {
			
			Token t = reader.read();
			if(t.type != TokenType.NAME) {
				throw new CompilationFailedError(sourceReader.getLocation(t.start),
						"Expected class name after the class keyword.");
			}
			
			String superName = "";
			if(reader.peek().type == TokenType.FROM_KW) {
				reader.skip();
				Token tSuper = reader.read();
				superName = sourceReader.getSlice(tSuper.start, tSuper.length);
				
			}
			
			Token t2 = reader.read();
			if(t2.type != TokenType.OPEN_BRACK) {
				throw new CompilationFailedError(sourceReader.getLocation(t2.start),
						"Expected opening bracket to begin class declaration.");
			}
			
			ClassDecl classDecl = new ClassDecl(sourceReader.getSlice(t.start, t.length), isAbstract);
			classDecl.setSuperName(superName);
			if(comment != null) classDecl.setComment(comment);
			
			while(reader.peek().type != TokenType.CLOS_BRACK) {
			
				VariableDecl varDecl = variableDecl(sourceReader, reader);
				if(varDecl != null) {
					if(reader.read().type != TokenType.SEMICOL) {
						throw new CompilationFailedError(sourceReader.getLocation(reader.prev().start),
							"Expected semi-colon after variable declaration in class declaration");
					}
					classDecl.getVariables().add(varDecl);
					continue;
				}
				
				FunctionDecl funcDecl = functionDecl(sourceReader,reader);
				if(funcDecl != null) {
					classDecl.getFunctions().add(funcDecl);
					continue;
				}
				
				throw new CompilationFailedError(sourceReader.getLocation(reader.peek().start),
						"Expected variable declaration or function declaration in a class declaration");
			
			}
			reader.skip();
			
			return classDecl;
			
		}
		
		reader.reset(mark);
		return null;
		
	}
	
	private FunctionDecl functionDecl(SourceReader sourceReader, ListReader<Token> reader) throws IOException {

		int mark = reader.mark();
		
		OocDocComment comment = null;
		if(reader.peek().type == TokenType.OOCDOC) {
			Token t = reader.read();
			comment = new OocDocComment(sourceReader.getSlice(t.start, t.length));
		}
		
		boolean isAbstract = false;
		
		FunctionDeclType declType = FunctionDeclType.FUNC;
		if(reader.peek().type == TokenType.IMPL_KW) {
			reader.skip();
			declType = FunctionDeclType.IMPL;
		} else if(reader.peek().type == TokenType.OVER_KW) {
			reader.skip();
			declType = FunctionDeclType.OVER;
		} else {
			while(reader.peek().type != TokenType.FUNC_KW) {
				Token t = reader.read();
				if(t.type == TokenType.ABSTRACT_KW) {
					isAbstract = true;
				} else {
					reader.reset(mark);
					return null;
				}
			}
			reader.skip(); // the 'func' keyword
		}
		
		Token name = reader.read();
		if(name.type != TokenType.NAME && name.type != TokenType.NEW_KW) {
			throw new CompilationFailedError(sourceReader.getLocation(name.start), "Expected function name after 'func' keyword");
		}
		
		FunctionDecl functionDecl = new FunctionDecl(declType,
				sourceReader.getSlice(name.start, name.length), isAbstract);
		if(comment != null) functionDecl.setComment(comment);
		
		if(reader.peek().type == TokenType.OPEN_PAREN) {
			reader.skip();
			boolean comma = false;
			while(true) {
				
				if(reader.peek().type == TokenType.CLOS_PAREN) {
					reader.skip(); // skip the ')'
					break;
				}
				if(comma) {
					if(reader.read().type != TokenType.COMMA) {
						throw new CompilationFailedError(sourceReader.getLocation(reader.prev().start), "Expected comma between arguments of a function definition");
					}
				} else {
					Argument arg = argument(sourceReader, reader);
					if(arg == null) {
						throw new CompilationFailedError(sourceReader.getLocation(reader.peek().start), "Expected variable declaration as an argument of a function definition");
					}
					functionDecl.getArguments().add(arg);
				}
				comma = !comma;
				
			}
		}
		
		Token t = reader.read();
		if(t.type == TokenType.ARROW) {
			Type returnType = type(sourceReader, reader);
			if(returnType == null) {
				throw new CompilationFailedError(sourceReader.getLocation(reader.peek().start), "Expected return type after arrow");
			}
			functionDecl.setReturnType(returnType);
			t = reader.read();
		}
		if(t.type == TokenType.SEMICOL) {
			return functionDecl;
		}
		if(t.type != TokenType.OPEN_BRACK) {
			throw new CompilationFailedError(sourceReader.getLocation(reader.prev().start), "Expected opening brace after function name.");
		}
	
		while(reader.peek().type != TokenType.CLOS_BRACK) {
		
			Line line = line(sourceReader, reader);
			if(line == null) {
				throw new CompilationFailedError(sourceReader.getLocation(reader.peek().start), "Expected statement in function body.");
			}
			functionDecl.getBody().add(line);
		
		}
		reader.skip();
		
		return functionDecl;
		
	}
	
	private Argument argument(SourceReader sourceReader, ListReader<Token> reader) throws IOException {

		int mark = reader.mark();
		
		if(reader.peek().type == TokenType.TRIPLE_DOT) {
			reader.skip();
			return new VarArg();
		}
		
		Type type = type(sourceReader, reader);
		if(type != null) {
			Token t = reader.peek();
			if(t.type == TokenType.NAME) {
				reader.skip();
				return new RegularArgument(type, sourceReader.getSlice(t.start, t.length));
			}
		}
		reader.reset(mark);
		
		Token t = reader.read();
		if(t.type == TokenType.ASSIGN) {
			Token t2 = reader.read();
			if(t2.type != TokenType.NAME) {
				throw new CompilationFailedError(sourceReader.getLocation(t2.start),
						"Expecting member variable name in member-assign-argument");
			}
			return new MemberAssignArgument(sourceReader.getSlice(t2.start, t2.length));
		}
		
		if(t.type != TokenType.NAME) {
			throw new CompilationFailedError(sourceReader.getLocation(t.start),
			"Expecting member variable name in member-assign-argument");
		}
		return new MemberArgument(sourceReader.getSlice(t.start, t.length));
		
	}

	private Assignment assignment(SourceReader sourceReader, ListReader<Token> reader) throws IOException {
		
		int mark = reader.mark();

		//System.out.println("Parsing an assignment");
		
		Access lvalue = access(sourceReader, reader);
		//System.out.println("Got lvalue "+lvalue);
		if(lvalue != null) {
			Token t = reader.peek();
			if(t.type == TokenType.ASSIGN) {
				reader.skip();
				Expression rvalue = expression(sourceReader, reader);
				if(rvalue != null) {
					return new Assignment(lvalue, rvalue);
				}
			}
		}
		
		reader.reset(mark);
		//System.out.println("Didn't find anything to assign, resetting... Now next token is "+reader.peek().type);
		return null;
		
	}
	
	private Access access(SourceReader sourceReader, ListReader<Token> reader) {
		
		int mark = reader.mark();
		
		//System.out.println("Attempting to read varAccess");
		VariableAccess varAcc = variableAccess(sourceReader, reader);
		if(varAcc != null) {
			return varAcc;
		}
		
		reader.reset(mark);
		return null;
		
	}

	private VariableAccess variableAccess(SourceReader sourceReader, ListReader<Token> reader) {
		
		int mark = reader.mark();
		
		Token t = reader.peek();
		//System.out.println("Got token "+t.type);
		if(t.type == TokenType.NAME || t.type == TokenType.THIS_KW) {
			reader.skip();
			return new VariableAccess(sourceReader.getSlice(t.start, t.length));
		}
		
		reader.reset(mark);
		return null;
		
	}
	
	private Type type(SourceReader sourceReader, ListReader<Token> reader) {
		
		Token t = reader.peek();
		if(t.type == TokenType.NAME) {
			reader.skip();
			int pointerLevel = 0;
			while(reader.peek().type == TokenType.STAR) {
				reader.skip();
				pointerLevel++;
			}
			return new Type(sourceReader.getSlice(t.start, t.length), pointerLevel);
		}
		
		return null;
		
	}
	
	private Expression expression(SourceReader sourceReader, ListReader<Token> reader) throws IOException {
		
		int mark = reader.mark();
		
		if(reader.peek().type == TokenType.EXCL) {
			reader.skip();
			Expression inner = expression(sourceReader, reader);
			if(inner == null) {
				reader.reset(mark);
				return null;
			}
			System.out.println("Got NOT of a "+inner.getClass().getSimpleName());
			return new Not(inner);
		}
		
		Expression expr = flatExpression(sourceReader, reader);
		if(expr == null) {
			//System.out.println("Null flat expression");
			return null;
		}
		
		//int count = 1;
		
		while(true) {
			
			//System.out.println("Got #" + (count++) + " expression "+expr.getClass().getSimpleName());
			
			Token t = reader.peek();
			if(t.type == TokenType.DOT) {
				
				reader.skip();
				FunctionCall call = functionCall(sourceReader, reader);
				if(call != null) {
					expr = new MemberCall(expr, call);
					continue;
				}
				
				VariableAccess varAccess = variableAccess(sourceReader, reader);
				if(varAccess != null) {
					expr = new MemberAccess(expr, varAccess);
					continue;
				}
				
			}
			
			if(t.type == TokenType.DOUBLE_DOT) {
				
				reader.skip();
				Expression upper = expression(sourceReader, reader);
				if(upper == null) {
					throw new CompilationFailedError(sourceReader.getLocation(reader.peek().start),
							"Expected expression for the upper part of a range literal");
				}
				// this is so beautiful it makes me wanna cry
				expr = new RangeLiteral(expr, upper);
				
			}
			
			if(t.type == TokenType.OPEN_SQUAR) {

				reader.skip();
				Expression index = expression(sourceReader, reader);
				if(index == null) {
					throw new CompilationFailedError(sourceReader.getLocation(reader.peek().start),
						"Expected expression for the index of an array access");
				}
				//System.out.println("Got expression which is really a "+index.getClass().getSimpleName());
				//System.out.println("Next is a "+reader.peek().type);
				if(reader.read().type != TokenType.CLOS_SQUAR) {
					throw new CompilationFailedError(sourceReader.getLocation(reader.prev().start),
						"Expected closing bracket to end array access, got "+reader.prev().type+" instead.");
				}
				//System.out.println("Did arrayAccess just fine, next is a "+reader.peek().type);
				expr = new ArrayAccess(expr, index);
				continue;
				
			}
			
			if(t.type == TokenType.ASSIGN) {
				
				reader.skip();
				Expression rvalue = expression(sourceReader, reader);
				if(rvalue == null) {
					throw new CompilationFailedError(sourceReader.getLocation(reader.peek().start),
						"Expected rvalue for assignment");
				}
				if(!(expr instanceof Access)) {
					throw new CompilationFailedError(sourceReader.getLocation(reader.peek().start),
						"Trying to assign something which is not an access (ie. not a lvalue)");
				}
				expr = new Assignment((Access) expr, rvalue);
				continue;
				
			}
			
			if(t.type == TokenType.PLUS || t.type == TokenType.STAR
					|| t.type == TokenType.MINUS || t.type == TokenType.SLASH) {
				
				reader.skip();
				Expression rvalue = expression(sourceReader, reader);
				if(rvalue == null) {
					throw new CompilationFailedError(sourceReader.getLocation(reader.peek().start),
						"Expected rvalue after binary operator");
				}
				switch(t.type) {
					case PLUS:  expr = new Add(expr, rvalue); break;
					case STAR:  expr = new Mul(expr, rvalue); break;
					case MINUS: expr = new Sub(expr, rvalue); break;
					case SLASH: expr = new Div(expr, rvalue); break;
					default: throw new CompilationFailedError(sourceReader.getLocation(reader.prev().start),
							"Unknown binary operation yet");
				}
				continue;
				
				
			}
			
			return expr;
			
		}
		
	}
	
	private Expression flatExpression(SourceReader sourceReader, ListReader<Token> reader) throws IOException {
		
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
			expression = expression(sourceReader, reader);
		} else {
			expression = flatUnparenExpression(sourceReader, reader);
		}
		
		if(expression == null) {
			if(parenNumber > 0) {
				throw new CompilationFailedError(sourceReader.getLocation(reader.peek().start),
					"Expected expression into parenthesis");
			}
			reader.reset(mark);
			return null;
		}
		
		while(parenNumber > 0) {
			if(reader.read().type != TokenType.CLOS_PAREN) {
				throw new CompilationFailedError(sourceReader.getLocation(reader.peek().start),
					"Missing closing parenthesis.");
			}
			expression = new Parenthesis(expression);
			parenNumber--;
		}
		
		return expression;
		
	}
	
	private Expression flatUnparenExpression(SourceReader sourceReader, ListReader<Token> reader) throws IOException {
		
		int mark = reader.mark();
		
		//System.out.println("Parsing flatUnparenExpression");
		
		//System.out.println("Attempting to parse literal for flatUnparenExpression (next is "+reader.peek().type+")");
		Literal literal = literal(sourceReader, reader);
		if(literal != null) {
			return literal;
		}
		
		//System.out.println("Attempting to parse assignment for flatUnparenExpression (next is "+reader.peek().type+")");
		Assignment ass = assignment(sourceReader, reader);
		if(ass != null) {
			return ass;
		}
		
		//System.out.println("Attempting to parse declaration for flatUnparenExpression (next is "+reader.peek().type+")");
		Declaration declaration = declaration(sourceReader, reader);
		if(declaration != null) {
			return declaration;
		}
		
		//System.out.println("Attempting to parse instantiation for flatUnparenExpression (next is "+reader.peek().type+")");
		Instantiation instantiation = instantiation(sourceReader, reader);
		if(instantiation != null) {
			return instantiation;
		}
		
		//System.out.println("Attempting to parse funcCall for flatUnparenExpression (next is "+reader.peek().type+")");
		FunctionCall funcCall = functionCall(sourceReader, reader);
		if(funcCall != null) {
			return funcCall;
		}
		
		//System.out.println("Attempting to parse access for flatUnparenExpression (next is "+reader.peek().type+")");
		Access access = access(sourceReader, reader);
		if(access != null) {
			return access;
		}
		
		reader.reset(mark);
		return null;
		
	}

	private Literal literal(SourceReader sourceReader, ListReader<Token> reader) throws IOException {

		int mark = reader.mark();
		
		Token t = reader.read();
		if(t.type == TokenType.STRING_LIT) {
			return new StringLiteral(sourceReader.getSlice(t.start, t.length));
		}
		if(t.type == TokenType.CHAR_LIT) {
			try {
				return new CharLiteral(SourceReader.parseCharLiteral(sourceReader.getSlice(t.start, t.length)));
			} catch (SyntaxError e) {
				throw new CompilationFailedError(sourceReader.getLocation(t.start), "Malformed char literal");
			}
		}
		if(t.type == TokenType.DEC_NUMBER) {
			return new NumberLiteral(Long.parseLong(sourceReader.getSlice(t.start, t.length)), Format.DEC);
		}
		if(t.type == TokenType.HEX_NUMBER) {
			return new NumberLiteral(Long.parseLong(sourceReader.getSlice(t.start, t.length).toUpperCase(), 16), Format.HEX);
		}
		if(t.type == TokenType.OCT_NUMBER) {
			return new NumberLiteral(Long.parseLong(sourceReader.getSlice(t.start, t.length).toUpperCase(), 8), Format.OCT);
		}
		if(t.type == TokenType.BIN_NUMBER) {
			return new NumberLiteral(Long.parseLong(sourceReader.getSlice(t.start, t.length).toUpperCase(), 2), Format.BIN);
		}
		if(t.type == TokenType.TRUE) {
			return new BoolLiteral(true);
		}
		if(t.type == TokenType.FALSE) {
			return new BoolLiteral(false);
		}
		if(t.type == TokenType.NULL) {
			return new NullLiteral();
		}
		
		reader.reset(mark);
		return null;
		
	}
		
}
