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
		Token startToken = reader.peek();
		if(startToken.type == TokenType.OOCDOC) {
			comment = new OocDocComment(reader.read().get(sReader), startToken);
		}
		
		String name = "";
		Token tName = reader.peek();
		if(tName.isNameToken()) {
			name = tName.get(sReader);
			reader.skip();
			if(reader.read().type != TokenType.COLON) {
				reader.reset(mark);
				return null;
			}
		}
		
		String externName = ExternParser.parse(sReader, reader);
		
		if(reader.read().type == TokenType.COVER_KW) {

			Type overType = null;
			String superName = "";
			while(true) {
				Token token = reader.peek();
				if(token.type == TokenType.FROM_KW) {
					reader.skip();
					overType = TypeParser.parse(sReader, reader);
					if(overType == null) {
						throw new CompilationFailedError(sReader.getLocation(reader.peek()),
						"Expected cover's base type name after the from keyword.");
					}
				} else if(token.type == TokenType.EXTENDS_KW) {
					reader.skip();
					superName = reader.read().get(sReader);
				}
				break;
			}
			
			CoverDecl coverDecl = new CoverDecl(name, superName, overType, tName);
			coverDecl.setExternName(externName);
			if(comment != null) coverDecl.setComment(comment);
			
			Token t2 = reader.read();
			if(t2.type != TokenType.OPEN_BRACK) {
				if(t2.type == TokenType.LINESEP) {
					return coverDecl; // empty cover, acts like a typedef
				}
				throw new CompilationFailedError(sReader.getLocation(t2),
						"Expected opening bracket to begin cover declaration, got "+t2.type);
			}
			
			while(reader.hasNext() && reader.peek().type != TokenType.CLOS_BRACK) {

				if(reader.peek().type == TokenType.LINESEP) {
					reader.skip(); continue;
				}
				
				VariableDecl varDecl = VariableDeclParser.parse(sReader, reader);
				if(varDecl != null) {
					if(reader.read().type != TokenType.LINESEP) {
						throw new CompilationFailedError(sReader.getLocation(reader.prev()),
							"Expected newline after variable declaration in cover declaration, but got "+reader.prev());
					}
					if(overType != null && !varDecl.isExtern()) {
						throw new CompilationFailedError(sReader.getLocation(reader.prev()),
							"You can't add non-extern member variables to a Cover which already has a base type (in this case, "
								+overType.getName()+")");
					}
					coverDecl.addVariable(varDecl);
					continue;
				}
				
				FunctionDecl funcDecl = FunctionDeclParser.parse(sReader, reader, false);
				if(funcDecl != null) {
					coverDecl.addFunction(funcDecl);
					continue;
				}
				
				throw new CompilationFailedError(sReader.getLocation(reader.peek()),
						"Expected variable declaration or function declaration in a cover declaration, but got "
						+reader.peek().type);
			
			}
			reader.skip();
			
			return coverDecl;
			
		}
		
		reader.reset(mark);
		return null;
	}
	
}
