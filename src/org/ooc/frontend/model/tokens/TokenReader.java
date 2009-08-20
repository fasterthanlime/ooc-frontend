package org.ooc.frontend.model.tokens;

import java.util.List;

import org.ooc.frontend.model.tokens.Token.TokenType;

public class TokenReader extends ListReader<Token> {

	public TokenReader(List<Token> list) {
		super(list);
	}

	public boolean skipWhitespace() {
		boolean result = false;
		while(list.get(index).type == TokenType.LINESEP) {
			index++;
			result = true;
		}
		return result;
	}
	
	public boolean skipWorthless() {
		boolean result = false;
		while(list.get(index).type == TokenType.LINESEP
				|| list.get(index).type == TokenType.SL_COMMENT
				|| list.get(index).type == TokenType.ML_COMMENT) {
			index++;
			result = true;
		}
		return result;
	}
	
	public Token peekWhiteless() {
		int index2 = index;
		while(list.get(index2).type == TokenType.LINESEP) {
			index2++;
		}
		return list.get(index2);
	}
	
	public Token readWhiteless() {
		skipWhitespace();
		return list.get(index++);
	}
	
}
