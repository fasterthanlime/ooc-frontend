package org.ooc.frontend.parser;

import org.ooc.frontend.model.Type;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.frontend.model.tokens.TokenReader;
import org.ooc.frontend.model.tokens.Token.TokenType;
import org.ubi.SourceReader;

public class TypeParser {

	public static Type parse(SourceReader sReader, TokenReader reader) {
		
		String name = "";
		int pointerLevel = 0;
		
		while(reader.hasNext()) {
			Token t = reader.peek();
			if(t.type == TokenType.UNSIGNED) {
				reader.skip();
				name += "unsigned ";
			} else if(t.type == TokenType.SIGNED) {
				reader.skip();
				name += "signed ";
			} else if(t.type == TokenType.LONG) {
				reader.skip();
				name += "long ";
			} else break;
		}
			
		if(reader.peek().type == TokenType.NAME) {
			name += reader.read().get(sReader);
		}
		
		while(reader.peek().type == TokenType.STAR) {
			pointerLevel++;
			reader.skip();
		}
		
		if(!name.isEmpty()) {
			return new Type(name.trim(), pointerLevel);
		}
		return null;
		
	}
	
}
