include stdio;
extern func printf(String, ...);
cover String from char*;
cover Int from int;

func main {

	Int i;
	i = 42;
	printf("The answer is %d\n", i);

}
