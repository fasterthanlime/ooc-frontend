include stdio;
extern func printf(String, ...);
cover String from char*;
cover Int from int;

func main {

	printf("The answer is %d\n", Int i = 42);

}
