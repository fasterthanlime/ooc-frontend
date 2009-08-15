package org.ooc.frontend.parser;

import static org.ooc.frontend.model.tokens.Token.TokenType.CLOS_PAREN;
import static org.ooc.frontend.model.tokens.Token.TokenType.FOR_KW;
import static org.ooc.frontend.model.tokens.Token.TokenType.IN_KW;
import static org.ooc.frontend.model.tokens.Token.TokenType.OPEN_PAREN;

import java.io.IOException;

import org.ooc.frontend.model.Expression;
import org.ooc.frontend.model.Foreach;
import org.ooc.frontend.model.VariableDecl;
import org.ooc.frontend.model.tokens.TokenReader;
import org.ubi.CompilationFailedError;
import org.ubi.SourceReader;

public class ForeachParser {

	public static Foreach parse(SourceReader sReader, TokenReader reader) throws IOException {

		int mark = reader.mark();
		
		if(reader.read().type == FOR_KW) {
			if(reader.read().type != OPEN_PAREN) {
				throw new CompilationFailedError(sReader.getLocation(reader.prev().start),
					"Expected opening parenthesis after for");
			}
			
			VariableDecl variable = VariableDeclParser.parse(sReader, reader);
			if(variable == null) {
				reader.reset(mark);
				return null;
			}
			
			if(reader.read().type != IN_KW) {
				reader.reset(mark);
				return null;
			}
			
			Expression collection = ExpressionParser.parse(sReader, reader);
			if(collection == null) {
				throw new CompilationFailedError(sReader.getLocation(reader.peek().start),
						"Expected expression after 'in' keyword in a foreach");
			}
			
			if(reader.read().type != CLOS_PAREN) {
				throw new CompilationFailedError(sReader.getLocation(reader.prev().start),
					"Expected closing parenthesis at the end of a foreach");
			}
			
			Foreach foreach = new Foreach(variable, collection);
			ControlStatementFiller.fill(sReader, reader, foreach);
			return foreach;
			
		}
		
		reader.reset(mark);
		return null;
		
	}
	
}
