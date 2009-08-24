include stdlib, string

strlen: extern func (String) -> SizeT
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
		for (i : Int in 0..other length()) {
			if (this[i] != other[i]) {
				return false
			}
		}
		return true
	}
	
	toInt: func -> Int atoi(this)
	
	toLong: func -> Long atol(this)
	
	toLLong: func -> LLong atol(this)
	
	/* TODO: toDouble */
	
	isEmpty: func -> Bool ((this == null) || (this[0] == 0))
	
	startsWith: func(s: String) -> Bool {
		if (this length() < s length()) return false
		for (i : Int in 0..s length()) {
			if(this[i] != s[i]) return false
		}
		return true
	}
	
	endsWith: func(s: String) -> Bool {
		l1 = this length() : Int
		l2 = s length() : Int
		if(l1 < l2) return false
		offset = (l1 - l2) : Int
		for (i: Int in 0..l1) {
			if(this[i + offset] != s[i]) {
				return false
			}
		}
		return true
	}
	
}
