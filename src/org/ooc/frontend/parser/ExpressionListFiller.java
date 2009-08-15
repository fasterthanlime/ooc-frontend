package org.ooc.frontend.parser;

import java.io.IOException;

import org.ooc.frontend.model.Expression;
import org.ooc.frontend.model.NodeList;
import org.ooc.frontend.model.tokens.TokenReader;
import org.ooc.frontend.model.tokens.Token.TokenType;
import org.ubi.CompilationFailedError;
import org.ubi.SourceReader;

public class ExpressionListFiller {

	public static boolean fill(SourceReader sReader, TokenReader reader,
			NodeList<Expression> list) throws IOException {

		int mark = reader.mark();
		
		if(!reader.hasNext()) return false;
		
		if(reader.read().type != TokenType.OPEN_PAREN) {
			reader.reset(mark);
			return false;
		}
		
		boolean comma = false;
		while(true) {
			
			if(reader.peekWhiteless().type == TokenType.CLOS_PAREN) {
				reader.skipNonWhitespace();
				reader.skip(); // skip the ')'
				break;
			}
			if(comma) {
				if(reader.readWhiteless().type != TokenType.COMMA) {
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
