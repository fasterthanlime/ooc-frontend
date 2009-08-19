operator + func (left, right: String) -> String {
	copy := GC_malloc(left length() + right length() + 1) as String
	memcpy(copy, left, left length())
	memcpy(copy as Char* + left length(), right, right length() + 1) // copy the final '\0'
	copy
}

Int: cover from int {

	repr: func -> String {
		str = GC_malloc(64) : String
		sprintf(str, "%d", this)
		str
	}

}

main: func {

	println ("The answer is " + 42 repr())

}
