package org.ooc.frontend.parser;

import java.io.IOException;

import org.ooc.frontend.model.CoverDecl;
import org.ooc.frontend.model.FunctionDecl;
import org.ooc.frontend.model.OocDocComment;
import org.ooc.frontend.model.Type;
import org.ooc.frontend.model.VariableDecl;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.frontend.model.tokens.TokenReader;
import org.ooc.frontend.model.tokens.Token.TokenType;
import org.ubi.CompilationFailedError;
import org.ubi.SourceReader;

public class CoverDeclParser {

	public static CoverDecl parse(SourceReader sReader, TokenReader reader) throws IOException {
		int mark = reader.mark();
		
		OocDocComment comment = null;
		if(reader.peek().type == TokenType.OOCDOC) {
			Token t = reader.read();
			comment = new OocDocComment(t.get(sReader));
		}
		
		String name = "";
		Token tName = reader.peek();
		if(tName.type == TokenType.NAME || tName.type == TokenType.NEW_KW) {
			name = tName.get(sReader);
			reader.skip();
			if(reader.read().type != TokenType.COLON) {
				reader.reset(mark);
				return null;
			}
		}
		
		if(reader.read().type == TokenType.COVER_KW) {
			
			Type fromType = null;
			if(reader.peek().type == TokenType.FROM_KW) {
				reader.skip();
				fromType = TypeParser.parse(sReader, reader);
				if(fromType == null) {
					throw new CompilationFailedError(sReader.getLocation(reader.peek().start),
					"Expected cover's base type name after the from keyword.");
				}
			}
			
			CoverDecl coverDecl = new CoverDecl(name, fromType);
			if(comment != null) coverDecl.setComment(comment);
			
			Token t2 = reader.read();
			if(t2.type != TokenType.OPEN_BRACK) {
				if(t2.type == TokenType.LINESEP) {
					return coverDecl; // empty cover, acts like a typedef
				}
				throw new CompilationFailedError(sReader.getLocation(t2.start),
						"Expected opening bracket to begin cover declaration, got "+t2.type);
			}
			
			while(reader.hasNext() && reader.peek().type != TokenType.CLOS_BRACK) {

				if(reader.peek().type == TokenType.LINESEP || reader.peek().type == TokenType.SL_COMMENT) {
					reader.skip(); continue;
				}
				
				VariableDecl varDecl = VariableDeclParser.parse(sReader, reader);
				if(varDecl != null) {
					if(reader.read().type != TokenType.LINESEP) {
						throw new CompilationFailedError(sReader.getLocation(reader.prev().start),
							"Expected semi-colon after variable declaration in cover declaration");
					}
					if(fromType != null && !varDecl.isExtern()) {
						throw new CompilationFailedError(sReader.getLocation(reader.prev().start),
							"You can't add non-extern member variables to a Cover which already has a base type (in this case, "
								+fromType.getName()+")");
					}
					coverDecl.addVariable(varDecl);
					continue;
				}
				
				FunctionDecl funcDecl = FunctionDeclParser.parse(sReader, reader);
				if(funcDecl != null) {
					coverDecl.addFunction(funcDecl);
					continue;
				}
				
				throw new CompilationFailedError(sReader.getLocation(reader.peek().start),
						"Expected variable declaration or function declaration in a cover declaration, but got "+reader.peek().length);
			
			}
			reader.skip();
			
			return coverDecl;
			
		}
		
		reader.reset(mark);
		return null;
	}
	
}
