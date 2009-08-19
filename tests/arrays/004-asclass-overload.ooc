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

// [], []=, +, -, *, /, as, ==, !, ~

operator []  func(array: IntArray, index: Int) -> Int {
	array get(index)
}
	
operator []= func(array: IntArray, index: Int, value: Int) {
	array set(index, value)
}

/*

TODO: Mind cyclic references!!!

operator ! func(array: IntArray) {
	!!array
}
*/

main: func {
	
	max := 20
	array := new IntArray(max)
	
	if (array is IntArray)
	
	for(i: Int in 0..max) array[i] = i
	for(i: Int in 0..max) printf("array[%d] = %d\n", i, array[i])
	
}
