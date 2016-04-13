package shape;

import java.util.List;

import light.LightIntersection;
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
	public LightIntersection getRandomPoint(Point hitPoint);
	public boolean inShape(Point hitPoint);
	public TextPoint getUV(Point pPrime);
	public List<LightableShape> subdivide(int subdivisions);
	public Point getCenter();
}
