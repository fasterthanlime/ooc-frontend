include stdio;

cover String from char*;
extern func printf(String, ...);

func main {
	Dog fido = new Dog("Fido");
	fido.sayName;
	fido.bark;
}

class Animal {

	String name;

	func sayName {
		printf("Hi, my name is %s\n", name);
	}

}

class Dog from Animal {

	func new(=name);
	
	func bark {
		printf("Woof, woof!\n");
	}

}
