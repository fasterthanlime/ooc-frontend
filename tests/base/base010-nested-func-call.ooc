include time
TimeT: cover from time_t
srand: extern func (Int)
rand: extern func -> Int
time: extern func (TimeT)

random: func (max : Int) -> Int {
	
	srand(time(null))
	return (rand() + 1) % max
	
}

main: func {

	printf("The answer is %d\n", random(random(random(random(random(random(2500)))))))

}
