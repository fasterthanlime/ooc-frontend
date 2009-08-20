package org.ooc.frontend.parser;

import java.io.IOException;

import org.ooc.frontend.model.ControlStatement;
import org.ooc.frontend.model.Line;
import org.ooc.frontend.model.SingleLineComment;
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
		
		if(reader.peek().type == TokenType.SL_COMMENT) {
			Token t = reader.read();
			return new SingleLineComment(t.get(sReader));
		}
		
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
			if(next.type != TokenType.LINESEP && next.type != TokenType.SL_COMMENT) {
				throw new CompilationFailedError(sReader.getLocation(next.start),
						"Missing semi-colon at the end of a line (got a "+next.type+" instead)");
			}
		}
		
		return new Line(statement);
		
	}

	
}
