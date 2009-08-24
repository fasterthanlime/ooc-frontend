main: func -> Int {
	test = "A short list of long stories", start = "A short list" : String
	printf("String '%s' startsWith: '%s'? ", test, start)
	if (test startsWith(start)) {
		printf("OK\n")
	} else {
		printf("FAIL\n")
	}
}
