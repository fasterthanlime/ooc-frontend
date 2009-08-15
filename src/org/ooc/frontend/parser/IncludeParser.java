package org.ooc.frontend.parser;

import static org.ooc.frontend.model.tokens.Token.TokenType.COMMA;
import static org.ooc.frontend.model.tokens.Token.TokenType.INCLUDE_KW;
import static org.ooc.frontend.model.tokens.Token.TokenType.LINESEP;
import static org.ooc.frontend.model.tokens.Token.TokenType.NAME;
import static org.ooc.frontend.model.tokens.Token.TokenType.SLASH;

import java.io.EOFException;

import org.ooc.frontend.model.Include;
import org.ooc.frontend.model.NodeList;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.frontend.model.tokens.TokenReader;
import org.ubi.CompilationFailedError;
import org.ubi.SourceReader;

public class IncludeParser {

	public static boolean parse(SourceReader sReader, TokenReader reader, NodeList<Include> includes) throws EOFException, CompilationFailedError {

		if(reader.peek().type != INCLUDE_KW) {
			return false;
		}
		reader.skip();
		
		StringBuilder sb = new StringBuilder();
		
		while(true) {
		
			Token t = reader.read();
			if(t.type == LINESEP) {
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
	
}
