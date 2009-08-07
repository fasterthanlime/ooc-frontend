include stdio, memory, string;

extern func printf(String, ...);
extern func strdup(String) -> String;
extern func strlen(String) -> Int;

cover Int from int;
cover Char from char;
cover String from Char* {
	
	func replace(Char oldie, Char kiddo) -> String {
		String copy = clone(this);
		for(Int i: 0..length) {
			if (copy[i] == oldie) copy[i] = kiddo;
		}
		return copy;
	}

	func length ->Int strlen(this);
	func clone -> String strdup(this);

}

func main {

	printf("A dog is a not a %s\n", "dog".replace('o', 'a'));

}
