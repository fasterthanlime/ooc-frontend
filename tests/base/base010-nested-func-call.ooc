include stdlib, stdio, time;
extern func printf(String, ...);
extern func srand(Int);
extern func rand -> Int;
extern func time(time_t);
cover Int from int;

func random(Int max) -> Int {
	
	srand(time(null));
	return rand() % max;
	
}

func main {

	printf("The answer is %d\n", random(random(random(random(random(random(2500)))))));

}
