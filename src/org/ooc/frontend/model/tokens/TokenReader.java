package org.ooc.frontend.model.tokens;

import java.util.List;

import org.ooc.frontend.model.tokens.Token.TokenType;

public class TokenReader extends ListReader<Token> {

	public TokenReader(List<Token> list) {
		super(list);
	}

	public void skipNonWhite() {
		while(list.get(index).type == TokenType.LINESEP) {
			index++;
		}
	}
	
	public Token peekNonWhite() {
		int index2 = index;
		while(list.get(index2).type == TokenType.LINESEP) {
			index2++;
		}
		return list.get(index2);
	}
	
	public Token readNonWhite() {
		skipNonWhite();
		return list.get(index++);
	}
	
}
