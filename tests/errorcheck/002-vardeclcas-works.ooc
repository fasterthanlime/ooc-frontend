include stdio;
cover String from char*;
cover Int from int;
extern func printf(String, ...);

func main {

	Int i = 42;
	printf("The answer is %d\n", i);

}
