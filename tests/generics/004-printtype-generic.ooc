Dog: class {
	bloh: func
}
Hound: class from Dog {
	i := 42
}

main: func {
	//printType(new Dog)
	//printType(new Hound)
	//printType('c')
	printType(i := 42)
	//printType(3.14)
	//printType(8.0)
	//printType(6.52)
}

printType: func <T> (arg: T) {
	printf("Class hierarchy = ")
	c := T
	while (c) {
		mess := "bytes"
		if(c size == 1) mess = "byte"
		printf("%s (%d %s)", c name, c size, mess)
		c = c super
		if(c) printf(" -> ")
	}
	printf("\n-----------------\n");
	if(T == Int class) {
		printf("It's an Int, and it's worth %d!\n", arg as Int)
	}
}
