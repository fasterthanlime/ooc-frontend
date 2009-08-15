include stdio;

Dog: class {

	name: String | private

	new: func (=name)

	sayName: func printf("My name is %s\n", name)
	barf: func printf("Woof!\n")

}

Point3f: class {

	x, y, z : Float | private, get, set

}

func main {

	new Dog("Dogbert") sayName(), barf();
	new Point3f x = 3.0f, y = 2.13f, z = 3.2f;
	new Dog name = "Fido";

}
