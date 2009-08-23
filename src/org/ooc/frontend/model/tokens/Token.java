package org.ooc.frontend.model.tokens;

import org.ubi.Locatable;
import org.ubi.SourceReader;


public class Token implements Locatable {

	public static Token defaultToken = new Token(0, 0, null);
	
	public static enum TokenType {

		CLASS_KW, // class keyword
		COVER_KW, // cover keyword
		FUNC_KW, // func keyword
		ABSTRACT_KW, // abstract keyword
		FROM_KW, // from keyword
		THIS_KW, // this keyword
		SUPER_KW, // super keyword
		NEW_KW, // new keyword
		
		CONST_KW, // const keyword
		FINAL_KW, // final keyword
		STATIC_KW, // static keyword
		
		INCLUDE_KW, // include keyword
		IMPORT_KW, // import keyword
		USE_KW, // use keyword
		EXTERN_KW, // extern keyword
		PROTO_KW, // proto keyword
		
		BREAK_KW, // break keyword
		CONTINUE_KW, // continue keyword
		FALLTHR_KW, // fallthrough keyword
		
		OPERATOR_KW,
		
		IF_KW,
		ELSE_KW,
		FOR_KW,
		WHILE_KW,
		DO_KW,
		SWITCH_KW,
		CASE_KW,
		
		AS_KW,
		IN_KW,
		
		VERSION_KW, // version keyword
		
		RETURN_KW,
		
		TRUE,
		FALSE,
		NULL,
		
		OOCDOC, // oodoc comment
		
		REFERENCE, // @functionName
		NAME, // mostly a Java identifier

		BACKSLASH, // \
		DOUBLE_BACKSLASH, // \\
		AT, // @
		HASH, // #
		TILDE, // ~
		COMMA, // ,
		DOT, // .
		DOUBLE_DOT, // ..
		TRIPLE_DOT, // ...
		ARROW, // ->
		COLON, // :
		LINESEP, // ;
		
		PLUS, // +
		PLUS_ASSIGN, // +=
		MINUS, // -
		MINUS_ASSIGN, // -=
		STAR, // *
		STAR_ASSIGN, // *=
		SLASH, // /
		SLASH_ASSIGN, // /=
		
		PERCENT, // %
		BANG, // !
		NOT_EQUALS, // !=
		QUEST, // ?
		
		ANTISLASH, // \
		
		GREATERTHAN, // >
		LESSTHAN, // <
		GREATERTHAN_EQUALS, // >=
		LESSTHAN_EQUALS, // <=
		ASSIGN, // =
		DECL_ASSIGN, // :=
		EQUALS, // ==
		
		DOUBLE_AMPERSAND, // && (logical and)
		DOUBLE_PIPE, // || (et non pas double pipe..)
		
		AMPERSAND, // & (binary and)
		PIPE, // | (binary or)
		
		CHAR_LIT, // 'c'
		STRING_LIT, // "blah\n"
		
		DEC_INT, // 234
		HEX_INT, // 0xdeadbeef007
		OCT_INT, // 0c777
		BIN_INT, // 0b1011
		DEC_FLOAT, // 3.14
		
		OPEN_PAREN, // (
		CLOS_PAREN, // )
		
		OPEN_BRACK, // {
		CLOS_BRACK, // }
		
		OPEN_SQUAR, // [
		CLOS_SQUAR, // ]
		
		UNSIGNED,
		SIGNED,
		LONG,
		STRUCT,
		UNION,
	
	}
	
	public final int start;
	public final int length;
	public TokenType type;
	
	public Token(int start, int length, TokenType type) {
		super();
		this.start = start;
		this.length = length;
		this.type = type;
	}
	
	@Override
	public String toString() {
		return type.toString();
	}
	
	public String get(SourceReader sReader) {
		return sReader.getSlice(start, length);
	}

	@Override
	public int getLength() {
		return length;
	}

	@Override
	public int getStart() {
		return start;
	}
	
	public Token cloneEnclosing(Token end) {
		Token token = new Token(start, end.getEnd() - start, type);
		return token;
	}

	public int getEnd() {
		return start + length;
	}

	public boolean isNameToken() {
		return type == TokenType.NAME || type == TokenType.THIS_KW
			|| type == TokenType.SUPER_KW || type == TokenType.CLASS_KW;
	}
	
}
