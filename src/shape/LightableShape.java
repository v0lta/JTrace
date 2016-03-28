package shape;

import math.Normal;


public interface LightableShape extends Shape {

	public double getInverseArea();
	public Normal getNormal();
}
