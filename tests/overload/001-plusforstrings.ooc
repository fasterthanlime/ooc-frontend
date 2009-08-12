include stdio;

cover Char from char;
cover String from Char* {

	func + (This rvalue) -> This {
		String copy = GC_malloc(length + rvalue.length + 1)
		memcpy(copy, this, length)
		memcpy(copy as Pointer + length, rvalue, rvalue.length + 1) // copy the final '\0'
		return copy
	}

}

cover Int from int {

	func repr -> String {

	}

}

func main {

	printf ("The answer is " + 42.repr)

}
