package org.ooc.frontend.parser;

import org.ooc.frontend.model.OocDocComment;
import org.ooc.frontend.model.Visitable;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.frontend.model.tokens.TokenReader;
import org.ooc.frontend.model.tokens.Token.TokenType;
import org.ubi.SourceReader;

public class CommentParser {

	public static Visitable parse(SourceReader sReader, TokenReader reader) {
		
		Token t = reader.peek();
		if(t.type == TokenType.OOCDOC) {
			reader.skip();
			return new OocDocComment(t.get(sReader)); // FIXME lazy
		}
		
		return null;
		
	}
	
}
