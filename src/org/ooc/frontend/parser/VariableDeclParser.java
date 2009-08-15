package org.ooc.frontend.parser;

import static org.ooc.frontend.model.tokens.Token.TokenType.ASSIGN;
import static org.ooc.frontend.model.tokens.Token.TokenType.COLON;
import static org.ooc.frontend.model.tokens.Token.TokenType.COMMA;
import static org.ooc.frontend.model.tokens.Token.TokenType.CONST_KW;
import static org.ooc.frontend.model.tokens.Token.TokenType.EXTERN_KW;
import static org.ooc.frontend.model.tokens.Token.TokenType.NAME;
import static org.ooc.frontend.model.tokens.Token.TokenType.STATIC_KW;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.ooc.frontend.model.Expression;
import org.ooc.frontend.model.Type;
import org.ooc.frontend.model.VariableDecl;
import org.ooc.frontend.model.VariableDecl.VariableDeclAtom;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.frontend.model.tokens.TokenReader;
import org.ubi.CompilationFailedError;
import org.ubi.SourceReader;

public class VariableDeclParser {

	public static VariableDecl parse(SourceReader sReader, TokenReader reader) throws IOException {
		int mark = reader.mark();

		List<VariableDeclAtom> atoms = new ArrayList<VariableDeclAtom>();
		
		if(reader.peek().type != NAME) {
			reader.reset(mark);
			return null;
		}
		while(reader.peek().type == NAME) {
			String name = reader.read().get(sReader);
			Expression expr = null;
			if(reader.peek().type == ASSIGN) {
				reader.skip();
				expr = ExpressionParser.parse(sReader, reader);
				if(expr == null) {
					throw new CompilationFailedError(sReader.getLocation(reader.prev().start),
							"Expected expression as an initializer to a variable declaration.");
				}
			}
			atoms.add(new VariableDeclAtom(name, expr));
			if(reader.peek().type != COMMA) break;
			reader.skip();
		}
		
		if(reader.read().type != COLON) {
			reader.reset();
			return null;
		}
		
		boolean isConst = false;
		boolean isStatic = false;
		boolean isExtern = false;
		
		while(true) {
			Token t = reader.peek();
			if(t.type == CONST_KW) {
				isConst = true;
				reader.skip();
			} else if(t.type == STATIC_KW) {
				isStatic = true;
				reader.skip();
			} else if(t.type == EXTERN_KW) {
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
