include stdio, stdlib;
extern func printf(String, ...);
extern func sqrt(Float);

cover String from char*;
cover Float from float;
cover Point3f {

	Float x, y, z;
	func length -> Float {
		return sqrt(x * x + y * y + z * z)
	}

}

func main() {

	Point3f point;
	point.x = 3.0;
	point.y = 1.2;
	point.z = 5.5;
	printf("Point (%f, %f, %f), length = %f\n", point.x, point.y, point.z, point.length());

}
