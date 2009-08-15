package org.ooc.frontend.parser;

import java.io.EOFException;

import org.ooc.frontend.model.Include;
import org.ooc.frontend.model.NodeList;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.frontend.model.tokens.TokenReader;
import org.ooc.frontend.model.tokens.Token.TokenType;
import org.ubi.CompilationFailedError;
import org.ubi.SourceReader;

public class IncludeParser {

	public static boolean parse(SourceReader sReader, TokenReader reader, NodeList<Include> includes) throws EOFException, CompilationFailedError {

		if(reader.peek().type != TokenType.INCLUDE_KW) {
			return false;
		}
		reader.skip();
		
		StringBuilder sb = new StringBuilder();
		
		while(true) {
		
			Token t = reader.read();
			if(t.type == TokenType.LINESEP) {
				includes.add(new Include(sb.toString()));
				break;
			}
			if(t.type == TokenType.COMMA) {
				includes.add(new Include(sb.toString()));
				sb.setLength(0);
			} else if(t.type == TokenType.NAME) {
				sb.append(t.get(sReader));
			} else if(t.type == TokenType.SLASH) {
				sb.append('/');
			} else {
				throw new CompilationFailedError(sReader.getLocation(t.start), "Unexpected token "+t.type);
			}
			
		}
		
		return true;
		
	}
	
}
