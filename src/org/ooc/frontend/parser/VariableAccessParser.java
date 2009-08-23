package org.ooc.frontend.parser;

import org.ooc.frontend.model.VariableAccess;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.frontend.model.tokens.TokenReader;
import org.ooc.frontend.model.tokens.Token.TokenType;
import org.ubi.SourceReader;

public class VariableAccessParser {

	public static VariableAccess parse(SourceReader sReader, TokenReader reader) {
		int mark = reader.mark();
		
		Token token = reader.peek();
		if(token.type == TokenType.NAME || token.type == TokenType.THIS_KW) {
			reader.skip();
			return new VariableAccess(token.get(sReader), token);
		}
		
		reader.reset(mark);
		return null;
	}
	
}
