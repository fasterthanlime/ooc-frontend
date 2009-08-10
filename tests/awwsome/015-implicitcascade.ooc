include stdio;

class Dog {

	String name;

	func(=name);

	func sayName printf("My name is %s\n", name);
	func barf printf("Woof!\n);

}

class Point3f {

	private Float x, y, z;

	property x {get, set}
	property y {get, set}
	property z {get, set}

}

func main {

	new Dog("Dogbert") {sayName; barf};
	new Point3f {x = 3.0f, y = 2.13f, z = 3.2f};
	new Dog {name = "Fido"};

}
