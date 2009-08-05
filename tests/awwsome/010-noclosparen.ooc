include stdlib, stdio, time;

cover Int from int;
cover String from char*;
cover TimeT from time_t;

extern func printf(String, ...);
extern func srand(Int);
extern func rand -> Int;
extern func time(TimeT);

func random(Int max) -> Int {
	srand(time(null));
	return rand % max;
}

func main {
	int i = random(random(random(random(random(random(240000;
	printf("Got number %d\n", i);
}
