package org.ooc.frontend.parser;

import java.io.EOFException;

import org.ooc.frontend.model.NodeList;
import org.ooc.frontend.model.Use;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.frontend.model.tokens.TokenReader;
import org.ooc.frontend.model.tokens.Token.TokenType;
import org.ubi.CompilationFailedError;
import org.ubi.SourceReader;

public class UseParser {

	public static boolean parse(SourceReader sReader, TokenReader reader,
			NodeList<Use> uses) throws EOFException, CompilationFailedError {
		
		Token startToken = reader.peek();
		if(startToken.type != TokenType.USE_KW) return false;
		reader.skip();
		
		StringBuilder sb = new StringBuilder();
		
		while(true) {
			
			Token token = reader.read();
			if(token.type == TokenType.LINESEP) {
				uses.add(new Use(sb.toString(), startToken));
				break;
			}
			if(token.type == TokenType.COMMA) {
				uses.add(new Use(sb.toString(), startToken));
				sb.setLength(0);
			} else if(token.type == TokenType.NAME || token.type == TokenType.MINUS 
					|| token.type == TokenType.SLASH) {
				sb.append(token.get(sReader));
			} else {
				throw new CompilationFailedError(sReader.getLocation(token),
						"Unexpected token "+token.type+" while reading use");
			}
			
		}
		return true;
		
	}

}
