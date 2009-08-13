include stdlib, stdio, stdint, stdbool, memory, gc/gc;

cover Char from char;
cover String from Char*;
cover Object from void*;
cover Int from int;
cover UInt from unsigned int;
cover Float from float;
cover Double from double;
cover LDouble from long double;
cover Short from short;
cover Long from long;
cover LLong from long long;
cover Void from void;
//cover Func?
cover Size from size_t;
cover Octet from uint8_t;

printf: extern func (String, ...);
echo: func(str: String) {
	printf("%s\n", str)
}

/*
func GC_calloc ~ (Int nmemb, Size size) -> Object {
	Size memsize = nmemb * size;
	Object tmp = GC_malloc(memsize);
	memset(tmp, 0, memsize);
	return tmp;
}
*/
