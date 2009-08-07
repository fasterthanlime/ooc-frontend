include stdio;
extern func printf(String, ...);

cover String from char*;
cover Int from int {

	func max(Int other) -> Int {
		if(this > other) return this;
		return other;
	}

}

func main() {

	Int value = 24;
	printf("The greatest of 24 and 42 is %d\n", value.max(42));

}
