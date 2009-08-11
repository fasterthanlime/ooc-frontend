include stdio
extern func printf(String, ...)
cover String from char*
cover Int from int

func main {

	say("Alan", "Heya world")

}

func say(String name, String msg) {

	printf("%s says: %s\n", name, msg)

}
