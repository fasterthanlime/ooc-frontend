package org.ooc.frontend.parser;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.ooc.errors.CompilationFailedError;
import org.ooc.frontend.model.Expression;
import org.ooc.frontend.model.FunctionCall;
import org.ooc.frontend.model.FunctionDecl;
import org.ooc.frontend.model.Include;
import org.ooc.frontend.model.Line;
import org.ooc.frontend.model.NumberLiteral;
import org.ooc.frontend.model.Literal;
import org.ooc.frontend.model.Node;
import org.ooc.frontend.model.NodeList;
import org.ooc.frontend.model.SourceUnit;
import org.ooc.frontend.model.Statement;
import org.ooc.frontend.model.StringLiteral;
import org.ooc.frontend.model.TokenParser;
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
			
			FunctionDecl f = func(sourceReader, reader);
			if(f != null) {
				unit.getBody().add(f);
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

	private FunctionDecl func(SourceReader sourceReader, ListReader<Token> reader) throws IOException {

		int mark = reader.mark();
		
		if(reader.read().type == TokenType.FUNC_KW) {

			Token name = reader.read();
			if(name.type != TokenType.NAME) {
				throw new CompilationFailedError(sourceReader.getLocation(name.start), "Expected function name after 'func' keyword");
			}
			
			FunctionDecl function = new FunctionDecl(sourceReader.getSlice(name.start, name.length));
			
			String returnType;
			List<VariableDecl> args;
			
			if(reader.peek().type == TokenType.OPEN_PAREN) {
				
			}
			
			if(reader.read().type != TokenType.OPEN_BRACK) {
				throw new CompilationFailedError(sourceReader.getLocation(reader.prev().start), "Expected opening brace after function name.");
			}
		
			while(reader.peek().type != TokenType.CLOS_BRACK) {
			
				Line line = line(sourceReader, reader);
				if(line == null) {
					throw new CompilationFailedError(sourceReader.getLocation(reader.peek().start), "Expected statement in function body.");
				}
				function.getBody().nodes.add(line);
			
			}
			reader.skip();
			
			return function;
			
		}
		
		reader.reset(mark);
		return null;
		
	}

	private Line line(SourceReader sourceReader, ListReader<Token> reader) throws IOException {

		int mark = reader.mark();
		
		Statement statement = statement(sourceReader, reader);
		if(statement != null) {
			if(reader.read().type != TokenType.SEMICOL) {
				throw new CompilationFailedError(sourceReader.getLocation(reader.prev().start), "Expected semi-colon to terminate line, not "+reader.prev().type);
			}
			return new Line(statement);
		}
		
		reader.reset(mark);
		return null;
		
	}

	private Statement statement(SourceReader sourceReader, ListReader<Token> reader) throws IOException {

		int mark = reader.mark();
		
		FunctionCall call = functionCall(sourceReader, reader);
		if(call != null) {
			return call;
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
	
	private Expression expression(SourceReader sourceReader, ListReader<Token> reader) throws IOException {
		
		int mark = reader.mark();
		
		Literal literal = literal(sourceReader, reader);
		if(literal != null) {
			return literal;
		}
		
		FunctionCall funcCall = functionCall(sourceReader, reader);
		if(funcCall != null) {
			return funcCall;
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
