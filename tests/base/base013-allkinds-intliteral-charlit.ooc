include stdio;
cover Int from int;
cover Char from char;
cover String from char*;
extern func printf(String, ...);

func main {

	Int a = 42;
	Int b = 0xdeadbeef;
	Int c = 0c777;
	Int d = 0b1011;
	String e = "abba zabba";
	Char f = 'c';
	Float g = 3.14;
	Int h = 10_000_000;
	printf("(a, b, c, d, e, f, g, h) = (%d, %d, %d, %d, %s, %c, %f, %d)\n", a, b, c, d, e, f, g, h);

}
