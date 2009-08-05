include stdlib, stdio, time;
cover Int from int;
cover TimeT from time_t;
cover String from char*;
extern func printf(String, ...);
extern func srand(Int);
extern func rand -> Int;
extern func time(TimeT);

func main {

	srand(time(null));
	printf("The answer is %d\n", rand);

}
