include stdio

cover Int from int
cover Char from char
cover String from Char*

extern func printf(String, ...)

func main {

	printf ("pi roughly equals %d\n", 3.14 as Int)

}
