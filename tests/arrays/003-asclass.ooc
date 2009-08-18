Int: extern Int

IntArray: class {
	
	size: SizeT
	data: Int*
	
	new: func(=size) {
		data = GC_calloc(sizeof(Int), size)
	}
	
	get: func(index: Int) -> Int {
		data[index]
	}
	
	set: func(index: Int, value: Int) {
		data[index] = value
	}
	
}

main: func {
	
	max := 20
	array := new IntArray(max)
	for(i: Int in 0..max) array set(i, i)
	for(i: Int in 0..max) printf("array[%d] = %d\n", i, array get(i))
	
}
