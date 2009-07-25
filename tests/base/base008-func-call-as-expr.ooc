include stdlib, stdio, time;
extern func printf(String, ...);
extern func srand(Int);
extern func rand -> Int;
extern func time(time_t);

func main {

	srand(time(null));
	printf("The answer is %d\n", rand());

}
