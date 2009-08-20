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
		
		if(reader.peek().type != TokenType.USE_KW) return false;
		reader.skip();
		
		StringBuilder sb = new StringBuilder();
		
		while(true) {
			
			Token t = reader.read();
			if(t.type == TokenType.LINESEP) {
				uses.add(new Use(sb.toString()));
				break;
			}
			if(t.type == TokenType.COMMA) {
				uses.add(new Use(sb.toString()));
				sb.setLength(0);
			} else if(t.type == TokenType.NAME || t.type == TokenType.MINUS 
					|| t.type == TokenType.SLASH) {
				sb.append(t.get(sReader));
			} else {
				throw new CompilationFailedError(sReader.getLocation(t.start),
						"Unexpected token "+t.type+" while reading use");
			}
			
		}
		return true;
		
	}

}
