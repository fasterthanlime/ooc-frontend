include stdlib

atoi: extern func (String) -> Int
atol: extern func (String) -> Long

String: cover from Char* {
	
	length: func -> Int strlen(this)
	
	equals: func(other: String) -> Bool {
		if ((this == null) || (other == null)) {
			return false
		}
		if (this length() != other length()) {
			return false
		}
		/* TODO: Actually compare the strings */
		return true
	}
	
	toInt: func -> Int return atoi(this)
	
	toLong: func -> Long return atol(this)
	
}
