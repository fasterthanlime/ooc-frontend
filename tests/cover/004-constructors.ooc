include stdio, stdlib;
extern func printf(String, ...);
extern func sqrt(Float);

cover String from char*;
cover Float from float;
cover Point3f {

	Float x, y, z;

	func new(Float x, Float y, Float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	func length -> Float {
		return sqrt(x * x + y * y + z * z)
	}

}

func main() {

	Point3f point = new Point3f(3.0, 1.2, 5.5);
	printf("Point (%f, %f, %f), length = %f\n", point.x, point.y, point.z, point.length());

}
