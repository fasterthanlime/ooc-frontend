include stdio, memory, string, gc/gc;

extern func printf(String, ...);
extern func strdup(String) -> String;
extern func strlen(String) -> Int;
extern func GC_malloc(Int) -> void*;
extern func memcpy(...) -> void*;

cover Int from int;
cover Char from char;
cover String from Char* {
	
	func replace(Char oldie, Char kiddo) -> String {
		String copy = clone;
		for(Int i: 0..length) {
			if (copy[i] == oldie) copy[i] = kiddo;
		}
		return copy;
	}

	func length ->Int strlen(this);
	func clone -> String {
		String copy = GC_malloc(length);
		memcpy(copy, this, length);
		return copy;
	}

}

func main {

	printf("doogy-di-doo is not %s\n", "doogie-die-doo".replace('d', 'b'));

}
