package org.ooc.frontend.parser;

import static org.ooc.frontend.model.tokens.Token.TokenType.ABSTRACT_KW;
import static org.ooc.frontend.model.tokens.Token.TokenType.CLASS_KW;
import static org.ooc.frontend.model.tokens.Token.TokenType.CLOS_BRACK;
import static org.ooc.frontend.model.tokens.Token.TokenType.COLON;
import static org.ooc.frontend.model.tokens.Token.TokenType.FROM_KW;
import static org.ooc.frontend.model.tokens.Token.TokenType.LINESEP;
import static org.ooc.frontend.model.tokens.Token.TokenType.NAME;
import static org.ooc.frontend.model.tokens.Token.TokenType.NEW_KW;
import static org.ooc.frontend.model.tokens.Token.TokenType.OOCDOC;
import static org.ooc.frontend.model.tokens.Token.TokenType.OPEN_BRACK;

import java.io.IOException;

import org.ooc.frontend.model.ClassDecl;
import org.ooc.frontend.model.FunctionDecl;
import org.ooc.frontend.model.OocDocComment;
import org.ooc.frontend.model.VariableDecl;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.frontend.model.tokens.TokenReader;
import org.ubi.CompilationFailedError;
import org.ubi.SourceReader;

public class ClassDeclParser {

	public static ClassDecl parse(SourceReader sReader, TokenReader reader) throws IOException {

		int mark = reader.mark();
		
		OocDocComment comment = null;
		if(reader.peek().type == OOCDOC) {
			Token t = reader.read();
			comment = new OocDocComment(t.get(sReader));
		}
		
		String name = "";
		Token tName = reader.peek();
		if(tName.type == NAME || tName.type == NEW_KW) {
			name = tName.get(sReader);
			reader.skip();
			if(reader.read().type != COLON) {
				reader.reset(mark);
				return null;
			}
		}
		
		boolean isAbstract = reader.peek().type == ABSTRACT_KW;
		if(isAbstract) {
			reader.skip();
		}
		
		if(reader.read().type == CLASS_KW) {
			
			String superName = "";
			if(reader.peek().type == FROM_KW) {
				reader.skip();
				Token tSuper = reader.read();
				if(tSuper.type != NAME) {
					throw new CompilationFailedError(sReader.getLocation(reader.peek().start),
					"Expected super-class name after the from keyword.");
				}
				superName = tSuper.get(sReader);
				
			}
			
			Token t2 = reader.read();
			if(t2.type != OPEN_BRACK) {
				throw new CompilationFailedError(sReader.getLocation(t2.start),
						"Expected opening bracket to begin class declaration.");
			}
			
			ClassDecl classDecl = new ClassDecl(name, isAbstract);
			classDecl.setSuperName(superName);
			if(comment != null) classDecl.setComment(comment);
			
			while(reader.hasNext() && reader.peek().type != CLOS_BRACK) {
			
				if(reader.peek().type == LINESEP) {
					reader.skip(); continue;
				}
				
				VariableDecl varDecl = VariableDeclParser.parse(sReader, reader);
				if(varDecl != null) {
					if(reader.read().type != LINESEP) {
						throw new CompilationFailedError(sReader.getLocation(reader.prev().start),
							"Expected semi-colon after variable declaration in class declaration");
					}
					classDecl.getVariables().add(varDecl);
					continue;
				}
				
				FunctionDecl funcDecl = FunctionDeclParser.parse(sReader,reader);
				if(funcDecl != null) {
					classDecl.getFunctions().add(funcDecl);
					continue;
				}
				
				throw new CompilationFailedError(sReader.getLocation(reader.peek().start),
						"Expected variable declaration or function declaration in a class declaration, got "+reader.peek().type);
			
			}
			reader.skip();
			
			return classDecl;
			
		}
		
		reader.reset(mark);
		return null;
		
	}
	
}
