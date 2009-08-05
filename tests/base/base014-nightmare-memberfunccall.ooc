include stdio;

cover Int from int;
cover String from char*;
extern func printf(String, ...);

class Thinga {

	Int count;

	func getThis -> This {
		this.count = this.count + 1;
		return this;
	}

	func thingo {
		printf("Thingo bingo =) count = %d\n", this.count);
	}

}

func main {

	new Thinga.getThis().getThis().getThis().getThis().thingo();

}
