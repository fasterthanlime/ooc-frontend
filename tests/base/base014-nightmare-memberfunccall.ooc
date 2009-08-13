class Thinga {

	count: Int

	func getThis -> This {
		this.count = this.count + 1
		return this
	}

	func thingo {
		printf("Thingo bingo =) count = %d\n", this.count)
	}

}

func main {

	new Thinga.getThis().getThis().getThis().getThis().thingo()

}
