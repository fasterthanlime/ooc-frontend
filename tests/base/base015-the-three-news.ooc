Funky: class {

	new: func {
		printf("Created a Funky =)\n")
	}

}

accept: func (Funky f)

main: func {

	f = new Funky() : Funky // Fully explicit
	f2 = new Funky : Funky // No-parenthesis
	f3 = new : Funky // VariableDeclAssigned
	f4 : Funky
	f4 = new // Assignment
}
