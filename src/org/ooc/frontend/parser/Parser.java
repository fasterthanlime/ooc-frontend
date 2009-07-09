package org.ooc.frontend.parser;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.ooc.errors.CompilationFailedError;
import org.ooc.frontend.model.Access;
import org.ooc.frontend.model.Assignment;
import org.ooc.frontend.model.Declaration;
import org.ooc.frontend.model.Expression;
import org.ooc.frontend.model.FunctionCall;
import org.ooc.frontend.model.FunctionDecl;
import org.ooc.frontend.model.Include;
import org.ooc.frontend.model.Line;
import org.ooc.frontend.model.Literal;
import org.ooc.frontend.model.Node;
import org.ooc.frontend.model.NodeList;
import org.ooc.frontend.model.NumberLiteral;
import org.ooc.frontend.model.SourceUnit;
import org.ooc.frontend.model.Statement;
import org.ooc.frontend.model.StringLiteral;
import org.ooc.frontend.model.TokenParser;
import org.ooc.frontend.model.Type;
import org.ooc.frontend.model.VariableAccess;
import org.ooc.frontend.model.VariableDecl;
import org.ooc.frontend.model.VariableDeclAssigned;
import org.ooc.frontend.model.NumberLiteral.Format;
import org.ooc.frontend.model.tokens.ListReader;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.frontend.model.tokens.Token.TokenType;
import org.ubi.SourceReader;

public class Parser {

	public Parser() {
		
	}
	
	public SourceUnit parse(File file) throws IOException {

		SourceReader sourceReader = SourceReader.getReaderFromFile(file);
		List<Token> tokens = new TokenParser().parse(sourceReader);
		SourceUnit unit = sourceUnit(sourceReader, new ListReader<Token>(tokens));
		return unit;
		
	}
	
	public SourceUnit sourceUnit(SourceReader sourceReader, ListReader<Token> reader) throws IOException {
		
		SourceUnit unit = new SourceUnit(sourceReader.getLocation().getFileName());
		
		while(reader.hasNext()) {
			
			Declaration declaration = declaration(sourceReader, reader);
			if(declaration != null) {
				unit.getBody().add(declaration);
				continue;
			}
			
			if(include(sourceReader, reader, unit.getBody())) {
				continue;
			}
			
			throw new CompilationFailedError(sourceReader.getLocation(reader.peek().start), "Expected declaration in source unit");
			
		}
		
		return unit;
		
	}
	
	

	private boolean include(SourceReader sourceReader,	ListReader<Token> reader, NodeList<Node> body) throws EOFException, CompilationFailedError {

		if(reader.peek().type != TokenType.INCLUDE_KW) {
			return false;
		}
		reader.skip();
		
		StringBuilder sb = new StringBuilder();
		
		while(true) {
		
			Token t = reader.read();
			if(t.type == TokenType.SEMICOL) {
				body.add(new Include(sb.toString()));
				break;
			}
			if(t.type == TokenType.COMMA) {
				body.add(new Include(sb.toString()));
				sb.setLength(0);
			} else if(t.type == TokenType.NAME) {
				sb.append(sourceReader.getSlice(t.start, t.length));
			} else if(t.type == TokenType.SLASH) {
				sb.append(sourceReader.getSlice(t.start, t.length));
			} else {
				throw new CompilationFailedError(sourceReader.getLocation(t.start), "Unexpected token "+t.type);
			}
			
		}
		
		return true;
		
	}

	private Line line(SourceReader sourceReader, ListReader<Token> reader) throws IOException {

		int mark = reader.mark();
		
		Statement statement = statement(sourceReader, reader);
		if(statement != null) {
			if(reader.read().type != TokenType.SEMICOL) {
				throw new CompilationFailedError(sourceReader.getLocation(reader.prev(2).start), "Missing semi-colon at the end of a line.");
			}
			return new Line(statement);
		}
		
		reader.reset(mark);
		return null;
		
	}

