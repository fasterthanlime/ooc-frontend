func main {

	Int number = 39
	add(@number, 3)
	printf("The answer is %d", number)

}

func add(Int* dst@, Int off) {
	dst += off
}
