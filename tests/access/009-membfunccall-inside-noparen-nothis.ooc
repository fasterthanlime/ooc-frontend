include stdio;

cover Int from int;
cover String from char*;

extern func printf(String, ...);

func main {

	new IntContainer;

}

class IntContainer {

	Int value;

	func new {
		value = 42;
		print;
	}

	func print {
		printf("The answer is %d\n", value);
	}

}
