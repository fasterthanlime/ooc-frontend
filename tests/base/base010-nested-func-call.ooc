include time
extern func srand(Int)
extern func rand -> Int
extern func time(TimeT)
cover TimeT from time_t

func random(max : Int) -> Int {
	
	srand(time(null))
	return (rand + 1) % max
	
}

func main {

	printf("The answer is %d\n", random(random(random(random(random(random(2500)))))))

}
