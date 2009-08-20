package org.ooc.frontend.model;

import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ooc.frontend.model.tokens.Token;
import org.ooc.frontend.model.tokens.Token.TokenType;
import org.ubi.CompilationFailedError;
import org.ubi.FileLocation;
import org.ubi.SourceReader;
import org.ubi.SyntaxError;

public class Tokenizer {

	private static class Name {
		String name;
		TokenType tokenType;
		
		public Name(String name, TokenType tokenType) {
			this.name = name;
			this.tokenType = tokenType;
		}
	}
	
	private static Name[] names = new Name[] {
		new Name("class", TokenType.CLASS_KW),
		new Name("cover", TokenType.COVER_KW),
		new Name("func", TokenType.FUNC_KW),
		new Name("abstract", TokenType.ABSTRACT_KW),
		new Name("from", TokenType.FROM_KW),
		new Name("this", TokenType.THIS_KW),
		new Name("super", TokenType.SUPER_KW),
		new Name("new", TokenType.NEW_KW),
		new Name("const", TokenType.CONST_KW),
		new Name("final", TokenType.FINAL_KW),
		new Name("static", TokenType.STATIC_KW),
		new Name("include", TokenType.INCLUDE_KW),
		new Name("import", TokenType.IMPORT_KW),
		new Name("use", TokenType.USE_KW),
		new Name("break", TokenType.BREAK_KW),
		new Name("continue", TokenType.CONTINUE_KW),
		new Name("fallthrough", TokenType.FALLTHR_KW),
		new Name("if", TokenType.IF_KW),
		new Name("else", TokenType.ELSE_KW),
		new Name("for", TokenType.FOR_KW),
		new Name("while", TokenType.WHILE_KW),
		new Name("do", TokenType.DO_KW),
		new Name("switch", TokenType.SWITCH_KW),
		new Name("return", TokenType.RETURN_KW),
		new Name("as", TokenType.AS_KW),
		new Name("in", TokenType.IN_KW),
		new Name("version", TokenType.VERSION_KW),
		new Name("true", TokenType.TRUE),
		new Name("false", TokenType.FALSE),
		new Name("null", TokenType.NULL),
		new Name("extern", TokenType.EXTERN_KW),		
		new Name("operator", TokenType.OPERATOR_KW),
		//TODO I'm not sure if those three should be keywords.
		//They are remains from C and can be parsed as NAMEs
		new Name("unsigned", TokenType.UNSIGNED),
		new Name("signed", TokenType.SIGNED),
		new Name("long", TokenType.LONG),
		new Name("union", TokenType.UNION),
		new Name("struct", TokenType.STRUCT),
	};
	
	private static class CharTuple {
		private char first;
		private TokenType firstType;
		
		private char second;
		private TokenType secondType;
		
		private char third;
		private TokenType thirdType;
		
		public CharTuple(char first, TokenType firstType) {
			this(first, firstType, '\0', null);
		}
		
		public CharTuple(char first, TokenType firstType, char second, TokenType secondType) {
			this(first, firstType, second, secondType, '\0', null);
		}
		
		public CharTuple(char first, TokenType firstType, char second, TokenType secondType,
				char third, TokenType thirdType) {
			this.first = first;
			this.firstType = firstType;
			this.second = second;
			this.secondType = secondType;
			this.third = third;
			this.thirdType = thirdType;
		}

		public Token handle(FileLocation location, char c, SourceReader reader) throws EOFException {
			if(c != first) return null;
			reader.read();
			if(second == '\0' || second != reader.peek()) {
				return new Token(location.getIndex(), 1, firstType);
			}
			reader.read();
			if(third == '\0' || third != reader.peek()) {
				return new Token(location.getIndex(), 2, secondType);
			}
			reader.read();
			return new Token(location.getIndex(), 3, thirdType);
		}
	}
	
	private CharTuple[] chars = new CharTuple[] {
		new CharTuple('{', TokenType.OPEN_BRACK),
		new CharTuple('}', TokenType.CLOS_BRACK),
		new CharTuple('(', TokenType.OPEN_PAREN),
		new CharTuple(')', TokenType.CLOS_PAREN),
		new CharTuple('[', TokenType.OPEN_SQUAR),
		new CharTuple(']', TokenType.CLOS_SQUAR),
		new CharTuple('.', TokenType.DOT, '.', TokenType.DOUBLE_DOT, '.', TokenType.TRIPLE_DOT),
		new CharTuple(',', TokenType.COMMA),
		new CharTuple('=', TokenType.ASSIGN, '=', TokenType.EQUALS),
		new CharTuple('%', TokenType.PERCENT),
		new CharTuple('~', TokenType.TILDE),
		new CharTuple(':', TokenType.COLON, '=', TokenType.DECL_ASSIGN),
		new CharTuple('!', TokenType.BANG, '=', TokenType.NOT_EQUALS),
		new CharTuple('&', TokenType.AMPERSAND, '&', TokenType.LOGICAL_AND),
		new CharTuple('|', TokenType.BINARY_OR, '|', TokenType.LOGICAL_OR),
		new CharTuple('?', TokenType.QUEST),
		new CharTuple('#', TokenType.HASH),
		new CharTuple('@', TokenType.AT),
		new CharTuple('+', TokenType.PLUS, '=', TokenType.PLUS_ASSIGN),
		new CharTuple('*', TokenType.STAR, '=', TokenType.STAR_ASSIGN),
		new CharTuple('<', TokenType.LESSTHAN, '=', TokenType.LESSTHAN_EQUALS),
		new CharTuple('>', TokenType.GREATERTHAN, '=', TokenType.GREATERTHAN_EQUALS),
	};
	
