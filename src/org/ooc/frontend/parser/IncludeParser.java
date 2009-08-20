package org.ooc.frontend.parser;

import java.io.EOFException;
import java.util.ArrayList;
import java.util.List;

import org.ooc.frontend.model.Include;
import org.ooc.frontend.model.NodeList;
import org.ooc.frontend.model.Include.Define;
import org.ooc.frontend.model.Include.Mode;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.frontend.model.tokens.TokenReader;
import org.ooc.frontend.model.tokens.Token.TokenType;
import org.ubi.CompilationFailedError;
import org.ubi.SourceReader;

public class IncludeParser {

	public static boolean parse(SourceReader sReader, TokenReader reader, NodeList<Include> includes) throws EOFException, CompilationFailedError {

		if(reader.peek().type != TokenType.INCLUDE_KW) return false;
		reader.skip();
		
		StringBuilder sb = new StringBuilder();
		List<Define> defines = new ArrayList<Define>();
		
		while(true) {
		
			Token t = reader.read();
			if(t.type == TokenType.LINESEP) {
				addInclude(includes, sb.toString(), defines);
				break;
			}
			if(t.type == TokenType.COMMA) {
				addInclude(includes, sb.toString(), defines);
				sb.setLength(0);
				defines.clear();
			} else if(t.type == TokenType.NAME || t.type == TokenType.SLASH
					|| t.type == TokenType.DOT || t.type == TokenType.DOUBLE_DOT) {
				sb.append(t.get(sReader));
			} else if(t.type == TokenType.BINARY_OR) {
				readDefines(sReader, reader, defines);
			} else {
				throw new CompilationFailedError(sReader.getLocation(t.start),
						"Unexpected token "+t.type+" while reading an include");
			}
			
		}
		
		return true;
		
	}

	private static void readDefines(SourceReader sReader, TokenReader reader,
			List<Define> defines) throws CompilationFailedError {
		if(reader.read().type != TokenType.OPEN_PAREN) {
			throw new CompilationFailedError(null, "Expected opening parenthesis to begin include defines, but got "
					+reader.prev().type);
		}
		
		while(true){
			Define define = new Define();
			define.name = reader.read().get(sReader);
			if(reader.peek().type == TokenType.ASSIGN) {
				reader.skip();
				define.value = reader.read().get(sReader);
			}
			defines.add(define);
			
			if(reader.peek().type != TokenType.COMMA) {
				break;
			}
			reader.skip();
		}
		
		if(reader.read().type != TokenType.CLOS_PAREN) {
			throw new CompilationFailedError(null, "Expected closing parenthesis to end include defines but got "
					+reader.prev().type);
		}
	}

	private static void addInclude(NodeList<Include> includes, String contentParam, List<Define> defines) {
		String content = contentParam;
		Mode mode = Mode.PATHY;
		if(content.startsWith("./")) {
			content = content.substring(2);
			mode = Mode.LOCAL;
		}

		Include include = new Include(content, mode);
		include.getDefines().addAll(defines);
		includes.add(include);
	}
	
}
