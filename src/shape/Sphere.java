package shape;

import java.util.ArrayList;
import java.util.List;

import material.Material;
import math.Constants;
import math.Normal;
import math.Point;
import math.Ray;
import math.TextPoint;
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
	public final Material material;
	public final double reflectivity;


	/**
	 * Creates a new unit {@link Sphere} at the origin, transformed by the given
	 * {@link Transformation}.
	 * 
	 * @param transformation
	 *          the transformation applied to this {@link Sphere}.
	 * @param material
	 * 			defines the color of the spheres surface.
	 * @param reflectivity 
	 * 			determines how well the sphere is going to reflect the light.
	 * @throws NullPointerException
	 *             when the transformation is null.
	 */
	public Sphere(Transformation transformation, Material material, double reflectivity) {

		this.transformation = transformation;
		this.material = material;
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
		
        if (Constants.compVisualization){
        	ray.countIntersection();
        }
        
        Ray transformed = transformation.transformInverse(ray);
		
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
				hitColor = this.material.getColor(getUV(hitPoint.toPoint()));
				hits.add(new Intersection(hitPntT,hitNmlT,hitColor,this.reflectivity));
				return hits;
				
				
			} else {
				hitPoint = o.add(transformed.direction.scale(t1));
				hitPntT = this.transformation.transform(hitPoint.toPoint());
				hitNmlT = this.transformation.transformInverseTranspose(hitPoint.toNormal());
				hitColor = this.material.getColor(getUV(hitPoint.toPoint()));
				hits.add(new Intersection(hitPntT,hitNmlT,hitColor,this.reflectivity));
				return hits;
			}
		} else {
			// No intersection worth reporting
			return hits;
		}
	}
	
	private TextPoint getUV(Point hitPoint) {
		double u,v, theta, phi;
		phi = Math.atan(hitPoint.x/hitPoint.z);
		u = phi/(2*Math.PI);
		theta = Math.acos(hitPoint.y);
		v = 1.0 - theta/Math.PI;
		
		TextPoint txtPoint = new TextPoint(u,v);
		return txtPoint;
		
	}
	
	
}
