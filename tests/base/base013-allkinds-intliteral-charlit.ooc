include stdio
cover Int from int
cover Char from char
cover String from char*
cover Float from double
extern func printf(String, ...)

func main {

	Int a = 42
	Int b = 0xdeadbeef
	Int c = 0c777
	Int d = 0b1011
	String e = "abba zabba"
	Char f = 'c'
	Float g = 3.14
	Int h = 10_000_000
	Float i = 3_000.32
	printf("(a, b, c, d, e, f, g, h, i) = (%d, %d, %d, %d, %s, %c, %f, %d, %f)\n", a, b, c, d, e, f, g, h, i)

}
