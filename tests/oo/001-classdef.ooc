include stdio;
cover String from char*;
extern func printf(String, ...);

class Sayer {
	
	func say {
		printf("Hi, I'm a Sayer, just sayin'...\n");
	}
	
}

func main {
	
	Sayer s = new Sayer();
	s.say();
	
}
