package org.ooc.frontend.parser;

import static org.ooc.frontend.model.tokens.Token.TokenType.BIN_INT;
import static org.ooc.frontend.model.tokens.Token.TokenType.CHAR_LIT;
import static org.ooc.frontend.model.tokens.Token.TokenType.DEC_FLOAT;
import static org.ooc.frontend.model.tokens.Token.TokenType.DEC_INT;
import static org.ooc.frontend.model.tokens.Token.TokenType.FALSE;
import static org.ooc.frontend.model.tokens.Token.TokenType.HEX_INT;
import static org.ooc.frontend.model.tokens.Token.TokenType.NULL;
import static org.ooc.frontend.model.tokens.Token.TokenType.OCT_INT;
import static org.ooc.frontend.model.tokens.Token.TokenType.STRING_LIT;
import static org.ooc.frontend.model.tokens.Token.TokenType.TRUE;

import java.io.IOException;

import org.ooc.frontend.model.BoolLiteral;
import org.ooc.frontend.model.CharLiteral;
import org.ooc.frontend.model.FloatLiteral;
import org.ooc.frontend.model.IntLiteral;
import org.ooc.frontend.model.Literal;
import org.ooc.frontend.model.NullLiteral;
import org.ooc.frontend.model.StringLiteral;
import org.ooc.frontend.model.IntLiteral.Format;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.frontend.model.tokens.TokenReader;
import org.ubi.CompilationFailedError;
import org.ubi.SourceReader;
import org.ubi.SyntaxError;

public class LiteralParser {

	public static Literal parse(SourceReader sReader, TokenReader reader) throws IOException {

		int mark = reader.mark();
		
		Token t = reader.read();
		if(t.type == STRING_LIT) {
			return new StringLiteral(t.get(sReader));
		}
		if(t.type == CHAR_LIT) {
			try {
				return new CharLiteral(SourceReader.parseCharLiteral(t.get(sReader)));			} catch (SyntaxError e) {
				throw new CompilationFailedError(sReader.getLocation(t.start), "Malformed char literal");
			}
		}
		if(t.type == DEC_INT) 
			return new IntLiteral(Long.parseLong(t.get(sReader)
					.replace("_", "")), Format.DEC);
		if(t.type == HEX_INT)
			return new IntLiteral(Long.parseLong(t.get(sReader)
					.replace("_", "").toUpperCase(), 16), Format.HEX);
		if(t.type == OCT_INT)
			return new IntLiteral(Long.parseLong(t.get(sReader)
					.replace("_", "").toUpperCase(), 8), Format.OCT);
		if(t.type == BIN_INT)
			return new IntLiteral(Long.parseLong(t.get(sReader)
					.replace("_", "").toUpperCase(), 2), Format.BIN);
		if(t.type == DEC_FLOAT)
			return new FloatLiteral(Double.parseDouble(t.get(sReader)
					.replace("_", "")));
		if(t.type == TRUE)
			return new BoolLiteral(true);
		if(t.type == FALSE)
			return new BoolLiteral(false);
		if(t.type == NULL)
			return new NullLiteral();
		
		reader.reset(mark);
		return null;
		
	}
	
}
