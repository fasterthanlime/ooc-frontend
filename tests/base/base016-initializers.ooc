include stdio
extern func printf(String, ...)
cover Int from int
cover String from char*

class Foo {
	
	Int value = 99
	String msg
	
	func new(=msg) { // in ooc, you can omit the type if it's a member variable's name, and '=' means to automatically assign it.
		printf("msg = %s, value = %d\n", msg, this.value)
	}
	
}

func main {
	
	new Foo("Dilbert")
	
}
