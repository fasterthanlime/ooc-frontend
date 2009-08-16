include stdio;
extern func printf(String, ...);

cover String from char*;
cover Int from int {

	func max(Int other) -> Int {
		if(this > other) return this;
		return other;
	}

}

main: func {

	printf("The greatest of 24 and 42 is %d\n", 24.max(42));

}
