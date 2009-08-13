package org.ooc.frontend.model.tokens;

import java.util.List;

import org.ooc.frontend.model.tokens.Token.TokenType;

public class TokenReader extends ListReader<Token> {

	public TokenReader(List<Token> list) {
		super(list);
	}

	public void skipNonWhitespace() {
		while(list.get(index).type == TokenType.LINESEP) {
			index++;
		}
	}
	
	public Token peekWhiteless() {
		int index2 = index;
		while(list.get(index2).type == TokenType.LINESEP) {
			index2++;
		}
		return list.get(index2);
	}
	
	public Token readWhiteless() {
		skipNonWhitespace();
		return list.get(index++);
	}
	
}
