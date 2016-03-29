package shape;

import java.util.Random;

import material.Material;
import math.Normal;
import math.Point;
import math.Transformation;


public interface LightableShape extends Shape {

	public double getInverseArea();
	public Normal getNormal();
	public Material getMaterial();
	public Transformation getTransformation();
	public Point getRandomPoint(Random r);
	public boolean inShape(Point hitPoint);
}
