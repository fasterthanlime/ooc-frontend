package org.ooc.frontend.parser;

import java.io.IOException;

import org.ooc.frontend.model.Instantiation;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.frontend.model.tokens.TokenReader;
import org.ooc.frontend.model.tokens.Token.TokenType;
import org.ubi.SourceReader;

public class InstantiationParser {

	public static Instantiation parse(SourceReader sReader, TokenReader reader) throws IOException {

		int mark = reader.mark();
		
		if(reader.read().type == TokenType.NEW_KW) {
			Token tName = reader.peek();
			if(tName.type == TokenType.NAME) {
				reader.skip();
				String name = tName.get(sReader);
				Instantiation inst = new Instantiation(name, ""); // TODO add suffix parsing
				ExpressionListFiller.fill(sReader, reader, inst.getArguments());
				return inst;
			}
			Instantiation inst = new Instantiation();
			ExpressionListFiller.fill(sReader, reader, inst.getArguments());
			return inst;
		}
		
		reader.reset(mark);
		return null;
		
	}
	
}
