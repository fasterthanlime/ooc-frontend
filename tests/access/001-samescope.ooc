include stdio;

cover Int from int;
cover String from char*;

extern func printf(String, ...);

func main {

	Int i = 42;
	printf("The answer is %d\n", i);

}
