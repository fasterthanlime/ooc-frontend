package org.ooc.frontend.parser;

import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.ooc.frontend.model.Argument;
import org.ooc.frontend.model.MemberArgument;
import org.ooc.frontend.model.MemberAssignArgument;
import org.ooc.frontend.model.NodeList;
import org.ooc.frontend.model.RegularArgument;
import org.ooc.frontend.model.Type;
import org.ooc.frontend.model.VarArg;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.frontend.model.tokens.TokenReader;
import org.ooc.frontend.model.tokens.Token.TokenType;
import org.ubi.CompilationFailedError;
import org.ubi.SourceReader;

public class ArgumentParser {

	public static boolean fill(SourceReader sReader, TokenReader reader, boolean isExtern, NodeList<Argument> args) throws IOException {
		
		int mark = reader.mark();
		
		if(reader.peek().type == TokenType.TRIPLE_DOT) {
			reader.skip();
			args.add(new VarArg());
			return true;
		}
		
		Token token = reader.read();
		if(tryRegular(sReader, reader, args, mark, token)) return true;
		if(tryAssign(sReader, reader, args, token)) return true;
		if(tryMember(sReader, reader, args, token)) return true;
		
		if(isExtern) {
			Type type = TypeParser.parse(sReader, reader);
			if(type == null) {
				throw new CompilationFailedError(sReader.getLocation(reader.peek().start),
					"Expected argument type in extern func definition ':'");
			}
			args.add(new TypeArgument(type));
			return true;
		}
				
		reader.reset();
		return false;
		
	}

	protected static boolean tryMember(SourceReader sReader, TokenReader reader,
			NodeList<Argument> args, Token token)
			throws CompilationFailedError, EOFException {
		
		if(token.type != TokenType.DOT) return false;
		
		Token t2 = reader.read();
		if(t2.type != TokenType.NAME) {
			throw new CompilationFailedError(sReader.getLocation(t2.start),
					"Expecting member variable name in member-assign-argument");
		}
		args.add(new MemberArgument(t2.get(sReader)));
		return true;
	}

	protected static boolean tryAssign(SourceReader sReader, TokenReader reader,
			NodeList<Argument> args, Token token)
			throws CompilationFailedError, EOFException {
		
		if(token.type != TokenType.ASSIGN) return false;
		Token t2 = reader.read();
		if(t2.type != TokenType.NAME) {
			throw new CompilationFailedError(sReader.getLocation(t2.start),
					"Expecting member variable name in member-assign-argument");
		}
		args.add(new MemberAssignArgument(t2.get(sReader)));
		return true;
	}

	protected static boolean tryRegular(SourceReader sReader, TokenReader reader,
			NodeList<Argument> args, int mark, Token t)
			throws CompilationFailedError, EOFException {
		
		if(t.type != TokenType.NAME) return false;
		
		List<String> names = new ArrayList<String>();
		names.add(t.get(sReader));
		while(reader.peek().type == TokenType.COMMA) {
			reader.skip();
			if(reader.peek().type != TokenType.NAME) {
				reader.reset(mark);
				return false;
			}
			names.add(reader.read().get(sReader));
		}
		
		if(reader.peek().type == TokenType.COLON) {
			reader.skip();
			Type type = TypeParser.parse(sReader, reader);
			if(type == null) {
				throw new CompilationFailedError(sReader.getLocation(reader.peek().start),
						"Expected argument type after its name and ':'");
			}
			for(String name: names) {
				args.add(new RegularArgument(type, name));
			}
			return true;
		}
		reader.reset(mark);
		return false;
		
	}
	
}
