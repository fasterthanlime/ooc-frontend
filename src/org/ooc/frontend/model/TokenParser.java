package org.ooc.frontend.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ooc.errors.CompilationFailedError;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.frontend.model.tokens.Token.TokenType;
import org.ubi.FileLocation;
import org.ubi.SourceReader;
import org.ubi.SyntaxError;

public class TokenParser {

	public List<Token> parse(SourceReader reader) throws IOException {
		
		List<Token> tokens = new ArrayList<Token>();
		
		while(reader.hasNext()) {
			
			reader.skipWhitespace();
			if(!reader.hasNext()) {
				break;
			}
			
			//int mark = reader.mark();
			FileLocation location = reader.getLocation();
			
			char c = reader.peek();
			
			if(c == '{') {
				reader.read();
				tokens.add(new Token(location.getIndex(), 1, TokenType.OPEN_BRACK));
				continue;
			}
			
			if(c == '(') {
				reader.read();
				tokens.add(new Token(location.getIndex(), 1, TokenType.OPEN_PAREN));
				continue;
			}
			
			if(c == '[') {
				reader.read();
				tokens.add(new Token(location.getIndex(), 1, TokenType.OPEN_SQUAR));
				continue;
			}
			
			if(c == '}') {
				reader.read();
				tokens.add(new Token(location.getIndex(), 1, TokenType.CLOS_BRACK));
				continue;
			}
			
			if(c == ')') {
				reader.read();
				tokens.add(new Token(location.getIndex(), 1, TokenType.CLOS_PAREN));
				continue;
			}
			
			if(c == ']') {
				reader.read();
				tokens.add(new Token(location.getIndex(), 1, TokenType.CLOS_SQUAR));
				continue;
			}
			
			if(c == '.') {
				reader.read();
				char c2 = reader.peek();
				if(c2 == '.') {
					reader.read();
					tokens.add(new Token(location.getIndex(), 2, TokenType.DOUBLE_DOT));
				} else {
					tokens.add(new Token(location.getIndex(), 1, TokenType.DOT));
				}
				continue;
			}
			
			if(c == ',') {
				reader.read();
				tokens.add(new Token(location.getIndex(), 1, TokenType.COMMA));
				continue;
			}
			
			if(c == '=') {
				reader.read();
				char c2 = reader.peek();
				if(c2 == '=') {
					reader.read();
					tokens.add(new Token(location.getIndex(), 2, TokenType.EQUALS));
				} else {
					tokens.add(new Token(location.getIndex(), 1, TokenType.ASSIGN));
				}
				continue;
			}
			
			if(c == ';') {
				reader.read();
				tokens.add(new Token(location.getIndex(), 1, TokenType.SEMICOL));
				continue;
			}
			
			if(c == '%') {
				reader.read();
				tokens.add(new Token(location.getIndex(), 1, TokenType.PERCENT));
				continue;
			}
			
			if(c == ':') {
				reader.read();
				tokens.add(new Token(location.getIndex(), 1, TokenType.COL));
				continue;
			}
			
			if(c == '!') {
				reader.read();
				char c2 = reader.peek();
				if(c2 == '=') {
					reader.read();
					tokens.add(new Token(location.getIndex(), 2, TokenType.NOT_EQ));
				} else {
					tokens.add(new Token(location.getIndex(), 1, TokenType.EXCL));
				}
				continue;
			}
			
			if(c == '&') {
				reader.read();
				char c2 = reader.peek();
				if(c2 == '&') {
					reader.read();
					tokens.add(new Token(location.getIndex(), 2, TokenType.L_AND));
				} else {
					tokens.add(new Token(location.getIndex(), 1, TokenType.B_AND));
				}
				continue;
			}
			
			if(c == '|') {
				reader.read();
				char c2 = reader.peek();
				if(c2 == '|') {
					reader.read();
					tokens.add(new Token(location.getIndex(), 2, TokenType.L_OR));
				} else {
					tokens.add(new Token(location.getIndex(), 1, TokenType.B_OR));
				}
				continue;
			}
			
			if(c == '?') {
				reader.read();
				tokens.add(new Token(location.getIndex(), 1, TokenType.QUEST));
				continue;
			}
			
			
			if(c == '"') {
				reader.read();
				// TODO: optimize. readStringLiteral actually stores it into a String, but we don't care
				reader.readStringLiteral();
				tokens.add(new Token(location.getIndex() + 1,
						reader.mark() - location.getIndex() - 2,
						TokenType.STRING_LIT));
				continue;
			}
			
			if(c == '\'') {
				reader.read();
				try {
					reader.readCharLiteral();
					tokens.add(new Token(location.getIndex() + 1, 
							reader.mark() - location.getIndex() - 2,
							TokenType.CHAR_LIT));
					continue;
				} catch(SyntaxError e) {
					throw new CompilationFailedError(location, e.getMessage());
				}
			}
			
			if(c == '+') {
				reader.read();
				char c2 = reader.peek();
				if(c2 == '=') {
					reader.read();
					tokens.add(new Token(location.getIndex(), 2, TokenType.PLUS_EQ));
				} else {
					tokens.add(new Token(location.getIndex(), 1, TokenType.PLUS));
				}
				continue;
			}
			
			if(c == '-') {
				reader.read();
				char c2 = reader.peek();
				if(c2 == '=') {
					reader.read();
					tokens.add(new Token(location.getIndex(), 2, TokenType.MINUS_EQ));
				} else if(c2 == '>') {
					reader.read();
					tokens.add(new Token(location.getIndex(), 2, TokenType.ARROW));
				} else {
					tokens.add(new Token(location.getIndex(), 1, TokenType.MINUS));
				}
				continue;
			}
			
			if(c == '*') {
				reader.read();
				char c2 = reader.peek();
				if(c2 == '=') {
					reader.read();
					tokens.add(new Token(location.getIndex(), 2, TokenType.STAR_EQ));
				} else {
					tokens.add(new Token(location.getIndex(), 1, TokenType.STAR));
				}
				continue;
			}
			
			if(c == '/') {
				reader.read();
				char c2 = reader.peek();
				if(c2 == '=') {
					reader.read();
					tokens.add(new Token(location.getIndex(), 2, TokenType.SLASH_EQ));
				} else if(c2 == '/') {
					reader.readLine();
					tokens.add(new Token(location.getIndex(), reader.mark() - location.getIndex(), TokenType.SL_COMMENT));
				} else if(c2 == '*') {
					reader.read();
					char c3 = reader.peek();
					TokenType type = TokenType.ML_COMMENT;
					if(c3 == '*') {
						reader.read();
						type = TokenType.OOCDOC;
					}
					reader.readUntil(new String[] {"*/"}, true);
					tokens.add(new Token(location.getIndex(), reader.mark() - location.getIndex(), type));
				} else {
					tokens.add(new Token(location.getIndex(), 1, TokenType.SLASH));
				}
				continue;
			}
			
			if(c == '<') {
				reader.read();
				char c2 = reader.peek();
				if(c2 == '=') {
					reader.read();
					tokens.add(new Token(location.getIndex(), 2, TokenType.LTE));
				} else {
					tokens.add(new Token(location.getIndex(), 1, TokenType.LT));
				}
				continue;
			}
			
			if(c == '>') {
				reader.read();
				char c2 = reader.peek();
				if(c2 == '=') {
					reader.read();
					tokens.add(new Token(location.getIndex(), 2, TokenType.GT));
				} else {
					tokens.add(new Token(location.getIndex(), 1, TokenType.GTE));
				}
				continue;
			}
			
			if(c == '@') {
				reader.read();
				if(reader.readName().isEmpty()) {
					throw new CompilationFailedError(location, "Empty reference name, should be @functionName");
				}
				tokens.add(new Token(location.getIndex(), reader.mark() - location.getIndex(), TokenType.REFERENCE));
				continue;
			}
			
			if(c == '0') {
				reader.read();
				char c2 = reader.peek();
				if(c2 == 'x') {
					reader.read();
					String lit = reader.readMany("0123456789abcdefABCDEF", "_", true);
					if(lit.isEmpty()) {
						throw new CompilationFailedError(location, "Empty hexadecimal number literal");
					}
					tokens.add(new Token(location.getIndex() + 2, reader.mark() - location.getIndex() - 2, TokenType.HEX_NUMBER));
					continue;
				} else if(c2 == 'c') {
					reader.read();
					String lit = reader.readMany("01234567", "_", true);
					if(lit.isEmpty()) {
						throw new CompilationFailedError(location, "Empty octal number literal");
					}
					tokens.add(new Token(location.getIndex() + 2, reader.mark() - location.getIndex() - 2, TokenType.OCT_NUMBER));
					continue;
				} else if(c2 == 'b') {
					reader.read();
					String lit = reader.readMany("01", "_", true);
					if(lit.isEmpty()) {
						throw new CompilationFailedError(location, "Empty binary number literal");
					}
					tokens.add(new Token(location.getIndex() + 2, reader.mark() - location.getIndex() - 2, TokenType.BIN_NUMBER));
					continue;
				}
			}
			
			if(Character.isDigit(c)) {
				reader.readMany("0123456789", "_", true);
				tokens.add(new Token(location.getIndex(), reader.mark() - location.getIndex(),
						TokenType.DEC_NUMBER));
				continue;
			}
			
			String name = reader.readName();
			if(!name.isEmpty()) {
				
				if(name.equals("class")) {
					tokens.add(new Token(location.getIndex(), 5, TokenType.CLASS_KW));
				} else if(name.equals("cover")) {
					tokens.add(new Token(location.getIndex(), 5, TokenType.COVER_KW));
				} else if(name.equals("func")) {
					tokens.add(new Token(location.getIndex(), 4, TokenType.FUNC_KW));
				} else if(name.equals("abstract")) {
					tokens.add(new Token(location.getIndex(), 8, TokenType.ABSTRACT_KW));
				} else if(name.equals("from")) {
					tokens.add(new Token(location.getIndex(), 4, TokenType.FROM_KW));
				} else if(name.equals("this")) {
					tokens.add(new Token(location.getIndex(), 4, TokenType.THIS_KW));
				} else if(name.equals("super")) {
					tokens.add(new Token(location.getIndex(), 5, TokenType.SUPER_KW));
				} else if(name.equals("new")) {
					tokens.add(new Token(location.getIndex(), 3, TokenType.NEW_KW));
				} else if(name.equals("const")) {
					tokens.add(new Token(location.getIndex(), 5, TokenType.CONST_KW));
				} else if(name.equals("static")) {
					tokens.add(new Token(location.getIndex(), 6, TokenType.STATIC_KW));
				} else if(name.equals("include")) {
					tokens.add(new Token(location.getIndex(), 7, TokenType.INCLUDE_KW));
				} else if(name.equals("import")) {
					tokens.add(new Token(location.getIndex(), 6, TokenType.IMPORT_KW));
				} else if(name.equals("break")) {
					tokens.add(new Token(location.getIndex(), 5, TokenType.BREAK_KW));
				} else if(name.equals("continue")) {
					tokens.add(new Token(location.getIndex(), 8, TokenType.CONTINUE_KW));
				} else if(name.equals("fallthrough")) {
					tokens.add(new Token(location.getIndex(), 11, TokenType.FALLTHR_KW));
				} else if(name.equals("implement")) {
					tokens.add(new Token(location.getIndex(), 9, TokenType.IMPL_KW));
				} else if(name.equals("override")) {
					tokens.add(new Token(location.getIndex(), 8, TokenType.OVER_KW));
				} else if(name.equals("if")) {
					tokens.add(new Token(location.getIndex(), 2, TokenType.IF_KW));
				} else if(name.equals("else")) {
					tokens.add(new Token(location.getIndex(), 4, TokenType.ELSE_KW));
				} else if(name.equals("for")) {
					tokens.add(new Token(location.getIndex(), 3, TokenType.FOR_KW));
				} else if(name.equals("while")) {
					tokens.add(new Token(location.getIndex(), 5, TokenType.WHILE_KW));
				} else if(name.equals("do")) {
					tokens.add(new Token(location.getIndex(), 2, TokenType.DO_KW));
				} else if(name.equals("switch")) {
					tokens.add(new Token(location.getIndex(), 6, TokenType.SWITCH_KW));
				} else if(name.equals("return")) {
					tokens.add(new Token(location.getIndex(), 6, TokenType.RETURN_KW));
				} else if(name.equals("version")) {
					tokens.add(new Token(location.getIndex(), 7, TokenType.VERSION_KW));
				} else if(name.equals("true")) {
					tokens.add(new Token(location.getIndex(), 4, TokenType.TRUE));
				} else if(name.equals("false")) {
					tokens.add(new Token(location.getIndex(), 5, TokenType.FALSE));
				} else if(name.equals("null")) {
					tokens.add(new Token(location.getIndex(), 4, TokenType.NULL));
				} else {
					tokens.add(new Token(location.getIndex(), name.length(), TokenType.NAME));
				}
				continue;
				
			}
			
			throw new CompilationFailedError(location, "Unexpected input. Token list is: "+tokens);
			
		}
		
		return tokens;
		
	}
	
	public static void main(String[] args) throws IOException {
		
		if(args.length < 1) {
			System.out.println("Usage: ooft file.ooc");
			System.exit(0);
		}
		
		SourceReader reader = SourceReader.getReaderFromPath(args[0]);
		List<Token> tokens = new TokenParser().parse(reader);
		Iterator<Token> iter = tokens.iterator();
		while(iter.hasNext()) {
			Token t = iter.next();
			System.out.printf("%s \t[%s]\n", reader.getSlice(t.start, t.length), t.type);
			if(iter.hasNext()) {
				//System.out.printf(", ");
			}
		}
		
	}
	
}
