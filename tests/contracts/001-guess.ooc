extern func scanf(String, ...)

func main {

	Int number;
	while true {
		printf("Give me a number between 0 and 100\n")
		scanf("%d", &number)
		check(number)
	}

}

func check(Int number) -> Bool {

	require (0 <= number <= 100)
	return (number == 42)

}

func rand -> Int {

	srand(ctime(null))
	return rand % 100;

	ensure

}
