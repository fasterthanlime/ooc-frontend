package org.ooc.frontend.parser;

import java.io.EOFException;

import org.ooc.frontend.model.Include;
import org.ooc.frontend.model.NodeList;
import org.ooc.frontend.model.Include.Mode;
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
				addInclude(includes, sb.toString());
				break;
			}
			if(t.type == TokenType.COMMA) {
				addInclude(includes, sb.toString());
				sb.setLength(0);
			} else if(t.type == TokenType.NAME) {
				sb.append(t.get(sReader));
			} else if(t.type == TokenType.SLASH) {
				sb.append('/');
			} else if(t.type == TokenType.DOT) {
				sb.append('.');
			} else if(t.type == TokenType.DOUBLE_DOT) {
				sb.append("..");
			} else {
				throw new CompilationFailedError(sReader.getLocation(t.start), "Unexpected token "+t.type);
			}
			
		}
		
		return true;
		
	}

	private static void addInclude(NodeList<Include> includes, String contentParam) {
		String content = contentParam;
		Mode mode = Mode.PATHY;
		if(content.startsWith("./")) {
			content = content.substring(2);
			mode = Mode.LOCAL;
		}
		includes.add(new Include(content, mode));
	}
	
}
