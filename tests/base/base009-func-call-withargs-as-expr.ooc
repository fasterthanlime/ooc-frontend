include time
cover TimeT from time_t
extern func srand(Int)
extern func rand -> Int
extern func time(TimeT)

func main {

	srand(time(null))
	printf("The answer is %d\n", rand())

}
