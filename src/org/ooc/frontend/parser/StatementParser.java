package org.ooc.frontend.parser;

import java.io.IOException;

import org.ooc.frontend.model.Conditional;
import org.ooc.frontend.model.Expression;
import org.ooc.frontend.model.Foreach;
import org.ooc.frontend.model.Return;
import org.ooc.frontend.model.Statement;
import org.ooc.frontend.model.tokens.TokenReader;
import org.ubi.SourceReader;

public class StatementParser {

	public static Statement parse(SourceReader sReader, TokenReader reader) throws IOException {

		int mark = reader.mark();
		
		Foreach foreach = ForeachParser.parse(sReader, reader);
		if(foreach != null) return foreach;
		
		Conditional conditional = ConditionalParser.parse(sReader, reader);
		if(conditional != null) return conditional;
		
		Return ret = ReturnParser.parse(sReader, reader);
		if(ret != null) return ret;
		
		Expression expression = ExpressionParser.parse(sReader, reader);
		if(expression != null) return expression;
		
		reader.reset(mark);
		return null;
		
	}
	
}
