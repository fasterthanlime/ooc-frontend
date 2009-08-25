package org.ooc.frontend.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.ooc.frontend.model.FunctionDecl;
import org.ooc.frontend.model.Line;
import org.ooc.frontend.model.OocDocComment;
import org.ooc.frontend.model.Type;
import org.ooc.frontend.model.TypeParam;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.frontend.model.tokens.TokenReader;
import org.ooc.frontend.model.tokens.Token.TokenType;
import org.ubi.CompilationFailedError;
import org.ubi.SourceReader;

public class FunctionDeclParser {

	public static FunctionDecl parse(SourceReader sReader, TokenReader reader) throws IOException {
		return parse(sReader, reader, false);
	}
	
	public static FunctionDecl parse(SourceReader sReader, TokenReader reader, boolean skipFunc) throws IOException {

		int mark = reader.mark();
		
		OocDocComment comment = null;
		if(reader.peek().type == TokenType.OOCDOC) {
			Token token = reader.read();
			comment = new OocDocComment(token.get(sReader), token);
		}
		
		Token startToken= reader.peek();
		
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
		
		boolean isProto = false;
		boolean isAbstract = false;
		boolean isStatic = false;
		boolean isFinal = false;
		String externName = null;
		
		Token kw = reader.peek();
		while(kw.type == TokenType.ABSTRACT_KW
		   || kw.type == TokenType.STATIC_KW
		   || kw.type == TokenType.FINAL_KW
		   || kw.type == TokenType.EXTERN_KW
		   || kw.type == TokenType.PROTO_KW
		   ) {
			
			switch(kw.type) {
			case ABSTRACT_KW: reader.skip(); isAbstract = true; break;
			case STATIC_KW: reader.skip(); isStatic = true; break;
			case FINAL_KW: reader.skip(); isFinal = true; break;
			case PROTO_KW: reader.skip(); isProto = true; break;
			case EXTERN_KW: externName = ExternParser.parse(sReader, reader); break;
			default:
			}
			
			kw = reader.peek();
		}
		
		if(reader.peek().type == TokenType.FUNC_KW) {
			reader.skip();
		} else if(!skipFunc) {
			reader.reset(mark);
			return null;
		}
		
		String suffix = "";
		List<TypeParam> typeParams = null;
		while(true) {
			Token tok = reader.peek();
			if(tok.type == TokenType.TILDE) {
				reader.skip();
				Token tSuffix = reader.peek();
				if(tSuffix.isNameToken()) {
					reader.skip();
					suffix = tSuffix.get(sReader);
				}
			} else if(tok.type == TokenType.LESSTHAN) {
				reader.skip();
				typeParams = new ArrayList<TypeParam>();
				while(reader.peek().type != TokenType.GREATERTHAN) {
					Token nameTok = reader.read();
					typeParams.add(new TypeParam(nameTok.get(sReader), nameTok));
					if(reader.peek().type != TokenType.COMMA) break;
					reader.skip();
				}
				reader.skip();
			} else break;
		}
		
		FunctionDecl functionDecl = new FunctionDecl(
				name, suffix, isFinal, isStatic, isAbstract, externName, startToken);
		functionDecl.setProto(isProto);
		if(typeParams != null) functionDecl.getTypeParams().addAll(typeParams);
		if(comment != null) functionDecl.setComment(comment);
		
		ArgumentListFiller.fill(sReader, reader, functionDecl.isExtern(), functionDecl.getArguments());
		
		if(reader.peek().type == TokenType.LINESEP) return functionDecl;
		
		Token token = reader.read();
		if(token.type == TokenType.ARROW) {
			Type returnType = TypeParser.parse(sReader, reader);
			if(returnType == null) {
				throw new CompilationFailedError(sReader.getLocation(reader.peek()),
						"Expected return type after arrow");
			}
			functionDecl.setReturnType(returnType);
			token = reader.read();
		}
		
		if(token.type == TokenType.LINESEP) return functionDecl; // empty func is actually legal

		if(token.type != TokenType.OPEN_BRACK) {
			reader.rewind();
			Line line = LineParser.parse(sReader, reader);
			if(line == null) {
				throw new CompilationFailedError(sReader.getLocation(reader.prev()),
						"Expected opening brace after function name.");
			}
			functionDecl.getBody().add(line);
			return functionDecl;
		}
	
		while(reader.hasNext() && reader.peek().type != TokenType.CLOS_BRACK) {
			reader.skipWhitespace();
		
			Line line = LineParser.parse(sReader, reader);
			if(line == null && reader.hasNext() && reader.peek().type != TokenType.CLOS_BRACK) {
				throw new CompilationFailedError(sReader.getLocation(reader.peek()),
						"Expected statement in function body. Found "+reader.peek().type+" instead.");
			}
			if(line != null) functionDecl.getBody().add(line);
		}
		reader.skip();
		
		return functionDecl;
	}
	
}
