package org.ooc.frontend.parser;

import java.io.File;
import java.io.IOException;

import org.ooc.errors.CompilationFailedError;
import org.ooc.frontend.model.Expression;
import org.ooc.frontend.model.FunctionCall;
import org.ooc.frontend.model.FunctionDecl;
import org.ooc.frontend.model.Literal;
import org.ooc.frontend.model.SourceUnit;
import org.ooc.frontend.model.Statement;
import org.ooc.frontend.model.StringLiteral;
import org.ubi.SourceReader;

public class Parser {

	public Parser() {
		
	}
	
	public SourceUnit parse(File f) throws IOException {

		SourceReader reader = SourceReader.getReaderFromFile(f);
		SourceUnit unit = sourceUnit(reader);
		return unit;
		
	}
	
	public SourceUnit sourceUnit(SourceReader reader) throws IOException {
		
		SourceUnit unit = new SourceUnit(reader.getLocation());
		
		while(reader.hasNext()) {
			
			reader.skipWhitespace();
			
			FunctionDecl f = func(reader);
			if(f != null) {
				unit.getBody().add(f);
				continue;
			}
			
			throw new CompilationFailedError(reader.getLocation(), "Expected declaration in source unit");
			
		}
		
		return unit;
		
	}

	private FunctionDecl func(SourceReader reader) throws IOException {

		int mark = reader.mark();
		
		if(reader.matchesSpaced("func", true)) {
			
			reader.skipWhitespace();
			String name = reader.readName();
			if(name.isEmpty()) {
				throw new CompilationFailedError(reader.getLocation(), "Expected function name after 'func' keyword");
			}
			
			FunctionDecl function = new FunctionDecl(name);
			
			reader.skipWhitespace();
			if(reader.read() != '{') {
				throw new CompilationFailedError(reader.getLocation(), "Expected opening brace after function name.");
			}
		
			reader.skipWhitespace();
			while(reader.peek() != '}') {
			
				reader.skipWhitespace();
				
				Statement stat = statement(reader);
				if(stat == null) {
					throw new CompilationFailedError(reader.getLocation(), "Expected statement in function body.");
				}
				function.getBody().nodes.add(stat);
				reader.skipWhitespace();
			
			}
			reader.read();
			
			return function;
			
		}
		
		reader.reset(mark);
		return null;
		
	}

	private Statement statement(SourceReader reader) throws IOException {

		int mark = reader.mark();
		
		FunctionCall call = functionCall(reader);
		if(call != null) {
			return call;
		}
		
		reader.reset(mark);
		return null;
		
	}
	
	private FunctionCall functionCall(SourceReader reader) throws IOException {
		
		int mark = reader.mark();
		
		reader.skipWhitespace();
		String name = reader.readName();
		if(name.isEmpty()) {
			reader.reset(mark);
			return null;
		}
		
		reader.skipWhitespace();
		if(reader.read() != '(') {
			reader.reset(mark);
			return null;
		}
		
		FunctionCall call = new FunctionCall(name);
		
		boolean comma = false;
		while(true) {
			
			reader.skipWhitespace();
			if(comma) {
				if(reader.read() != ',') {
					throw new CompilationFailedError(reader.getLocation(), "Expected comma between arguments of a function call");
				}
			} else {
				if(reader.peek() == ')') {
					reader.read(); // skip the ')'
					break;
				}
				Expression expr = expression(reader);
				if(expr == null) {
					throw new CompilationFailedError(reader.getLocation(), "Expected expression as argument of function call");
				}
				call.getArguments().add(expr);
			}
			
		}
		
		return call;
		
	}
	
	private Expression expression(SourceReader reader) throws IOException {
		
		int mark = reader.mark();
		
		Literal literal = literal(reader);
		if(literal != null) {
			return literal;
		}
		
		reader.reset(mark);
		return null;
		
	}

	private Literal literal(SourceReader reader) throws IOException {

		int mark = reader.mark();
		
		StringLiteral str = stringLiteral(reader);
		if(str != null) {
			return str;
		}
		
		reader.reset(mark);
		return null;
		
	}
	
	private StringLiteral stringLiteral(SourceReader reader) throws IOException {

		if(reader.peek() != '"') {
			return null;
		}
		reader.read();
		
		int mark = reader.mark();
		
		String str = reader.readStringLiteral();
		if(!str.isEmpty()) {
			return new StringLiteral(str);
		}
		
		reader.reset(mark);
		return null;
		
	}
	
}
