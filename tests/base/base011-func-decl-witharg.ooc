include stdio;
extern func printf(String, ...);
cover String from char*;

func main {

	say("Heya world");

}

func say(String msg) {

	printf("Says: %s\n", msg);

}
