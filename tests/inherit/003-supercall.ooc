include stdio;

cover String from char*;
extern func printf(String, ...);

func main {
	new Dog.sayName;
}

class Animal {

	String name;

	func new~withName(=name);
	func sayName printf("Hi, my name is %s\n", name);

}

class Dog from Animal {

	func new {
		super("Fido");
	}

}