	private Statement statement(SourceReader sourceReader, ListReader<Token> reader) throws IOException {

		int mark = reader.mark();
		
		//System.out.println("Parsing statement, trying function call");
		
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

	private FunctionCall functionCall(SourceReader sourceReader, ListReader<Token> reader) throws IOException {
		
		int mark = reader.mark();
		
		Token name = reader.read();
		if(name.type != TokenType.NAME) {
			reader.reset(mark);
			return null;
		}
		
		if(reader.read().type != TokenType.OPEN_PAREN) {
			reader.reset(mark);
			return null;
		}
		
		FunctionCall call = new FunctionCall(sourceReader.getSlice(name.start, name.length));
		
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
				call.getArguments().add(expr);
			}
			comma = !comma;
			
		}
		
		return call;
		
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
		
		reader.reset(mark);
		return null;
		
	}
	
	private VariableDecl variableDecl(SourceReader sourceReader, ListReader<Token> reader) throws IOException {

		int mark = reader.mark();
		
		//System.out.println("Parsing variable declaration");
		
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
						throw new CompilationFailedError(sourceReader.getLocation(t2.start), "Expecting expression as an initializer to a variable declaration.");
					}
					return new VariableDeclAssigned(type, sourceReader.getSlice(t.start, t.length), expr);
				}
				return new VariableDecl(type, sourceReader.getSlice(t.start, t.length));
			}
		}
		
		reader.reset(mark);
		return null;
		
	}
	
	private FunctionDecl functionDecl(SourceReader sourceReader, ListReader<Token> reader) throws IOException {

		int mark = reader.mark();
		
		if(reader.read().type == TokenType.FUNC_KW) {

			Token name = reader.read();
			if(name.type != TokenType.NAME) {
				throw new CompilationFailedError(sourceReader.getLocation(name.start), "Expected function name after 'func' keyword");
			}
			
			FunctionDecl functionDecl = new FunctionDecl(sourceReader.getSlice(name.start, name.length));
			
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
						VariableDecl arg = variableDecl(sourceReader, reader);
						if(arg == null) {
							throw new CompilationFailedError(sourceReader.getLocation(reader.peek().start), "Expected variable declaration as an argument of a function definition");
						}
						functionDecl.getArguments().add(arg);
					}
					comma = !comma;
					
				}
			}
			
			if(reader.read().type != TokenType.OPEN_BRACK) {
				throw new CompilationFailedError(sourceReader.getLocation(reader.prev().start), "Expected opening brace after function name.");
			}
		
			while(reader.peek().type != TokenType.CLOS_BRACK) {
			
				Line line = line(sourceReader, reader);
				if(line == null) {
					throw new CompilationFailedError(sourceReader.getLocation(reader.peek().start), "Expected statement in function body.");
				}
				functionDecl.getBody().nodes.add(line);
			
			}
			reader.skip();
			
			return functionDecl;
			
		}
		
		reader.reset(mark);
		return null;
		
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
		return null;
		
	}
	
	private Access access(SourceReader sourceReader, ListReader<Token> reader) {
		
		int mark = reader.mark();
		
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
		if(t.type == TokenType.NAME) {
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
			return new Type(sourceReader.getSlice(t.start, t.length));
		}
		
		return null;
		
	}
	
	private Expression expression(SourceReader sourceReader, ListReader<Token> reader) throws IOException {
		
		int mark = reader.mark();
		
		Literal literal = literal(sourceReader, reader);
		if(literal != null) {
			return literal;
		}
		
		Assignment ass = assignment(sourceReader, reader);
		if(ass != null) {
			return ass;
		}
		
		Declaration declaration = declaration(sourceReader, reader);
		if(declaration != null) {
			return declaration;
		}
		
		FunctionCall funcCall = functionCall(sourceReader, reader);
		if(funcCall != null) {
			return funcCall;
		}
		
		Access access = access(sourceReader, reader);
		if(access != null) {
			return access;
		}
		
		reader.reset(mark);
		return null;
		
	}

	private Literal literal(SourceReader sourceReader, ListReader<Token> reader) {

		int mark = reader.mark();
		
		Token t = reader.read();
		if(t.type == TokenType.STRING_LIT) {
			return new StringLiteral(sourceReader.getSlice(t.start, t.length));
		}
		if(t.type == TokenType.DEC_NUMBER) {
			return new NumberLiteral(Integer.parseInt(sourceReader.getSlice(t.start, t.length)), Format.DEC);
		}
		
		reader.reset(mark);
		return null;
		
	}
		
}
