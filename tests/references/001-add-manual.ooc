main: func {

	number := 32
	add(&number, 3)
	printf("The answer is %d", number)

}

// receive a regular pointer to int, treat is as such (e.g. you must dereference yourself)
add: func (dst: Int*, off: Int) {
	// in C: (*dst) += off
	dst@ += off
}
