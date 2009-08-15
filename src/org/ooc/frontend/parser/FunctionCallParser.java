package org.ooc.frontend.parser;

import static org.ooc.frontend.model.tokens.Token.TokenType.NAME;
import static org.ooc.frontend.model.tokens.Token.TokenType.SUPER_KW;
import static org.ooc.frontend.model.tokens.Token.TokenType.THIS_KW;
import static org.ooc.frontend.model.tokens.Token.TokenType.TILDE;

import java.io.IOException;

import org.ooc.frontend.model.FunctionCall;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.frontend.model.tokens.TokenReader;
import org.ubi.CompilationFailedError;
import org.ubi.SourceReader;

public class FunctionCallParser {

	public static FunctionCall parse(SourceReader sReader, TokenReader reader) throws IOException {
		
		int mark = reader.mark();
		
		Token tName = reader.read();
		if(tName.type != NAME && tName.type != THIS_KW && tName.type != SUPER_KW) {
			reader.reset(mark);
			return null;
		}
		String name = tName.get(sReader);
		
		String suffix = "";
		if(reader.peek().type == TILDE) {
			reader.skip();
			Token tSuff = reader.read();
			if(tSuff.type != NAME) {
				throw new CompilationFailedError(sReader.getLocation(tSuff.start),
				"Expecting suffix after 'functionname~' !");
			}
			suffix = tSuff.get(sReader);
		}

		FunctionCall call = new FunctionCall(name, suffix);
		
		if(!ExpressionListFiller.fill(sReader, reader, call.getArguments())) {
			reader.reset(mark);
			return null; // not a function call
		}
		
		return call;
		
	}
	
}
