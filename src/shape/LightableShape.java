package shape;

import material.Material;
import math.Normal;


public interface LightableShape extends Shape {

	public double getInverseArea();
	public Normal getNormal();
	public Material getMaterial();
}
