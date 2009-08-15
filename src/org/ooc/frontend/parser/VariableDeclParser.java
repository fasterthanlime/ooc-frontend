package org.ooc.frontend.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.ooc.frontend.model.Expression;
import org.ooc.frontend.model.Type;
import org.ooc.frontend.model.VariableDecl;
import org.ooc.frontend.model.VariableDecl.VariableDeclAtom;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.frontend.model.tokens.TokenReader;
import org.ooc.frontend.model.tokens.Token.TokenType;
import org.ubi.CompilationFailedError;
import org.ubi.SourceReader;

public class VariableDeclParser {

	public static VariableDecl parse(SourceReader sReader, TokenReader reader) throws IOException {
		int mark = reader.mark();

		List<VariableDeclAtom> atoms = new ArrayList<VariableDeclAtom>();
		
		if(reader.peek().type != TokenType.NAME) {
			reader.reset(mark);
			return null;
		}
		while(reader.peek().type == TokenType.NAME) {
			String name = reader.read().get(sReader);
			Expression expr = null;
			if(reader.peek().type == TokenType.ASSIGN) {
				reader.skip();
				expr = ExpressionParser.parse(sReader, reader);
				if(expr == null) {
					throw new CompilationFailedError(sReader.getLocation(reader.prev().start),
							"Expected expression as an initializer to a variable declaration.");
				}
			}
			atoms.add(new VariableDeclAtom(name, expr));
			if(reader.peek().type != TokenType.COMMA) break;
			reader.skip();
		}
		
		if(reader.read().type != TokenType.COLON) {
			reader.reset();
			return null;
		}
		
		boolean isConst = false;
		boolean isStatic = false;
		boolean isExtern = false;
		
		while(true) {
			Token t = reader.peek();
			if(t.type == TokenType.CONST_KW) {
				isConst = true;
				reader.skip();
			} else if(t.type == TokenType.STATIC_KW) {
				isStatic = true;
				reader.skip();
			} else if(t.type == TokenType.EXTERN_KW) {
				isExtern = true;
				reader.skip();
			} else {
				break;
			}
		}
		
		Type type = TypeParser.parse(sReader, reader);
		if(type == null) {
			reader.reset(mark);
			return null;
		}
		
		VariableDecl decl = new VariableDecl(type, isConst, isStatic, isExtern);
		decl.getAtoms().addAll(atoms);
		return decl;
	}
	
}
