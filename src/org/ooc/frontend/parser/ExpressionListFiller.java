package org.ooc.frontend.parser;

import static org.ooc.frontend.model.tokens.Token.TokenType.CLOS_PAREN;
import static org.ooc.frontend.model.tokens.Token.TokenType.COMMA;
import static org.ooc.frontend.model.tokens.Token.TokenType.OPEN_PAREN;

import java.io.IOException;

import org.ooc.frontend.model.Expression;
import org.ooc.frontend.model.NodeList;
import org.ooc.frontend.model.tokens.TokenReader;
import org.ubi.CompilationFailedError;
import org.ubi.SourceReader;

public class ExpressionListFiller {

	public static boolean fill(SourceReader sReader, TokenReader reader,
			NodeList<Expression> list) throws IOException {

		int mark = reader.mark();
		
		if(!reader.hasNext()) return false;
		
		if(reader.read().type != OPEN_PAREN) {
			reader.reset(mark);
			return false;
		}
		
		boolean comma = false;
		while(true) {
			
			if(reader.peek().type == CLOS_PAREN) {
				reader.skip(); // skip the ')'
				break;
			}
			if(comma) {
				if(reader.read().type != COMMA) {
					throw new CompilationFailedError(sReader.getLocation(reader.prev().start),
							"Expected comma between arguments of a function call");
				}
			} else {
				reader.skipNonWhitespace();
				Expression expr = ExpressionParser.parse(sReader, reader);
				if(expr == null) {
					throw new CompilationFailedError(sReader.getLocation(reader.peek().start),
							"Expected expression as argument of function call");
				}
				list.add(expr);
			}
			comma = !comma;
			
		}
		
		return true;
		
	}
	
}
