include stdio;
extern func printf(String, ...);
cover Int from int;
cover String from char*;

func main {
	new Speaker.coall();
}

class Speaker {

	func call print "Hi, cruel world";
	func bah;
	func clo;
}
