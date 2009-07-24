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
		STATIC_KW, // static keyword
		
		INCLUDE_KW, // include keyword
		IMPORT_KW, // import keyword
		EXTERN_KW, // extern keyword
		
		BREAK_KW, // break keyword
		CONTINUE_KW, // continue keyword
		FALLTHR_KW, // fallthrough keyword
		
		IMPL_KW, // implement keyword
		OVER_KW, // override keyword
		
		IF_KW,
		ELSE_KW,
		FOR_KW,
		WHILE_KW,
		DO_KW,
		SWITCH_KW,
		CASE_KW,
		
		VERSION_KW, // version keyword
		
		CTYPE_KW,
		TYPEDEF_KW,
		
		RETURN_KW,
		
		TRUE,
		FALSE,
		NULL,
		
		SL_COMMENT, // single-line comment
		ML_COMMENT, // multi-line comment
		OOCDOC, // oodoc comment
		
		REFERENCE, // @functionName
		NAME, // mostly a Java identifier
		
		COMMA, // ,
		DOT, // .
		DOUBLE_DOT, // ..
		TRIPLE_DOT, // ...
		ARROW, // ->
		COL, // :
		SEMICOL, // ;
		
		PLUS, // +
		PLUS_EQ, // +=
		MINUS, // -
		MINUS_EQ, // -=
		STAR, // *
		STAR_EQ, // *=
		SLASH, // /
		SLASH_EQ, // /=
		
		PERCENT, // %
		EXCL, // !
		NOT_EQ, // !=
		QUEST, // ?
		
		ANTISLASH, // \
		
		GT, // >
		LT, // <
		GTE, // >=
		LTE, // <=
		ASSIGN, // =
		EQUALS, // ==
		
		L_AND, // && (logical and)
		L_OR, // || (logical or)
		
		B_AND, // & (binary and)
		B_OR, // | (binary or)
		
		CHAR_LIT, // 'c'
		STRING_LIT, // "blah\n"
		
		DEC_NUMBER, // 234
		HEX_NUMBER, // 0xdeadbeef007
		OCT_NUMBER, // 0c777
		BIN_NUMBER, // 0b1011
		
		OPEN_PAREN, // (
		CLOS_PAREN, // )
		
		OPEN_BRACK, // {
		CLOS_BRACK, // }
		
		OPEN_SQUAR, // [
		CLOS_SQUAR, // ]
	
	}
	
	public final int start;
	public final int length;
	public final TokenType type;
	
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
