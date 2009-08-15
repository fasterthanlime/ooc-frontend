package org.ooc.frontend.parser;

import java.io.IOException;

import org.ooc.frontend.model.Conditional;
import org.ooc.frontend.model.Expression;
import org.ooc.frontend.model.If;
import org.ooc.frontend.model.While;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.frontend.model.tokens.TokenReader;
import org.ooc.frontend.model.tokens.Token.TokenType;
import org.ubi.CompilationFailedError;
import org.ubi.SourceReader;

public class ConditionalParser {

	public static Conditional parse(
			SourceReader sReader, TokenReader reader) throws IOException {
		
		int mark = reader.mark();
		
		Token token = reader.read();
		if(token.type != TokenType.WHILE_KW && token.type != TokenType.IF_KW) {
			reader.reset(mark);
			return null;
		}
		
		if(reader.read().type != TokenType.OPEN_PAREN) {
			throw new CompilationFailedError(sReader.getLocation(reader.prev().start),
				"Expected opening parenthesis after "+token.get(sReader));
		}
		
		Expression condition = ExpressionParser.parse(sReader, reader);
		if(condition == null) {
			throw new CompilationFailedError(sReader.getLocation(reader.peek().start),
					"Expected expression as while condition");
		}
		
		if(reader.read().type != TokenType.CLOS_PAREN) {
			throw new CompilationFailedError(sReader.getLocation(reader.prev().start),
				"Expected closing parenthesis after expression of an "+token.get(sReader));
		}
		
		Conditional statement;
		if(token.type == TokenType.WHILE_KW) {
			statement = new While(condition);
		} else if(token.type == TokenType.IF_KW) {
			statement = new If(condition);
		} else {
			reader.reset(mark);
			return null;
		}
		ControlStatementFiller.fill(sReader, reader, statement);
		
		return statement;
		
	}

	
}
