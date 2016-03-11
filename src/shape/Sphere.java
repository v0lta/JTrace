package shape;

import java.util.ArrayList;
import java.util.List;

import math.Constants;
import math.Normal;
import math.Point;
import math.Ray;
import math.Transformation;
import math.Vector;
import math.Intersection;
import math.Color;


/**
 * Represents a three-dimensional {@link Sphere} with radius one and centered at
 * the origin, which is transformed by the given {@link Transformation}.
 * 
 * @author Niels Billen
 * @version 0.2
 */
public class Sphere implements Shape {
	public final Transformation transformation;
	public final Color color;
	public final double reflectivity;
	private int accessCount = 0;

	/**
	 * Creates a new unit {@link Sphere} at the origin, transformed by the given
	 * {@link Transformation}.
	 * 
	 * @param transformation
	 *            the transformation applied to this {@link Sphere}.
	 * @throws NullPointerException
	 *             when the transformation is null.
	 */
	public Sphere(Transformation transformation, Color color,double reflectivity) {
		if (transformation == null)
			throw new NullPointerException("the given origin is null!");
		if (color == null)
			throw new NullPointerException("the given color is null!");

		this.transformation = transformation;
		this.color = color;
		this.reflectivity = reflectivity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see shape.Shape#intersect(geometry3d.Ray3D)
	 */
	@Override
	public List<Intersection> intersect(Ray ray) {
		List<Intersection> hits = new ArrayList<Intersection>();
		Ray transformed = transformation.transformInverse(ray);
		
        if (Constants.compVisualization == true){
        	accessCount = accessCount + 1;
        }

		Vector o = transformed.origin.toVector();

		double a = transformed.direction.dot(transformed.direction);
		double b = 2.0 * (transformed.direction.dot(o));
		double c = o.dot(o) - 1.0;

		double d = b * b - 4.0 * a * c;

		if (d < 0)
			return hits;
		double dr = Math.sqrt(d);

		// numerically solve the equation a*t^2 + b * t + c = 0
		double q = 0;
		if (b < 0) {
			q = -0.5 * (b - dr);
		} else {
			q = -0.5 * (b + dr);
		}
		
		double t0 = q / a;
		double t1 = c / q;

		//hit points with t<0 are behind the camera.
		boolean hasInt = t0 >= Constants.epsilon || t1 >= Constants.epsilon;
		
		
		if (hasInt == true) {
			//found an intersection compute the hit point.
			Vector hitPoint;
			Point hitPntT;
			Normal hitNmlT;
			Color hitColor;
			//the hit point with the smaller t is closer to the camera.
			if (t0 < t1) {
				hitPoint = o.add(transformed.direction.scale(t0));
				hitPntT = this.transformation.transform(hitPoint.toPoint());
				hitNmlT = this.transformation.transformInverseTranspose(hitPoint.toNormal());
				hitColor = this.color;
				hits.add(new Intersection(hitPntT,hitNmlT,hitColor,this.reflectivity,this.accessCount));
				return hits;
				
				
			} else {
				hitPoint = o.add(transformed.direction.scale(t1));
				hitPntT = this.transformation.transform(hitPoint.toPoint());
				hitNmlT = this.transformation.transformInverseTranspose(hitPoint.toNormal());
				hitColor = this.color;
				hits.add(new Intersection(hitPntT,hitNmlT,hitColor,this.reflectivity,this.accessCount));
				return hits;
			}
		} else {
			// No intersection worth reporting
			return hits;
		}
	}
}
