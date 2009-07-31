include stdio;

cover Int from int;
cover String from char*;

extern func printf(String, ...);

func main {

	IntContainer cont = new;
	cont.value = 42;
	if true printf("The answer is %d\n", cont.value);

}

class IntContainer {

	Int value;

}
