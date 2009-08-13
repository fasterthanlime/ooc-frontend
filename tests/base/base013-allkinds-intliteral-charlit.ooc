main: func {

	a = 42 : Int
	b = 0xdeadbeef : Int
	c = 0c777 : Int
	d = 0b1011 : Int
	e = "abba zabba" : String
	f = 'c' : Char
	g = 3.14 : Float
	h = 10_000_000 : Int
	i = 3_000.32 : Float
	printf("(a, b, c, d, e, f, g, h, i) = (%d, %d, %d, %d, %s, %c, %f, %d, %f)\n",
			 a, b, c, d, e, f, g, h, i)
			 
}
