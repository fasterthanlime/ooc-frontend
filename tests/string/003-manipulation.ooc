main: func -> Int {
	test = "A short list of long stories" : String
	printf("String startsWith: ")
	if (test startsWith("A short list")) {
		printf("OK\n")
	}
	else {
		printf("FAIL\n")
	}
}
