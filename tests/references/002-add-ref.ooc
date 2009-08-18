main: func {

	number := 32
	add(&number, 3)
	printf("The answer is %d", number)

}

// we declare dst as Int@, which means receive an Int* and treat as (*dst)
add: func (dst: Int@, off: Int) {
	// in C: (*dst) += off
	dst += off
}
