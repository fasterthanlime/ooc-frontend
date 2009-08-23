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

		Token startToken = reader.peek();
		if(startToken.type != TokenType.IMPORT_KW) {
			return false;
		}
		reader.skip();
		
		StringBuilder sb = new StringBuilder();
		
		while(true) {
		
			Token token = reader.read();
			if(token.type == TokenType.LINESEP) {
				Import imp = new Import(sb.toString(), startToken);
				imports.add(imp);
				break;
			}
			if(token.type == TokenType.COMMA) {
				Import imp = new Import(sb.toString(), startToken);
				imports.add(imp);
				sb.setLength(0);
				startToken = reader.peek();
			} else if(token.type == TokenType.NAME || token.type == TokenType.MINUS) {
				sb.append(token.get(sReader));
			} else if(token.type == TokenType.DOT) {
				if(token.type == TokenType.STAR) {
					System.out.println("Encountered import blah.*");
				}
				sb.append('.');
			} else {
				throw new CompilationFailedError(sReader.getLocation(token),
						"Unexpected token "+token.type+" while reading an import");
			}
			
		}
		
		return true;
		
	}
	
}
