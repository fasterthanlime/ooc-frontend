include stdio;
extern func printf(String, ...);
cover Int from int;
cover String from char*;

func main {
	call;
}

func call print "Hi, cruel world";
