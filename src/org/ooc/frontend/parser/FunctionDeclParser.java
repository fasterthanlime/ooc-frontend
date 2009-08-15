package org.ooc.frontend.parser;

import static org.ooc.frontend.model.tokens.Token.TokenType.ABSTRACT_KW;
import static org.ooc.frontend.model.tokens.Token.TokenType.ARROW;
import static org.ooc.frontend.model.tokens.Token.TokenType.CLOS_BRACK;
import static org.ooc.frontend.model.tokens.Token.TokenType.CLOS_PAREN;
import static org.ooc.frontend.model.tokens.Token.TokenType.COLON;
import static org.ooc.frontend.model.tokens.Token.TokenType.COMMA;
import static org.ooc.frontend.model.tokens.Token.TokenType.EXTERN_KW;
import static org.ooc.frontend.model.tokens.Token.TokenType.FINAL_KW;
import static org.ooc.frontend.model.tokens.Token.TokenType.FUNC_KW;
import static org.ooc.frontend.model.tokens.Token.TokenType.LINESEP;
import static org.ooc.frontend.model.tokens.Token.TokenType.NAME;
import static org.ooc.frontend.model.tokens.Token.TokenType.NEW_KW;
import static org.ooc.frontend.model.tokens.Token.TokenType.OOCDOC;
import static org.ooc.frontend.model.tokens.Token.TokenType.OPEN_BRACK;
import static org.ooc.frontend.model.tokens.Token.TokenType.OPEN_PAREN;
import static org.ooc.frontend.model.tokens.Token.TokenType.STATIC_KW;
import static org.ooc.frontend.model.tokens.Token.TokenType.TILDE;

import java.io.IOException;

import org.ooc.frontend.model.Argument;
import org.ooc.frontend.model.FunctionDecl;
import org.ooc.frontend.model.Line;
import org.ooc.frontend.model.OocDocComment;
import org.ooc.frontend.model.Type;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.frontend.model.tokens.TokenReader;
import org.ubi.CompilationFailedError;
import org.ubi.SourceReader;

public class FunctionDeclParser {

	public static FunctionDecl parse(SourceReader sReader, TokenReader reader) throws IOException {

		int mark = reader.mark();
		
		OocDocComment comment = null;
		if(reader.peek().type == OOCDOC) {
			Token t = reader.read();
			comment = new OocDocComment(t.get(sReader));
		}
		
		String name = "";
		Token tName = reader.peek();
		if(tName.type == NAME || tName.type == NEW_KW) {
			name = tName.get(sReader);
			reader.skip();
			if(reader.read().type != COLON) {
				reader.reset(mark);
				return null;
			}
		}
		
		boolean isAbstract = false;
		boolean isStatic = false;
		boolean isFinal = false;
		boolean isExtern = false;
		
		Token kw = reader.peek();
		while(kw.type == ABSTRACT_KW
		   || kw.type == STATIC_KW
		   || kw.type == FINAL_KW
		   || kw.type == EXTERN_KW
		   ) {
			
			reader.skip();
			
			switch(kw.type) {
			case ABSTRACT_KW: isAbstract = true; break;
			case STATIC_KW: isStatic = true; break;
			case FINAL_KW: isFinal = true; break;
			case EXTERN_KW: isExtern = true; break;
			default:
			}
			
			kw = reader.peek();
		}
		
		if(reader.read().type != FUNC_KW) {
			reader.reset(mark);
			return null;
		}
		
		String suffix = "";
		if(reader.peek().type == TILDE) {
			reader.skip();
			Token tSuffix = reader.peek();
			if(tSuffix.type == NAME) {
				reader.skip();
				suffix = tSuffix.get(sReader);
			}
		}
		
		FunctionDecl functionDecl = new FunctionDecl(
				name, suffix, isFinal, isStatic, isAbstract, isExtern);
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
					Argument arg = ArgumentParser.parse(sReader, reader, isExtern);
					if(arg == null) {
						throw new CompilationFailedError(sReader.getLocation(reader.peek().start),
								"Expected variable declaration as an argument of a function definition");
					}
					functionDecl.getArguments().add(arg);
				}
				comma = !comma;
				
			}
		}
		
		if(reader.peek().type == LINESEP) return functionDecl;
		
		Token t = reader.read();
		if(t.type == ARROW) {
			Type returnType = TypeParser.parse(sReader, reader);
			if(returnType == null) {
				throw new CompilationFailedError(sReader.getLocation(reader.peek().start), "Expected return type after arrow");
			}
			functionDecl.setReturnType(returnType);
			t = reader.read();
		}
		
		if(t.type == LINESEP) return functionDecl;

		if(t.type != OPEN_BRACK) {
			reader.rewind();
			Line line = LineParser.parse(sReader, reader);
			if(line == null) {
				throw new CompilationFailedError(sReader.getLocation(reader.prev().start), "Expected opening brace after function name.");
			}
			functionDecl.getBody().add(line);
			return functionDecl;
		}
	
		while(reader.hasNext() && reader.peek().type != CLOS_BRACK) {
			if(reader.peek().type == LINESEP) {
				reader.skip(); continue;
			}
		
			Line line = LineParser.parse(sReader, reader);
			if(line == null && reader.hasNext() && reader.peek().type != CLOS_BRACK) {
				throw new CompilationFailedError(sReader.getLocation(reader.peek().start), "Expected statement in function body. Found "+reader.peek().type+" instead.");
			}
			functionDecl.getBody().add(line);
		}
		reader.skip();
		
		return functionDecl;
	}
	
}
