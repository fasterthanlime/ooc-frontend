include stdio;

cover Int from int;
cover String from char*;

extern func printf(String, ...);

func main {

	new Dog("Dogbert").print;

}

class Animal {

	String name;
	func new(=name);

}

class Dog from Animal {

	func new(name) super(name);

	func print {
		printf("My name is %s\n", name);
	}

}
