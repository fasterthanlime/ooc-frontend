sqrt: extern func (Float);

Point3f: cover {

	x, y, z: Float

	length: func -> Float {
		return sqrt(x * x + y * y + z * z);
	}

}

main: func {

	point : Point3f
	point x = 3.0;
	point y = 1.2;
	point z = 5.5;
	printf("Point (%f, %f, %f), length = %f\n", point x, point y, point z, point length());

}
