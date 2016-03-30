package shape;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
public class Sphere implements LightableShape {
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
		phi = Math.atan(hitPoint.y/hitPoint.x) + Math.PI/2;
		if (hitPoint.x < 0) {
			phi = phi + Math.PI;
		}
		u = phi/(Math.PI*2);
		theta = Math.acos(hitPoint.z);
		v = 1 - theta/(Math.PI);
		
		TextPoint txtPoint = new TextPoint(u,v);
		
		if (u > 1) {
			System.out.println("error u index to large.");
		}
		if (v > 1) {
			System.out.println("error v index to large.");
		}
		if (u < 0) {
			System.out.println("error u index to small.");
		}
		if (v < 0) {
			System.out.println("error v index to small.");
		}
		
		return txtPoint;
		
	}

	@Override
	public double getInverseArea() {
		Point test = new Point(1,1,1);
		test = this.transformation.transform(test);
		double area = Math.PI*(4/3)*(test.x*test.y*test.z);
		return area;
	}

	@Override
	public Normal getNormal(Point pPrime) {
		return pPrime.toNormal();
	}

	@Override
	public Material getMaterial() {
		return this.material;
	}

	@Override
	public Transformation getTransformation() {
		return this.transformation;
	}

	@Override
	public Point getRandomPoint() {
		Random r = new Random();
		double rndR = r.nextDouble();
		double rndAngle = (2*Math.PI)  * r.nextDouble();
		double rndAngle2 = (2*Math.PI)  * r.nextDouble();
		
		double randomX = Math.cos(rndAngle)*rndR;
		double randomY = Math.sin(rndAngle)*rndR;
		double randomZ = Math.sin(rndAngle2)*rndR;
		Point p = new Point(randomX,randomY,randomZ);
		p = this.transformation.transform(p);
		return p;
	}

	@Override
	public boolean inShape(Point hitPoint) {
		hitPoint = this.transformation.transformInverse(hitPoint);
		double x = hitPoint.x;
		double y = hitPoint.y;
		double z = hitPoint.z;
		if (Math.abs(x*x + y*y + z*z) < 1.0) {
			return true;
		} else {
			return false;
		}
	}
	
	
}
