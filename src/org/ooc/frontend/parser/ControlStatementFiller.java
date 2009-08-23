package org.ooc.frontend.parser;

import java.io.IOException;

import org.ooc.frontend.model.ControlStatement;
import org.ooc.frontend.model.Line;
import org.ooc.frontend.model.tokens.TokenReader;
import org.ooc.frontend.model.tokens.Token.TokenType;
import org.ubi.CompilationFailedError;
import org.ubi.SourceReader;

public class ControlStatementFiller {

	public static void fill(SourceReader sReader,
			TokenReader reader, ControlStatement controlStatement)
			throws IOException {
		
		boolean hasBrack = false;
		if(reader.peek().type == TokenType.OPEN_BRACK) {
			reader.skip();
			hasBrack = true;
		}
		
		if(hasBrack) {
			while(reader.hasNext() && reader.peek().type != TokenType.CLOS_BRACK) {
				if(reader.skipWhitespace()) continue;
				
				Line line = LineParser.parse(sReader, reader);
				if(line == null) {
					throw new CompilationFailedError(sReader.getLocation(reader.peek()),
					"Expected line inside of "+controlStatement.getClass().getSimpleName());
				}
				controlStatement.getBody().add(line);
			}
			reader.skip(); // the closing bracket
		} else {
			Line only = LineParser.parse(sReader, reader);
			if(only == null) {
				throw new CompilationFailedError(sReader.getLocation(reader.peek()),
				"Expected line inside of bracket-less "+controlStatement.getClass().getSimpleName());
			}
			controlStatement.getBody().add(only);
		}
	}
	
}
