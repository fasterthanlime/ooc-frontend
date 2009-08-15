package org.ooc.frontend.parser;

import org.ooc.frontend.model.SingleLineComment;
import org.ooc.frontend.model.Visitable;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.frontend.model.tokens.TokenReader;
import org.ooc.frontend.model.tokens.Token.TokenType;
import org.ubi.SourceReader;

public class CommentParser {

	public static Visitable parse(SourceReader sReader, TokenReader reader) {
		
		Token t = reader.peek();
		
		if(t.type == TokenType.SL_COMMENT) {
			reader.skip();
			return new SingleLineComment(t.get(sReader));
		}

		if(t.type == TokenType.ML_COMMENT) {
			reader.skip();
			return new SingleLineComment(t.get(sReader)); // FIXME lazy 
		}
		
		if(t.type == TokenType.OOCDOC) {
			reader.skip();
			return new SingleLineComment(t.get(sReader)); // FIXME lazy
		}
		
		return null;
		
	}
	
}
