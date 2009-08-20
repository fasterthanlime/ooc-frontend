package org.ooc.frontend.parser;

import java.io.IOException;

import org.ooc.frontend.model.ArrayLiteral;
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
import org.ooc.frontend.model.tokens.Token.TokenType;
import org.ubi.CompilationFailedError;
import org.ubi.SourceReader;
import org.ubi.SyntaxError;

public class LiteralParser {

	public static Literal parse(SourceReader sReader, TokenReader reader) throws IOException {

		int mark = reader.mark();
		
		Token t = reader.read();
		if(t.type == TokenType.STRING_LIT) {
			return new StringLiteral(t.get(sReader));
		}
		if(t.type == TokenType.CHAR_LIT) {
			try {
				return new CharLiteral(SourceReader.parseCharLiteral(t.get(sReader)));			} catch (SyntaxError e) {
				throw new CompilationFailedError(sReader.getLocation(t.start), "Malformed char literal");
			}
		}
		if(t.type == TokenType.DEC_INT) 
			return new IntLiteral(Long.parseLong(t.get(sReader)
					.replace("_", "")), Format.DEC);
		if(t.type == TokenType.HEX_INT)
			return new IntLiteral(Long.parseLong(t.get(sReader)
					.replace("_", "").toUpperCase(), 16), Format.HEX);
		if(t.type == TokenType.OCT_INT)
			return new IntLiteral(Long.parseLong(t.get(sReader)
					.replace("_", "").toUpperCase(), 8), Format.OCT);
		if(t.type == TokenType.BIN_INT)
			return new IntLiteral(Long.parseLong(t.get(sReader)
					.replace("_", "").toUpperCase(), 2), Format.BIN);
		if(t.type == TokenType.DEC_FLOAT)
			return new FloatLiteral(Double.parseDouble(t.get(sReader)
					.replace("_", "")));
		if(t.type == TokenType.TRUE)
			return new BoolLiteral(true);
		if(t.type == TokenType.FALSE)
			return new BoolLiteral(false);
		if(t.type == TokenType.NULL)
			return new NullLiteral();
		if(t.type == TokenType.OPEN_SQUAR) {
			ArrayLiteral arrayLiteral = new ArrayLiteral();
			reader.rewind();
			if(!ExpressionListFiller.fill(sReader, reader, arrayLiteral.getElements(),
					TokenType.OPEN_SQUAR, TokenType.CLOS_SQUAR)) {
				throw new CompilationFailedError(null, "Malformed array literal");
			}
			return arrayLiteral;
		}
			
		
		reader.reset(mark);
		return null;
		
	}
	
}
