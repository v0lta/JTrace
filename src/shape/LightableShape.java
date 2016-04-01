package shape;

import material.Material;
import math.Normal;
import math.Point;
import math.TextPoint;
import math.Transformation;


public interface LightableShape extends Shape {

	public double getInverseArea();
	public Normal getNormal(Point pPrime);
	public Material getMaterial();
	public Transformation getTransformation();
	public Point getRandomPoint(Point hitPoint);
	public boolean inShape(Point hitPoint);
	public TextPoint getUV(Point pPrime);
}
