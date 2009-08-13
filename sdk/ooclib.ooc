include stdlib, stdio, stdint, stdbool, memory, gc/gc;

Char: cover from char;
String: cover from Char*;
Pointer: cover from void*;
Int: cover from int;
UInt: cover from unsigned int;
Float: cover from float;
Double: cover from double;
LDouble: cover from long double;
Short: cover from short;
Long: cover from long;
LLong: cover from long long;
Void: cover from void;
//cover Func?
Size: cover from size_t;
Octet: cover from uint8_t;

printf: extern func (String, ...);
sprintf: extern func (String, String, ...);
println: func(str: String) {
	printf("%s\n", str)
}

/*
GC_calloc: func ~ (nmemb: Int, size: Size) -> Object {
	memsize = nmemb * size : Size;
	tmp = GC_malloc(memsize) : Object;
	memset(tmp, 0, memsize);
	return tmp;
}
*/
