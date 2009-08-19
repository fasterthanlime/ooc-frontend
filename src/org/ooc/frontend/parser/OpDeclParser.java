package org.ooc.frontend.parser;

import java.io.IOException;

import org.ooc.frontend.model.FunctionDecl;
import org.ooc.frontend.model.OpDecl;
import org.ooc.frontend.model.OpDecl.OpType;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.frontend.model.tokens.TokenReader;
import org.ooc.frontend.model.tokens.Token.TokenType;
import org.ubi.CompilationFailedError;
import org.ubi.SourceReader;

public class OpDeclParser {

	public static OpDecl parse(SourceReader sReader, TokenReader reader) throws IOException {
		
		if(reader.peek().type != TokenType.OPERATOR_KW) return null;
		reader.skip();
		
		OpType type;
		
		Token token = reader.read();
		if(token.type == TokenType.PLUS) {
			type = OpType.ADD;
		} if(token.type == TokenType.OPEN_SQUAR) {
			if(reader.peek().type == TokenType.CLOS_SQUAR) {
				reader.skip();
				if(reader.peek().type == TokenType.ASSIGN) {
					reader.skip();
					type = OpType.INDEXED_ASSIGN;
				} else {
					type = OpType.INDEXING;
				}
			} else {
				throw new CompilationFailedError(null, "Unexpected token "+reader.peek().type
						+". You're probably trying to override [] (indexing) or []= (indexed assign)");
			}
		} else {
			throw new CompilationFailedError(null, "Trying to overload unknown operator "+token.type);
		}
		
		FunctionDecl decl = FunctionDeclParser.parse(sReader, reader);
		if(decl == null) {
			throw new CompilationFailedError(null, "Expected function after operator overloading of "+type);
		}
		
		return new OpDecl(type, decl);
		
	}

}
