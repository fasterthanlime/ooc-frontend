package org.ooc.frontend.parser;

import static org.ooc.frontend.model.tokens.Token.TokenType.ML_COMMENT;
import static org.ooc.frontend.model.tokens.Token.TokenType.OOCDOC;
import static org.ooc.frontend.model.tokens.Token.TokenType.SL_COMMENT;

import org.ooc.frontend.model.SingleLineComment;
import org.ooc.frontend.model.Visitable;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.frontend.model.tokens.TokenReader;
import org.ubi.SourceReader;

public class CommentParser {

	public static Visitable parse(SourceReader sReader, TokenReader reader) {
		
		Token t = reader.peek();
		
		if(t.type == SL_COMMENT) {
			reader.skip();
			return new SingleLineComment(t.get(sReader));
		}

		if(t.type == ML_COMMENT) {
			reader.skip();
			return new SingleLineComment(t.get(sReader)); // FIXME lazy 
		}
		
		if(t.type == OOCDOC) {
			reader.skip();
			return new SingleLineComment(t.get(sReader)); // FIXME lazy
		}
		
		return null;
		
	}
	
}
