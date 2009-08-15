package org.ooc.frontend.parser;

import static org.ooc.frontend.model.tokens.Token.TokenType.COMMA;
import static org.ooc.frontend.model.tokens.Token.TokenType.DOT;
import static org.ooc.frontend.model.tokens.Token.TokenType.IMPORT_KW;
import static org.ooc.frontend.model.tokens.Token.TokenType.LINESEP;
import static org.ooc.frontend.model.tokens.Token.TokenType.NAME;
import static org.ooc.frontend.model.tokens.Token.TokenType.STAR;

import java.io.EOFException;

import org.ooc.frontend.model.Import;
import org.ooc.frontend.model.NodeList;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.frontend.model.tokens.TokenReader;
import org.ubi.CompilationFailedError;
import org.ubi.SourceReader;

public class ImportParser {

	public static boolean parse(SourceReader sReader, 
			TokenReader reader, NodeList<Import> imports) throws EOFException {

		if(reader.peek().type != IMPORT_KW) {
			return false;
		}
		reader.skip();
		
		StringBuilder sb = new StringBuilder();
		
		while(true) {
		
			Token t = reader.read();
			if(t.type == LINESEP) {
				imports.add(new Import(sb.toString()));
				break;
			}
			if(t.type == COMMA) {
				imports.add(new Import(sb.toString()));
				sb.setLength(0);
			} else if(t.type == NAME) {
				sb.append(t.get(sReader));
			} else if(t.type == DOT) {
				if(t.type == STAR) {
					System.out.println("Encountered import blah.*");
				}
				sb.append('.');
			} else {
				throw new CompilationFailedError(sReader.getLocation(t.start), "Unexpected token "+t.type);
			}
			
		}
		
		return true;
		
	}
	
}
