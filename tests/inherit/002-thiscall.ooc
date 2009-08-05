include stdio;

cover String from char*;
extern func printf(String, ...);

func main {
	Dog fido = new;
	fido.sayName;
}

class Animal {

	String name;

	func sayName printf("Hi, my name is %s\n", name);

}

class Dog from Animal {

	func new {
		//this("Fido");
	}

	func new;

	func new#withName(=name);

}
