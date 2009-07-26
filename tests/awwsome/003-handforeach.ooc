include stdio;
cover String from char*;
extern func printf(String, ...);
cover Float from float;

func main {

	Float (x, y, z) = (1.0, 4.2, 3.7);
	printf("Assigned to values (%f, %f, %f)\n", x, y, z);
	for(Float f: (x, y, z)) {
		printf("Printing value %f\n", f);
	}

}
