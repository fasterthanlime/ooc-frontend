+: func (left, right: String) {
	String copy = GC_malloc(length + rvalue.length + 1)
	memcpy(copy, this, length)
	memcpy(copy as Pointer + length, rvalue, rvalue.length + 1) // copy the final '\0'
	return copy
}

Int: cover from int {

	func repr -> String {
		str = GC_malloc(128) : String
		sprintf(str, "%d", this)
		return str
	}

}

func main {

	printf ("The answer is " + 42.repr)

}
