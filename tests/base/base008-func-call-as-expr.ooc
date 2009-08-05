include stdlib, stdio, time;
cover Int from int;
cover String from char*;
cover TimeT from time_t;
extern func printf(String, ...);
extern func srand(Int);
extern func rand -> Int;
extern func time(TimeT);

func main {

	srand(time(null));
	printf("The answer is %d\n", rand());

}
