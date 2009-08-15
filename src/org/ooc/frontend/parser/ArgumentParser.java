package org.ooc.frontend.parser;

import static org.ooc.frontend.model.tokens.Token.TokenType.ASSIGN;
import static org.ooc.frontend.model.tokens.Token.TokenType.COLON;
import static org.ooc.frontend.model.tokens.Token.TokenType.DOT;
import static org.ooc.frontend.model.tokens.Token.TokenType.NAME;
import static org.ooc.frontend.model.tokens.Token.TokenType.TRIPLE_DOT;

import java.io.IOException;

import org.ooc.frontend.model.Argument;
import org.ooc.frontend.model.MemberArgument;
import org.ooc.frontend.model.MemberAssignArgument;
import org.ooc.frontend.model.RegularArgument;
import org.ooc.frontend.model.Type;
import org.ooc.frontend.model.VarArg;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.frontend.model.tokens.TokenReader;
import org.ubi.CompilationFailedError;
import org.ubi.SourceReader;

public class ArgumentParser {

	public static Argument parse(SourceReader sReader, TokenReader reader, boolean isExtern) throws IOException {

		if(reader.peek().type == TRIPLE_DOT) {
			reader.skip();
			return new VarArg();
		}
		
		Token t = reader.read();
		if(t.type == NAME) {
			if(reader.peek().type == COLON) {
				reader.skip();
				Type type = TypeParser.parse(sReader, reader);
				if(type == null) {
					throw new CompilationFailedError(sReader.getLocation(reader.peek().start),
							"Expected argument type after its name and ':'");
				}
				return new RegularArgument(type, t.get(sReader));
			}
			reader.rewind();
		}
		
		if(t.type == ASSIGN) {
			Token t2 = reader.read();
			if(t2.type != NAME) {
				throw new CompilationFailedError(sReader.getLocation(t2.start),
						"Expecting member variable name in member-assign-argument");
			}
			return new MemberAssignArgument(t2.get(sReader));
		}
		
		if(t.type == DOT) {
			Token t2 = reader.read();
			if(t2.type != NAME) {
				throw new CompilationFailedError(sReader.getLocation(t2.start),
						"Expecting member variable name in member-assign-argument");
			}
			return new MemberArgument(t2.get(sReader));
		}
		
		if(isExtern) {
			Type type = TypeParser.parse(sReader, reader);
			if(type == null) {
				throw new CompilationFailedError(sReader.getLocation(reader.peek().start),
					"Expected argument type in extern func definition ':'");
			}
			return new TypeArgument(type);
		}
		
		reader.reset();
		return null;
		
	}
	
}
