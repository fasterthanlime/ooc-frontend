package org.ooc.frontend.model.tokens;

import org.ubi.SourceReader;


public class Token {
	
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
		EXTERN_KW, // extern keyword
		
		BREAK_KW, // break keyword
		CONTINUE_KW, // continue keyword
		FALLTHR_KW, // fallthrough keyword
		
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
		
		SL_COMMENT, // single-line comment
		ML_COMMENT, // multi-line comment
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
		
		LOGICAL_AND, // && (logical and)
		LOGICAL_OR, // || (logical or)
		
		AMPERSAND, // & (binary and)
		BINARY_OR, // | (binary or)
		
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
	
}
