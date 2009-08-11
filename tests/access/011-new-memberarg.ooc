include stdio

cover Int from int
cover String from char*

extern func printf(String, ...)

func main {

	new IntContainer(42)

}

class IntContainer {

	Int value

	func new(value) {
		this.value = value
		print
	}

	func print {
		printf("The answer is %d\n", value)
	}

}
