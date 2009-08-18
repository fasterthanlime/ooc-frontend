include stdlib, stdio, stdint, stdbool, memory, gc/gc, string

strlen: extern func (String) -> SizeT

Char: cover from char
String: cover from Char* {
	
	length: func -> Int strlen(this)
	
}
Int: cover from int
Pointer: cover from void*
UInt: cover from unsigned int
Float: cover from float
Double: cover from double
LDouble: cover from long double
Short: cover from short
Long: cover from long
LLong: cover from long long
Void: cover from void
//cover Func?
SizeT: cover from size_t
Octet: cover from uint8_t

sizeof: extern func (...) -> SizeT
memcpy: extern func (Pointer, Pointer, SizeT)
scanf: extern func (String, ...)
printf: extern func (String, ...)
sprintf: extern func (String, String, ...)
println: func (str: String) {
	printf("%s\n", str)
}
println: func ~empty {
	printf("\n")
}

GC_malloc: extern func (size: SizeT) -> Pointer
GC_realloc: extern func (ptr: Pointer, size: SizeT) -> Pointer
GC_calloc: func ~ (nmemb: SizeT, size: SizeT) -> Pointer {
	return GC_malloc(nmemb * size)
}
