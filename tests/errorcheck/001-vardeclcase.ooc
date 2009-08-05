include stdio;
cover String from char*;
extern func printf(String, ...);

func main {

	int i = 42; // error, int should be capitalized
	printf("The answer is %d\n", i);

}
