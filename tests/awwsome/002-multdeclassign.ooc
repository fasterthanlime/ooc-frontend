include stdio;
cover String from char*;
extern func printf(String, ...);
cover Float from float;
cover Int from int;

func main {

	Float x = 1.0, y = 4.2, z = 3.7;
	printf("Assigned to values (%f, %f, %f)\n", x, y, z);

}
