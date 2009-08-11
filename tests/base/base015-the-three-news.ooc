include stdio
cover String from char*
cover Int from int
extern func printf(String, ...)

class Funky {

	func new {
		printf("Created a Funky =)\n")
	}

}

func accept(Funky f)

func main {

	Funky f = new Funky() // Fully explicit
	Funky f2 = new Funky // No-parenthesis
	Funky f3 = new // VariableDeclAssigned
	Funky f4
	f4 = new // Assignment
}
