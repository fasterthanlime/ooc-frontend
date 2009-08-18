Int: extern Int

IntArray: cover from Int* {
	
	new: func(size: SizeT) {
		return GC_calloc(sizeof(Int), size)
	}
	
}

main: func {
	
	max := 20
	array := new IntArray(max)
	for(i: Int in 0..max) array[i] = i
	for(i: Int in 0..max) printf("array[%d] = %d\n", i, array[i])
	
}
