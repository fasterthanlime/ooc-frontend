package org.ooc.frontend.parser;

import java.io.IOException;

import org.ooc.frontend.model.FunctionDecl;
import org.ooc.frontend.model.Line;
import org.ooc.frontend.model.OocDocComment;
import org.ooc.frontend.model.Type;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.frontend.model.tokens.TokenReader;
import org.ooc.frontend.model.tokens.Token.TokenType;
import org.ubi.CompilationFailedError;
import org.ubi.SourceReader;

public class FunctionDeclParser {

	public static FunctionDecl parse(SourceReader sReader, TokenReader reader) throws IOException {
		return parse(sReader, reader, false);
	}
	
	public static FunctionDecl parse(SourceReader sReader, TokenReader reader, boolean skipFunc) throws IOException {

		int mark = reader.mark();
		
		OocDocComment comment = null;
		if(reader.peek().type == TokenType.OOCDOC) {
			Token t = reader.read();
			comment = new OocDocComment(t.get(sReader));
		}
		
		String name = "";
		Token tName = reader.peek();
		if(tName.type == TokenType.NAME || tName.type == TokenType.NEW_KW) {
			name = tName.get(sReader);
			reader.skip();
			if(reader.read().type != TokenType.COLON) {
				reader.reset(mark);
				return null;
			}
		}
		
		boolean isAbstract = false;
		boolean isStatic = false;
		boolean isFinal = false;
		String externName = null;
		
		Token kw = reader.peek();
		while(kw.type == TokenType.ABSTRACT_KW
		   || kw.type == TokenType.STATIC_KW
		   || kw.type == TokenType.FINAL_KW
		   || kw.type == TokenType.EXTERN_KW
		   ) {
			
			switch(kw.type) {
			case ABSTRACT_KW: reader.skip(); isAbstract = true; break;
			case STATIC_KW: reader.skip(); isStatic = true; break;
			case FINAL_KW: reader.skip(); isFinal = true; break;
			case EXTERN_KW: externName = ExternParser.parse(sReader, reader); break;
			default:
			}
			
			kw = reader.peek();
		}
		
		if(reader.peek().type == TokenType.FUNC_KW) {
			reader.skip();
		} else if(!skipFunc) {
			reader.reset(mark);
			return null;
		}
		
		String suffix = "";
		if(reader.peek().type == TokenType.TILDE) {
			reader.skip();
			Token tSuffix = reader.peek();
			if(tSuffix.type == TokenType.NAME) {
				reader.skip();
				suffix = tSuffix.get(sReader);
			}
		}
		
		FunctionDecl functionDecl = new FunctionDecl(
				name, suffix, isFinal, isStatic, isAbstract, externName);
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
						throw new CompilationFailedError(sReader.getLocation(reader.prev().start),
								"Expected comma between arguments of a function definition");
					}
				} else {
					//Argument arg = ArgumentParser.parse(sReader, reader, isExtern);
					if(!ArgumentParser.fill(sReader, reader, functionDecl.isExtern(), functionDecl.getArguments())) {
					//if(arg == null) {
						throw new CompilationFailedError(sReader.getLocation(reader.peek().start),
								"Expected variable declaration as an argument of a function definition");
					}
					//functionDecl.getArguments().add(arg);
				}
				comma = !comma;
				
			}
		}
		
		if(reader.peek().type == TokenType.LINESEP) return functionDecl;
		
		Token t = reader.read();
		if(t.type == TokenType.ARROW) {
			Type returnType = TypeParser.parse(sReader, reader);
			if(returnType == null) {
				throw new CompilationFailedError(sReader.getLocation(reader.peek().start),
						"Expected return type after arrow");
			}
			functionDecl.setReturnType(returnType);
			t = reader.read();
		}
		
		if(t.type == TokenType.LINESEP) return functionDecl;

		if(t.type != TokenType.OPEN_BRACK) {
			reader.rewind();
			Line line = LineParser.parse(sReader, reader);
			if(line == null) {
				throw new CompilationFailedError(sReader.getLocation(reader.prev().start),
						"Expected opening brace after function name.");
			}
			functionDecl.getBody().add(line);
			return functionDecl;
		}
	
		while(reader.hasNext() && reader.peek().type != TokenType.CLOS_BRACK) {
			reader.skipWhitespace();
		
			Line line = LineParser.parse(sReader, reader);
			if(line == null && reader.hasNext() && reader.peek().type != TokenType.CLOS_BRACK) {
				throw new CompilationFailedError(sReader.getLocation(reader.peek().start),
						"Expected statement in function body. Found "+reader.peek().type+" instead.");
			}
			if(line != null) functionDecl.getBody().add(line);
		}
		reader.skip();
		
		return functionDecl;
	}
	
}
