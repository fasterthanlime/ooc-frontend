include stdlib, stdio, stdint, stdbool, memory, gc/gc, string

strlen: extern func (String) -> SizeT

Char: cover from char {

	toInt: func -> Int {
		if ((this >= 48) && (this <= 57)) {
			return (this - 48);
		}
		return -1;
	}
	
}

String: cover from Char* {
	
	length: func -> Int strlen(this)
	
}

Pointer: cover from void*
Int: cover from int
UInt: cover from unsigned int
Float: cover from float
Double: cover from double
LDouble: cover from long double
Short: cover from short
Long: cover from long
ULong: cover from unsigned long
LLong: cover from long long
Void: cover from void
Bool: cover from bool
Func: cover from Pointer
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

gc_malloc: extern(GC_malloc) func (size: SizeT) -> Pointer
gc_realloc: extern(GC_realloc) func (ptr: Pointer, size: SizeT) -> Pointer
gc_calloc: func (nmemb: SizeT, size: SizeT) -> Pointer {
	gc_malloc(nmemb * size)
}
