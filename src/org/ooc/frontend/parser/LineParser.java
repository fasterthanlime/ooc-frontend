package org.ooc.frontend.parser;

import java.io.IOException;

import org.ooc.frontend.model.ControlStatement;
import org.ooc.frontend.model.Line;
import org.ooc.frontend.model.Statement;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.frontend.model.tokens.TokenReader;
import org.ooc.frontend.model.tokens.Token.TokenType;
import org.ubi.CompilationFailedError;
import org.ubi.SourceReader;

public class LineParser {

	public static Line parse(SourceReader sReader, TokenReader reader) throws IOException {

		int mark = reader.mark();
		
		reader.skipWhitespace();
		
		while(reader.peek().type == TokenType.LINESEP) reader.skip();
		
		if(!reader.hasNext()) {
			reader.reset(mark);
			return null;
		}
		
		Statement statement = StatementParser.parse(sReader, reader);
		if(statement == null) {
			reader.reset(mark);
			return null;	
		}
		
		if(!(statement instanceof ControlStatement)) {
			Token next = reader.read();
			if(next.type != TokenType.LINESEP) {
				throw new CompilationFailedError(sReader.getLocation(next),
						"Missing semi-colon at the end of a line (got a "+next.type+" instead)");
			}
		}
		
		return new Line(statement);
		
	}

	
}
