include stdio;
cover String from char*;
cover Int from int;
extern func printf(String, ...);

func main {
	call();
}

func call {
	call(42);
}

func call(Int value) {
	printf("The answer is %d\n", value);
}