	public List<Token> parse(SourceReader reader) throws IOException {
		
		List<Token> tokens = new ArrayList<Token>();
		
		reading: while(reader.hasNext()) {
			
			reader.skipChars("\t ");
			if(!reader.hasNext()) {
				break;
			}
			
			FileLocation location = reader.getLocation();
			
			char c = reader.peek();
			if(c == ';' || c == '\n') {
				reader.read();
				while(reader.peek() == '\n' && reader.hasNext()) {
					reader.read();
				}
				tokens.add(new Token(location.getIndex(), 1, TokenType.LINESEP));
				continue;
			}
			
			if(c == '\\') {
				reader.read();
				char c2 = reader.peek();
				if(c2 == '\\') {
					reader.read();
					tokens.add(new Token(location.getIndex(), 2, TokenType.DOUBLE_BACKSLASH));
				} else if(c2 == '\n') {
					reader.read(); // Just skip both of'em (line continuation)
				} else {
					tokens.add(new Token(location.getIndex(), 1, TokenType.BACKSLASH));
				}
				continue;
			}
			
			for(CharTuple candidate: chars) {
				Token token = candidate.handle(location, c, reader);
				if(token != null) {
					tokens.add(token);
					continue reading;
				}
			}

			
			if(c == '"') {
				reader.read();
				// TODO: optimize. readStringLiteral actually stores it into a String, but we don't care
				try {
					reader.readStringLiteral();
				} catch(EOFException eof) {
					throw new CompilationFailedError(location, "Never-ending string literal (reached end of file)");
				}
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
			
			if(c == '/') {
				reader.read();
				char c2 = reader.peek();
				if(c2 == '=') {
					reader.read();
					tokens.add(new Token(location.getIndex(), 2, TokenType.SLASH_ASSIGN));
				} else if(c2 == '/') {
					reader.readLine();
					tokens.add(new Token(location.getIndex() + 2, reader.mark()
							- location.getIndex() - 2, TokenType.SL_COMMENT));
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
			
			if(c == '-') {
				reader.read();
				char c2 = reader.peek();
				if(c2 == '>') {
					reader.read();
					tokens.add(new Token(location.getIndex(), 2, TokenType.ARROW));
				} else if(c2 == '=') {
					reader.read();
					tokens.add(new Token(location.getIndex(), 2, TokenType.MINUS_ASSIGN));
				} else {
					tokens.add(new Token(location.getIndex(), 1, TokenType.MINUS));
				}
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
					tokens.add(new Token(location.getIndex() + 2, reader.mark()
							- location.getIndex() - 2, TokenType.HEX_INT));
					continue;
				} else if(c2 == 'c') {
					reader.read();
					String lit = reader.readMany("01234567", "_", true);
					if(lit.isEmpty()) {
						throw new CompilationFailedError(location, "Empty octal number literal");
					}
					tokens.add(new Token(location.getIndex() + 2, reader.mark()
							- location.getIndex() - 2, TokenType.OCT_INT));
					continue;
				} else if(c2 == 'b') {
					reader.read();
					String lit = reader.readMany("01", "_", true);
					if(lit.isEmpty()) {
						throw new CompilationFailedError(location, "Empty binary number literal");
					}
					tokens.add(new Token(location.getIndex() + 2, reader.mark()
							- location.getIndex() - 2, TokenType.BIN_INT));
					continue;
				}
			}
			
			if(Character.isDigit(c)) {
				reader.readMany("0123456789", "_", true);
				if(reader.peek() == '.') {
					reader.read();
					if(reader.peek() != '.') {
						reader.readMany("0123456789", "_", true);
						tokens.add(new Token(location.getIndex(), reader.mark() - location.getIndex(),
								TokenType.DEC_FLOAT));
						continue;
					}
					reader.rewind(1);
				}
				tokens.add(new Token(location.getIndex(), reader.mark() - location.getIndex(),
					TokenType.DEC_INT));
				continue;
			}
			
			String name = reader.readName();
			if(!name.isEmpty()) {
				for(Name candidate: names) {
					if(candidate.name.equals(name)) {
						tokens.add(new Token(location.getIndex(), name.length(), candidate.tokenType));
						continue reading;
					}
				}
				tokens.add(new Token(location.getIndex(), name.length(), TokenType.NAME));
				continue reading;
			}
			
			throw new CompilationFailedError(location, "Unexpected input. Token list is: "+tokens);
			
		}
		
		tokens.add(new Token(reader.mark(), 0, TokenType.LINESEP));
		
		return tokens;
	}
	
	public static void main(String[] args) throws IOException {
		
		if(args.length < 1) {
			System.out.println("Usage: ooft file.ooc");
			System.exit(0);
		}
		
		SourceReader reader = SourceReader.getReaderFromPath(args[0]);
		List<Token> tokens = new Tokenizer().parse(reader);
		Iterator<Token> iter = tokens.iterator();
		while(iter.hasNext()) {
			Token t = iter.next();
			System.out.printf("%s \t[%s]\n", reader.getSlice(t.start, t.length), t.type);
		}
		
	}
	
}
