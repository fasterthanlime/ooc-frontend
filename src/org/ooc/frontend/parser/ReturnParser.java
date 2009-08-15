package org.ooc.frontend.parser;

import java.io.IOException;

import org.ooc.frontend.model.Expression;
import org.ooc.frontend.model.Return;
import org.ooc.frontend.model.ValuedReturn;
import org.ooc.frontend.model.tokens.TokenReader;
import org.ooc.frontend.model.tokens.Token.TokenType;
import org.ubi.SourceReader;

public class ReturnParser {

	public static Return parse(SourceReader sReader, TokenReader reader) throws IOException {

		int mark = reader.mark();
		
		if(reader.read().type == TokenType.RETURN_KW) {
			Expression expr = ExpressionParser.parse(sReader, reader);
			if(expr == null) return new Return();
			return new ValuedReturn(expr);
		}
		
		reader.reset(mark);
		return null;
		
	}
	
}
