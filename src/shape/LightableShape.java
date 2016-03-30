package shape;

import java.util.Random;

import material.Material;
import math.Normal;
import math.Point;
import math.Transformation;


public interface LightableShape extends Shape {

	public double getInverseArea();
	public Normal getNormal(Point pPrime);
	public Material getMaterial();
	public Transformation getTransformation();
	public Point getRandomPoint();
	public boolean inShape(Point hitPoint);
}
