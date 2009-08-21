package org.ooc.frontend.parser;

import java.io.EOFException;

import org.ooc.frontend.model.Import;
import org.ooc.frontend.model.NodeList;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.frontend.model.tokens.TokenReader;
import org.ooc.frontend.model.tokens.Token.TokenType;
import org.ubi.CompilationFailedError;
import org.ubi.SourceReader;

public class ImportParser {

	public static boolean parse(SourceReader sReader, 
			TokenReader reader, NodeList<Import> imports) throws EOFException {

		if(reader.peek().type != TokenType.IMPORT_KW) {
			return false;
		}
		reader.skip();
		
		StringBuilder sb = new StringBuilder();
		
		while(true) {
		
			Token t = reader.read();
			if(t.type == TokenType.LINESEP) {
				imports.add(new Import(sb.toString()));
				break;
			}
			if(t.type == TokenType.COMMA) {
				imports.add(new Import(sb.toString()));
				sb.setLength(0);
			} else if(t.type == TokenType.NAME || t.type == TokenType.MINUS) {
				sb.append(t.get(sReader));
			} else if(t.type == TokenType.DOT) {
				if(t.type == TokenType.STAR) {
					System.out.println("Encountered import blah.*");
				}
				sb.append('.');
			} else {
				throw new CompilationFailedError(sReader.getLocation(t.start),
						"Unexpected token "+t.type+" while reading an import");
			}
			
		}
		
		return true;
		
	}
	
}
